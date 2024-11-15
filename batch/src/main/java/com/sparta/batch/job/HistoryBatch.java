package com.sparta.batch.job;

import com.sparta.batch.dto.HistoryInfo;
import com.sparta.batch.entity.History;
import com.sparta.batch.repository.HistoryRepository;
import com.sparta.batch.util.HistoryChunkListener;
import com.sparta.batch.util.HistoryPartitioner;
import com.sparta.batch.util.HistoryStepExecutionListener;
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
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HistoryBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
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
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to create history slave step", e);
        }

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

//    @Bean
//    @StepScope
//    public RepositoryItemReader<HistoryInfo> historyReader(
//            @Value("#{stepExecutionContext[startTime]}") LocalDateTime startTime,
//            @Value("#{stepExecutionContext[endTime]}") LocalDateTime endTime) {
//        return new RepositoryItemReaderBuilder<HistoryInfo>()
//                .name("historyReader")
//                .pageSize(1000)
//                .methodName("findAllByStartDateBetween")
//                .repository(scheduleRepository)
//                .arguments(startTime, endTime)
//                .sorts(Map.of("s.startTime", Sort.Direction.ASC))
//                .build();
//    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<HistoryInfo> historyReader(
            @Value("#{stepExecutionContext[startTime]}") LocalDateTime startTime,
            @Value("#{stepExecutionContext[endTime]}") LocalDateTime endTime) throws Exception {

        return new JdbcPagingItemReaderBuilder<HistoryInfo>()
                .name("historyReader")
                .dataSource(dataSource)
                .queryProvider(createQueryProvider())
                .parameterValues(createParameters(startTime, endTime))
                .pageSize(1000)
                .rowMapper(new HistoryInfoRowMapper())
                .build();
    }

    @Bean
    @StepScope
    public PagingQueryProvider createQueryProvider() {
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        queryProvider.setSelectClause("""
                c.id as center_id,
                s.user_id,
                u.nickname,
                s.schedule_name,
                s.start_time,
                s.end_time,
                s.required_coupon
                """);

        queryProvider.setFromClause("""
                from schedule s
                join timeslot t on s.timeslot_id = t.id
                join fitness f on t.fitness_id = f.id
                join center c on f.center_id = c.id
                join user u on s.user_id = u.id
                """);

        queryProvider.setWhereClause("""
                where s.start_time >= :startTime
                and s.start_time <= :endTime
                and s.club_id is null
                """);

        // 정렬 키 설정 (필수)
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("start_time", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }

    private Map<String, Object> createParameters(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
        return parameters;
    }

    @Component
    public static class HistoryInfoRowMapper implements RowMapper<HistoryInfo> {
        @Override
        public HistoryInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new HistoryInfo(
                    rs.getLong("center_id"),
                    rs.getLong("user_id"),
                    rs.getString("centerName"),
                    rs.getString("nickname"),
                    rs.getString("schedule_name"),
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getTimestamp("end_time").toLocalDateTime(),
                    rs.getInt("required_coupon")
            );
        }
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
