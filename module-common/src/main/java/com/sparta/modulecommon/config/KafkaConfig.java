package com.sparta.modulecommon.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String KAFKA_ADDRESS;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> myconfig = new HashMap<>();
        myconfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_ADDRESS);
        myconfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        myconfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 안정성 및 재시도 설정
        myconfig.put(ProducerConfig.ACKS_CONFIG, "all"); // 메시지 손실 방지
        myconfig.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수
        myconfig.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // 재시도 간격 설정
        myconfig.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "org.apache.kafka.clients.producer.RoundRobinPartitioner");

        return new DefaultKafkaProducerFactory<>(myconfig);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> myConfig = new HashMap<>();
        myConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_ADDRESS);
        myConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        myConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        myConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 신뢰할 패키지 설정
        myConfig.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap"); // 기본 타입 설정
        myConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        return new DefaultKafkaConsumerFactory<>(myConfig);
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auctionBidTopic() {
        return TopicBuilder.name("auction-bids")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auctionResultTopic() {
        return TopicBuilder.name("auction-results")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 파티션 수에 맞게 조정
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000); // 메시지 폴링 타임아웃 조정 (필요시 조정 가능)
        return factory;
    }

}