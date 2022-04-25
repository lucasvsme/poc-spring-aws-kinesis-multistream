package com.example.enrollment;

import com.example.event.Event;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;

import java.util.UUID;

public record StudentEnrolled(StudentEnrolled.Payload payload,
                              StudentEnrolled.Metadata metadata) implements Event<StudentEnrolled.Payload, StudentEnrolled.Metadata> {

    public static final String TYPE = "com.example.StudentEnrolled";

    public record Payload(UUID enrollmentId) implements EventPayload {
    }

    public record Metadata(UUID studentId, UUID courseId) implements EventMetadata {
    }

    @Override
    public String type() {
        return TYPE;
    }
}
