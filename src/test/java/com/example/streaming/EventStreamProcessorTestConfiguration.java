package com.example.streaming;

import com.example.event.EventConsumer;
import com.example.event.EventConsumerFactory;
import com.example.streaming.configuration.StreamConsumerFactory;
import com.example.streaming.configuration.StreamProcessor;
import com.example.streaming.consumers.CourseCreatedConsumer;
import com.example.streaming.consumers.StudentCreatedConsumer;
import com.example.streaming.consumers.StudentEnrolledConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStreamProcessorTestConfiguration {

    @Bean("CourseCreatedConsumer")
    EventConsumer courseCreatedConsumer() {
        return new CourseCreatedConsumer();
    }

    @Bean("StudentEnrolledConsumer")
    EventConsumer studentEnrolledConsumer() {
        return new StudentEnrolledConsumer();
    }

    @Bean("StudentCreatedConsumer")
    EventConsumer studentCreatedConsumer() {
        return new StudentCreatedConsumer();
    }

    @Bean
    EventConsumerFactory eventConsumerFactory(ApplicationContext applicationContext) {
        return new StreamConsumerFactory(applicationContext);
    }

    @Bean
    StreamProcessor processor(EventConsumerFactory eventConsumerFactory, ObjectMapper objectMapper) {
        return new EventStreamProcessor(eventConsumerFactory, objectMapper);
    }
}
