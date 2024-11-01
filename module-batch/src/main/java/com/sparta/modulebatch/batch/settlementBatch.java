package com.sparta.modulebatch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class settlementBatch {
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager platformTransactionManager;

//    @Bean
//    public Job settlementJop() {
//        System.out.println("settlement Job start");
//
//        return new JobBuilder("settlementJob", jobRepository)
//                .start(settlementFirstStep())
//                .build();
//    }
//
//    @Bean
//    public Step sttlementFirstStep() {
//        return new StepBuilder("settlementFirstStep", jobRepository)
//                .<Schedule, Result>chunk(5000, platformTransactionManager)
//                .reader()
//                .processor()
//                .writer()
//                .build();
//    }
}
