package com.sparta.modulebatch.batch;

import com.sparta.modulecommon.schedule.repository.ScheduleRepository;
import com.sparta.modulecommon.settlement.dto.SettlementResult;
import com.sparta.modulecommon.settlement.entity.Result;
import com.sparta.modulecommon.settlement.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class settlementBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ScheduleRepository scheduleRepository;
    private final ResultRepository resultRepository;

    @Bean
    public Job settlementJop() {
        System.out.println("settlement Job start");

        return new JobBuilder("settlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(settlementFirstStep())
                .build();
    }

    @Bean
    public Step settlementFirstStep() {
        return new StepBuilder("settlementFirstStep", jobRepository)
                .<SettlementResult, Result>chunk(100, platformTransactionManager)
                .reader(settlementReader())
                .processor(settlementProcessor())
                .writer(settlementWriter())
                .faultTolerant() // 내결함성 설정
                .skipLimit(5) // 스킵할 최대 항목 수
                .skip(Exception.class) // 특정 예외에 대해 스킵
                .retryLimit(3) // 재시도 횟수 설정
                .retry(Exception.class) // 특정 예외에 대해 재시도
                .build();
    }

    @Bean
    public RepositoryItemReader<SettlementResult> settlementReader() {
        LocalDate now = LocalDate.now().minusDays(1);
        LocalDateTime startDateTime = now.with(TemporalAdjusters.firstDayOfMonth()).atTime(LocalTime.MIN);
        LocalDateTime endDateTime = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        return new RepositoryItemReaderBuilder<SettlementResult>()
                .name("settlementReader")
                .pageSize(100)
                .methodName("findAllByTimeslotIdAndRequiredCoupon")
                .repository(scheduleRepository)
                .arguments(startDateTime, endDateTime)
                .sorts(Map.of("c.id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<SettlementResult, Result> settlementProcessor() {
        return new ItemProcessor<SettlementResult, Result>() {

            @Override
            public Result process(SettlementResult item) {
                // 수수료 5%
                return Result.of(item.getCenterId(), item.getSumOfCoupon() * 1000 * 0.95);
            }
        };
    }

    @Bean
    public ItemWriter<Result> settlementWriter() {
        return items -> items.forEach(resultRepository::save);
    }
}
