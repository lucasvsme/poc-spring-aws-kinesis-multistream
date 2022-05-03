package com.example.streaming;

import com.example.event.EventConsumerFactory;
import com.example.streaming.configuration.StreamProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.processor.ShardRecordProcessor;

@Configuration
public class CourseStreamConfiguration {

    private static final String STREAM_ID = "CourseStream";
    public static final String STREAM_PROPERTIES = STREAM_ID + "Properties";
    private static final String STREAM_PROCESSOR = STREAM_ID + "Processor";
    private static final String STREAM_SCHEDULER = STREAM_ID + "Scheduler";

    @Bean(STREAM_PROPERTIES)
    StreamProperties properties(Environment environment) {
        return new StreamProperties(
            new StreamProperties.Id(STREAM_ID),
            new StreamProperties.Stream(environment.getRequiredProperty("streams.course.name", String.class)),
            new StreamProperties.Application(environment.getRequiredProperty("streams.course.application", String.class))
        );
    }

    @Bean(STREAM_SCHEDULER)
    Scheduler scheduler(ApplicationContext context, @Qualifier(STREAM_PROPERTIES) StreamProperties properties) {
        return new EventStreamScheduler(context, properties);
    }

    @Bean(STREAM_PROCESSOR)
    ShardRecordProcessor processor(EventConsumerFactory eventConsumerFactory, ObjectMapper objectMapper) {
        return new EventStreamProcessor(eventConsumerFactory, objectMapper);
    }
}
