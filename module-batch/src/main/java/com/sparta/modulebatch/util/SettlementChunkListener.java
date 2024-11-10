package com.sparta.modulebatch.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class SettlementChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Starting chunk processing for step: {}",
                context.getStepContext().getStepName());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("Completed chunk processing for step: {} with {} items processed",
                context.getStepContext().getStepName(),
                context.getStepContext().getStepExecution().getWriteCount());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Error occurred during chunk processing for step: {}",
                context.getStepContext().getStepName());
    }
}