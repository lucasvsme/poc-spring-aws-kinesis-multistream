package com.example.streaming.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Configuration
public class StreamProcessorFactoryTestConfiguration {

    @Bean
    ShardRecordProcessorFactory shardRecordProcessorFactory(ApplicationContext applicationContext) {
        return new StreamProcessorFactory(applicationContext);
    }
}
