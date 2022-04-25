package com.example.enrollment;

import com.example.event.EventPublisher;
import com.example.streaming.configuration.StreamProperties;
import com.example.testing.EnrollmentTestBuilder;
import com.example.testing.StreamPropertiesTestBuilder;
import com.example.testing.Testing;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnrollmentServiceDefaultTest {

    private final StreamProperties streamProperties =
        StreamPropertiesTestBuilder.create();

    private final EventPublisher eventPublisher =
        Mockito.mock(EventPublisher.class);

    private final EnrollmentService enrollmentService =
        new EnrollmentServiceDefault(streamProperties, eventPublisher);

    @Test
    void enrollPublishesStudentEnrolledEvent() {
        Testing.mockStudentEnrolledEventPublished(eventPublisher, streamProperties);
        final var newEnrollment = EnrollmentTestBuilder.newEnrollment();

        final var enrollment = enrollmentService.enroll(newEnrollment);

        assertNotNull(enrollment.enrollmentId());
        assertEquals(newEnrollment.studentId(), enrollment.studentId());
        assertEquals(newEnrollment.courseId(), enrollment.courseId());
        Testing.verifyStudentEnrolledEventPublished(eventPublisher, enrollment.enrollmentId());
    }
}