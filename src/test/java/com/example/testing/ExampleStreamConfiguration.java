package com.example.testing;

import com.example.streaming.configuration.StreamProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.processor.ShardRecordProcessor;

@Configuration
public class ExampleStreamConfiguration {

    private static final String STREAM_ID = "ExampleStream";
    private static final String STREAM_PROPERTIES = STREAM_ID + "Properties";
    private static final String STREAM_PROCESSOR = STREAM_ID + "Processor";
    private static final String STREAM_SCHEDULER = STREAM_ID + "Scheduler";

    @Bean(STREAM_PROPERTIES)
    StreamProperties properties(Environment environment) {
        return new StreamProperties(
            new StreamProperties.Id(STREAM_ID),
            new StreamProperties.Stream(environment.getRequiredProperty("streams.example.name", String.class)),
            new StreamProperties.Application(environment.getRequiredProperty("streams.example.application", String.class))
        );
    }

    @Bean(STREAM_SCHEDULER)
    Scheduler scheduler(ApplicationContext context, @Qualifier(STREAM_PROPERTIES) StreamProperties properties) {
        return new ExampleStreamScheduler(context, properties);
    }

    @Bean(STREAM_PROCESSOR)
    ShardRecordProcessor processor() {
        return new ExampleStreamProcessor();
    }
}
