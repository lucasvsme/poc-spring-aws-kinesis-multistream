package com.example.enrollment;

import com.example.event.EventPublishRequest;
import com.example.event.EventPublisher;
import com.example.streaming.EnrollmentStreamConfiguration;
import com.example.streaming.configuration.StreamProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class EnrollmentServiceDefault implements EnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentServiceDefault.class);

    private final StreamProperties streamProperties;
    private final EventPublisher eventPublisher;

    public EnrollmentServiceDefault(@Qualifier(EnrollmentStreamConfiguration.STREAM_PROPERTIES) StreamProperties streamProperties,
                                    EventPublisher eventPublisher) {
        this.streamProperties = streamProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Enrollment enroll(NewEnrollment newEnrollment) {
        LOGGER.debug("Enrolling student {} in course {}", newEnrollment.studentId(), newEnrollment.courseId());
        final var enrollmentId = UUID.randomUUID();
        final var enrollment = new Enrollment(enrollmentId, newEnrollment);

        final var payload = new StudentEnrolled.Payload(enrollment.enrollmentId());
        final var metadata = new StudentEnrolled.Metadata(enrollment.studentId(), enrollment.studentId());
        final var event = new StudentEnrolled(payload, metadata);
        eventPublisher.publish(
            new EventPublishRequest<>(
                streamProperties.stream().value(),
                enrollmentId.toString(),
                event
            )
        );

        LOGGER.info("Enrollment created: {}", enrollment);
        return enrollment;
    }
}
