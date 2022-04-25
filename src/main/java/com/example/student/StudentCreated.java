package com.example.student;

import com.example.event.Event;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;

public record StudentCreated(Payload payload,
                             EventMetadata.Empty metadata) implements Event<StudentCreated.Payload, EventMetadata.Empty> {

    public static final String TYPE = "com.example.StudentCreated";

    public record Payload(Student student) implements EventPayload {
    }

    @Override
    public String type() {
        return TYPE;
    }
}
