package com.sparta.modulebatch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class HistoryStepExecutionListener implements StepExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(HistoryStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("History Step Started: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("History Step Completed: {} (Read: {}, Written: {}, Skipped: {})",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount());
        return ExitStatus.COMPLETED;
    }
}