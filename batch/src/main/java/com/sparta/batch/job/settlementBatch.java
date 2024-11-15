package com.sparta.batch.job;

import com.sparta.batch.dto.SettlementResult;
import com.sparta.batch.entity.Settlement;
import com.sparta.batch.util.SettlementChunkListener;
import com.sparta.batch.util.SettlementPartitioner;
import com.sparta.batch.util.SettlementStepExecutionListener;
import com.sparta.batch.repository.HistoryRepository;
import com.sparta.batch.repository.SettlementRepository;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class settlementBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final HistoryRepository historyRepository;
    private final SettlementRepository settlementRepository;

    @Bean
    public Job settlementJob() {
        return new JobBuilder("settlementJob", jobRepository)
                .start(settlementMasterStep())
                .build();
    }

    @Bean
    public Step settlementMasterStep() {
        return new StepBuilder("settlementMasterStep", jobRepository)
                .partitioner("settlementSlaveStep", settlementPartitioner())
                .step(settlementSlaveStep())
                .partitionHandler(settlementPartitionHandler())
                .build();
    }

    @Bean
    public Partitioner settlementPartitioner() {
        LocalDate now = LocalDate.now().minusDays(1);
        LocalDateTime startDateTime = now.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        LocalDateTime endDateTime = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        return new SettlementPartitioner(startDateTime, endDateTime);
    }

    @Bean
    public PartitionHandler settlementPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(4);
        handler.setTaskExecutor(settlementTaskExecutor());
        handler.setStep(settlementSlaveStep());
        return handler;
    }

    @Bean
    public Step settlementSlaveStep() {
        return new StepBuilder("settlementSlaveStep", jobRepository)
                .<SettlementResult, Future<Settlement>>chunk(1000, platformTransactionManager)
                .reader(settlementReader(null, null))
                .processor(asyncSettlementProcessor())
                .writer(asyncSettlementWriter())
                .faultTolerant()
                .retry(DataAccessException.class)
                .retry(TransientDataAccessException.class)
                .retryLimit(3)
                .skip(Exception.class)
                .skipLimit(10)
                .noSkip(IllegalArgumentException.class)
                .noSkip(NullPointerException.class)
                .listener(new SettlementChunkListener())
                .listener(new SettlementStepExecutionListener())
                .taskExecutor(settlementTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor settlementTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("settlement-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    public RepositoryItemReader<SettlementResult> settlementReader(
            @Value("#{stepExecutionContext[startTime]}") LocalDateTime startTime,
            @Value("#{stepExecutionContext[endTime]}") LocalDateTime endTime) {
        return new RepositoryItemReaderBuilder<SettlementResult>()
                .name("settlementReader")
                .pageSize(1000)
                .methodName("findAllCalculated")
                .repository(historyRepository)
                .arguments(startTime, endTime)
                .sorts(Map.of("h.centerId", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public AsyncItemProcessor<SettlementResult, Settlement> asyncSettlementProcessor() {
        AsyncItemProcessor<SettlementResult, Settlement> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(settlementProcessor());
        asyncItemProcessor.setTaskExecutor(settlementTaskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<SettlementResult, Settlement> settlementProcessor() {
        return item -> {
            try {
                return Settlement.of(item);
            } catch (Exception e) {
                log.error("Error processing settlement item: {}", item, e);
                throw e;
            }
        };
    }

    @Bean
    public AsyncItemWriter<Settlement> asyncSettlementWriter() {
        AsyncItemWriter<Settlement> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(settlementWriter());
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<Settlement> settlementWriter() {
        return items -> {
            try {
                settlementRepository.saveAll(items);
            } catch (Exception e) {
                log.error("Error writing settlement items: {}", items, e);
                throw e;
            }
        };
    }
}
