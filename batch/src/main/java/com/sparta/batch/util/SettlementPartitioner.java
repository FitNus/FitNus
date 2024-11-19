package com.sparta.batch.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SettlementPartitioner implements Partitioner {

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();

        long daysBetween = ChronoUnit.DAYS.between(startDateTime, endDateTime);
        long daysPerPartition = (daysBetween + gridSize - 1) / gridSize;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();

            LocalDateTime partitionStartTime = startDateTime.plusDays(i * daysPerPartition);
            LocalDateTime partitionEndTime = i == gridSize - 1 ?
                    endDateTime :
                    startDateTime.plusDays((i + 1) * daysPerPartition);

            context.put("startTime", partitionStartTime);
            context.put("endTime", partitionEndTime);
            context.putInt("partition", i);

            result.put("partition" + i, context);

            log.info("Created partition {}: startTime={}, endTime={}",
                    i, partitionStartTime, partitionEndTime);
        }

        return result;
    }
}