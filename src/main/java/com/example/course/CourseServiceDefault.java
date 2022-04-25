package com.example.course;

import com.example.event.EventMetadata;
import com.example.event.EventPublishRequest;
import com.example.event.EventPublisher;
import com.example.streaming.CourseStreamConfiguration;
import com.example.streaming.configuration.StreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class CourseServiceDefault implements CourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceDefault.class);

    private final StreamProperties streamProperties;
    private final EventPublisher eventPublisher;

    public CourseServiceDefault(@Qualifier(CourseStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                EventPublisher eventPublisher) {
        this.streamProperties = streamProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Course create(NewCourse newCourse) {
        LOGGER.debug("Creating new course: {}", newCourse);
        final var courseId = UUID.randomUUID();
        final var course = new Course(courseId, newCourse);

        final var payload = new CourseCreated.Payload(course);
        final var metadata = new EventMetadata.Empty();
        final var event = new CourseCreated(payload, metadata);
        eventPublisher.publish(
            new EventPublishRequest<>(
                streamProperties.stream().value(),
                courseId.toString(),
                event
            )
        );

        LOGGER.info("Course created: {}", newCourse);
        return course;
    }
}
