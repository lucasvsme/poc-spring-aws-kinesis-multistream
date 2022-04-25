package com.example.testing;

import com.example.enrollment.Enrollment;
import com.example.enrollment.NewEnrollment;

import java.util.UUID;

public final class EnrollmentTestBuilder {

    private EnrollmentTestBuilder() {
    }

    public static NewEnrollment newEnrollment() {
        final var student = StudentTestBuilder.student();
        final var course = CourseTestBuilder.course();

        return new NewEnrollment(student.id(), course.id());
    }

    public static Enrollment enrollment() {
        final var enrollmentId = UUID.randomUUID();
        final var student = StudentTestBuilder.student();
        final var course = CourseTestBuilder.course();

        return new Enrollment(enrollmentId, student.id(), course.id());
    }
}
