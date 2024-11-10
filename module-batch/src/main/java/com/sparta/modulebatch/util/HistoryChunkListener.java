package com.sparta.modulebatch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class HistoryChunkListener implements ChunkListener {
    private static final Logger log = LoggerFactory.getLogger(HistoryChunkListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
        log.debug("Processing new chunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.debug("Chunk processing completed");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Chunk processing failed: {}",
                context.getStepContext().getStepExecution().getFailureExceptions());
    }
}