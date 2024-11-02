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
                .<SettlementResult, Result>chunk(10, platformTransactionManager)
                .reader(settlementReader())
                .processor(settlementProcessor())
                .writer(settlementWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<SettlementResult> settlementReader() {
        return new RepositoryItemReaderBuilder<SettlementResult>()
                .name("settlementReader")
                .pageSize(10)
                .methodName("findAllByTimeslotIdAndRequiredCoupon")
                .repository(scheduleRepository)
                .sorts(Map.of("c.id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<SettlementResult, Result> settlementProcessor() {
        return new ItemProcessor<SettlementResult, Result>() {

            @Override
            public Result process(SettlementResult item) throws Exception {
                return Result.of(item);
            }
        };
    }

    @Bean
    public ItemWriter<Result> settlementWriter() {
        return items -> items.forEach(resultRepository::save);
    }
}
