package com.sparta.modulecommon.ssenotification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    @KafkaListener(
        topics = "notification", groupId = "invent"
    )
    public void consumer(String message) {
        log.info("Received message: {}", message);
    }
}