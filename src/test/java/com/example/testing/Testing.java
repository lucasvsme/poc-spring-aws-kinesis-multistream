package com.example.testing;

import com.example.course.CourseCreated;
import com.example.enrollment.StudentEnrolled;
import com.example.event.Event;
import com.example.event.EventPublisher;
import com.example.streaming.configuration.StreamProperties;
import com.example.student.StudentCreated;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

public final class Testing {

    private static void mockEventPublished(EventPublisher eventPublisher,
                                           StreamProperties streamProperties,
                                           String eventType) {
        Mockito.doNothing()
            .when(eventPublisher)
            .publish(Mockito.argThat(request -> {
                final var sameStreamName = request.streamName().equals(streamProperties.stream().value());
                final var partitionKeyWasDefined = request.partitionKey() != null;
                final var sameEventType = request.event().type().equals(eventType);

                return sameStreamName && partitionKeyWasDefined && sameEventType;
            }));
    }

    public static void mockCourseCreatedEventPublished(EventPublisher eventPublisher,
                                                       StreamProperties streamProperties) {
        Testing.mockEventPublished(eventPublisher, streamProperties, CourseCreated.TYPE);
    }

    public static void mockStudentCreatedEventPublished(EventPublisher eventPublisher,
                                                        StreamProperties streamProperties) {
        Testing.mockEventPublished(eventPublisher, streamProperties, StudentCreated.TYPE);
    }

    public static void mockStudentEnrolledEventPublished(EventPublisher eventPublisher,
                                                         StreamProperties streamProperties) {
        Testing.mockEventPublished(eventPublisher, streamProperties, StudentEnrolled.TYPE);
    }

    private static void verifyEventPublished(EventPublisher eventPublisher,
                                             Object partitionKey,
                                             String eventType) {
        Mockito.verify(eventPublisher, Mockito.times(1))
            .publish(Mockito.argThat(request -> {
                final var samePartitionKey = request.partitionKey().equals(partitionKey.toString());
                final var sameEventType = request.event().type().equals(eventType);

                return samePartitionKey && sameEventType;
            }));
    }

    public static void verifyCourseCreatedEventPublished(EventPublisher eventPublisher,
                                                         Object partitionKey) {
        Testing.verifyEventPublished(eventPublisher, partitionKey, CourseCreated.TYPE);
    }

    public static void verifyStudentCreatedEventPublished(EventPublisher eventPublisher,
                                                          Object partitionKey) {
        Testing.verifyEventPublished(eventPublisher, partitionKey, StudentCreated.TYPE);
    }

    public static void verifyStudentEnrolledEventPublished(EventPublisher eventPublisher,
                                                           Object partitionKey) {
        Testing.verifyEventPublished(eventPublisher, partitionKey, StudentEnrolled.TYPE);
    }

    public static void mockObjectMapperToFail(ObjectMapper objectMapper) {
        try {
            Mockito.when(objectMapper.writeValueAsBytes(Mockito.any(Event.class)))
                .thenThrow(new JsonProcessingExceptionStub());
        } catch (JsonProcessingException exception) {
            Assertions.fail(exception);
        }
    }

    public static void verifyObjectMapperFailed(ObjectMapper objectMapper) {
        try {
            Mockito.verify(objectMapper, Mockito.times(1))
                .writeValueAsBytes(Mockito.any(Event.class));
        } catch (JsonProcessingException exception) {
            Assertions.fail(exception);
        }
    }

    public static void mockKinesisAsyncClientToFail(KinesisAsyncClient kinesisAsyncClient) {
        Mockito.when(kinesisAsyncClient.putRecord(Mockito.any(PutRecordRequest.class)))
            .thenThrow(KinesisException.create("Kinesis Exception", null));
    }

    public static void verifyKinesisAsyncClientFailed(KinesisAsyncClient kinesisAsyncClient) {
        Mockito.verify(kinesisAsyncClient, Mockito.times(1))
            .putRecord(Mockito.any(PutRecordRequest.class));
    }
}
