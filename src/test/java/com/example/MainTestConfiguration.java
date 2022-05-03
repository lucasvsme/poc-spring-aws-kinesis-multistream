package com.example;

import com.example.course.CourseService;
import com.example.course.CourseServiceDefault;
import com.example.enrollment.EnrollmentService;
import com.example.enrollment.EnrollmentServiceDefault;
import com.example.event.EventConsumer;
import com.example.event.EventConsumerFactory;
import com.example.event.EventPublisher;
import com.example.streaming.CourseStreamConfiguration;
import com.example.streaming.EnrollmentStreamConfiguration;
import com.example.streaming.StudentStreamConfiguration;
import com.example.streaming.configuration.StreamConsumerFactory;
import com.example.streaming.configuration.StreamProcessorFactory;
import com.example.streaming.configuration.StreamProperties;
import com.example.streaming.configuration.StreamPublisher;
import com.example.streaming.consumers.CourseCreatedConsumer;
import com.example.streaming.consumers.StudentCreatedConsumer;
import com.example.streaming.consumers.StudentEnrolledConsumer;
import com.example.student.StudentService;
import com.example.student.StudentServiceDefault;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Configuration
@PropertySource("classpath:application.properties")
public class MainTestConfiguration {

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
    EventPublisher eventPublisher(ObjectMapper objectMapper, KinesisAsyncClient kinesisAsyncClient) {
        return new StreamPublisher(objectMapper, kinesisAsyncClient);
    }

    @Bean
    EventConsumerFactory eventConsumerFactory(ApplicationContext applicationContext) {
        return new StreamConsumerFactory(applicationContext);
    }

    @Bean
    ShardRecordProcessorFactory shardRecordProcessorFactory(ApplicationContext applicationContext) {
        return new StreamProcessorFactory(applicationContext);
    }

    @Bean
    CourseService courseService(@Qualifier(CourseStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                EventPublisher eventPublisher) {
        return new CourseServiceDefault(streamProperties, eventPublisher);
    }

    @Bean
    EnrollmentService enrollmentService(@Qualifier(EnrollmentStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                        EventPublisher eventPublisher) {
        return new EnrollmentServiceDefault(streamProperties, eventPublisher);
    }

    @Bean
    StudentService studentService(@Qualifier(StudentStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                  EventPublisher eventPublisher) {
        return new StudentServiceDefault(streamProperties, eventPublisher);
    }
}
