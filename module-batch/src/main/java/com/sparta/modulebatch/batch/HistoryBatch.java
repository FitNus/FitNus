package com.sparta.modulebatch.batch;

import com.sparta.modulebatch.util.HistoryChunkListener;
import com.sparta.modulebatch.util.HistoryPartitioner;
import com.sparta.modulebatch.util.HistoryStepExecutionListener;
import com.sparta.modulecommon.center.dto.HistoryInfo;
import com.sparta.modulecommon.center.entity.History;
import com.sparta.modulecommon.center.repository.HistoryRepository;
import com.sparta.modulecommon.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HistoryBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ScheduleRepository scheduleRepository;
    private final HistoryRepository historyRepository;

    @Bean
    public Job historyJob() {
        return new JobBuilder("historyJob", jobRepository)
                .start(historyMasterStep())
                .build();
    }

    @Bean
    public Step historyMasterStep() {
        return new StepBuilder("historyMasterStep", jobRepository)
                .partitioner("historySlaveStep", historyPartitioner())
                .step(historySlaveStep())
                .partitionHandler(historyPartitionHandler())
                .build();
    }

    @Bean
    public Partitioner historyPartitioner() {
        LocalDate now = LocalDate.now().minusDays(1);
        return new HistoryPartitioner(
                now.atTime(LocalTime.MIN),
                now.atTime(LocalTime.MAX));
    }

    @Bean
    public PartitionHandler historyPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(4);
        handler.setTaskExecutor(historyTaskExecutor());
        handler.setStep(historySlaveStep());
        return handler;
    }

    @Bean
    public Step historySlaveStep() {
        return new StepBuilder("historyWorkerStep", jobRepository)
                .<HistoryInfo, Future<History>>chunk(1000, platformTransactionManager)
                .reader(historyReader(null, null))
                .processor(asyncHistoryProcessor())
                .writer(asyncHistoryWriter())
                .faultTolerant()
                .retry(DataAccessResourceFailureException.class)
                .retry(TransientDataAccessException.class)
                .retryLimit(3)
                .skip(Exception.class)
                .skipLimit(10)
                .noSkip(IllegalArgumentException.class)
                .noSkip(NullPointerException.class)
                .listener(new HistoryChunkListener())
                .listener(new HistoryStepExecutionListener())
                .taskExecutor(historyTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor historyTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("history-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    public RepositoryItemReader<HistoryInfo> historyReader(
            @Value("#{stepExecutionContext[startTime]}") LocalDateTime startTime,
            @Value("#{stepExecutionContext[endTime]}") LocalDateTime endTime) {
        return new RepositoryItemReaderBuilder<HistoryInfo>()
                .name("historyReader")
                .pageSize(1000)
                .methodName("findAll")
                .repository(scheduleRepository)
                .arguments(startTime, endTime)
                .sorts(Map.of("s.startTime", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public AsyncItemProcessor<HistoryInfo, History> asyncHistoryProcessor() {
        AsyncItemProcessor<HistoryInfo, History> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(historyProcessor());
        asyncItemProcessor.setTaskExecutor(historyTaskExecutor());
        return asyncItemProcessor;
    }


    @Bean
    public ItemProcessor<HistoryInfo, History> historyProcessor() {
        return item -> {
            try {
                return History.of(item);
            } catch (Exception e) {
                log.error("Error processing history item: {}", item, e);
                throw e;
            }
        };
    }

    @Bean
    public AsyncItemWriter<History> asyncHistoryWriter() {
        AsyncItemWriter<History> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(historyWriter());
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<History> historyWriter() {
        return items -> {
            try {
                historyRepository.saveAll(items);
            } catch (Exception e) {
                log.error("Error writing history items: {}", items, e);
                throw e;
            }
        };
    }
}
