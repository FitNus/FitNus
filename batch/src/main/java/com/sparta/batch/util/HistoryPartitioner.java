package com.sparta.batch.util;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HistoryPartitioner implements Partitioner {
    
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long totalMinutes = java.time.Duration.between(startDateTime, endDateTime).toMinutes();
        long minutesPerPartition = totalMinutes / gridSize;

        Map<String, ExecutionContext> partitions = new HashMap<>();

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();

            LocalDateTime partitionStartTime = startDateTime.plusMinutes(i * minutesPerPartition);
            LocalDateTime partitionEndTime = i == gridSize - 1
                    ? endDateTime
                    : startDateTime.plusMinutes((i + 1) * minutesPerPartition);

            context.put("startTime", partitionStartTime);
            context.put("endTime", partitionEndTime);
            context.put("partition", i);

            partitions.put("partition" + i, context);
        }

        return partitions;
    }
}
