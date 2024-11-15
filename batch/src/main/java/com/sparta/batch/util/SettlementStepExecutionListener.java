package com.sparta.batch.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class SettlementStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Starting step execution: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Completed step execution: {} with status: {}",
                stepExecution.getStepName(),
                stepExecution.getExitStatus());
        return stepExecution.getExitStatus();
    }
}