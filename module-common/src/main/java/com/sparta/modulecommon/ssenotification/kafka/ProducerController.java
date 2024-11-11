package com.sparta.modulecommon.ssenotification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProducerController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/produce")
    public void produceKafkaTopic(
        @RequestParam(name = "topic") String topic,
        @RequestParam(name = "message") String message) {

        kafkaTemplate.send(topic, message);
        log.info("Published to topic: {} with message: {}",topic, message);
    }

}