package com.example.enrollment;

import java.util.UUID;

public record Enrollment(UUID enrollmentId, UUID studentId, UUID courseId) {

    public Enrollment(UUID enrollmentId, NewEnrollment newEnrollment) {
        this(enrollmentId, newEnrollment.studentId(), newEnrollment.courseId());
    }
}
