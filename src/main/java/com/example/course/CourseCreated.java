package com.example.course;

import com.example.event.Event;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;

public record CourseCreated(CourseCreated.Payload payload,
                            EventMetadata.Empty metadata) implements Event<CourseCreated.Payload, EventMetadata.Empty> {

    public static final String TYPE = "com.example.CourseCreated";

    public record Payload(Course course) implements EventPayload {
    }

    @Override
    public String type() {
        return TYPE;
    }
}
