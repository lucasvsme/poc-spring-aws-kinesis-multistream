package com.example.student;

import com.example.event.EventMetadata;
import com.example.event.EventPublishRequest;
import com.example.event.EventPublisher;
import com.example.streaming.StudentStreamConfiguration;
import com.example.streaming.configuration.StreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class StudentServiceDefault implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceDefault.class);

    private final StreamProperties streamProperties;
    private final EventPublisher eventPublisher;

    public StudentServiceDefault(@Qualifier(StudentStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                 EventPublisher eventPublisher) {
        this.streamProperties = streamProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Student create(NewStudent newStudent) {
        LOGGER.debug("Creating new student: {}", newStudent);
        final var studentId = UUID.randomUUID();
        final var student = new Student(studentId, newStudent);

        final var payload = new StudentCreated.Payload(student);
        final var metadata = new EventMetadata.Empty();
        final var event = new StudentCreated(payload, metadata);
        eventPublisher.publish(
            new EventPublishRequest<>(
                streamProperties.stream().value(),
                studentId.toString(),
                event
            )
        );

        LOGGER.info("Student created: {}", student);
        return student;
    }
}
