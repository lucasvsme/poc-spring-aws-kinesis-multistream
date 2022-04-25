package com.example.streaming.configuration;

import com.example.event.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@Configuration
public class StreamPublisherTestConfiguration {

    @Bean
    EventPublisher eventPublisher(ObjectMapper objectMapper, KinesisAsyncClient kinesisAsyncClient) {
        return new StreamPublisher(objectMapper, kinesisAsyncClient);
    }
}
