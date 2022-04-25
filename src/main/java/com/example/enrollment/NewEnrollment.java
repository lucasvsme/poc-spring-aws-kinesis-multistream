package com.example.enrollment;

import java.util.UUID;

public record NewEnrollment(UUID studentId, UUID courseId) {
}
