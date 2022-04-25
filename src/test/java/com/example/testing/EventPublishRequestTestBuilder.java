package com.example.testing;

import com.example.event.EventPublishRequest;

public final class EventPublishRequestTestBuilder {

    private EventPublishRequestTestBuilder() {
    }

    public static EventPublishRequest<?, ?> create() {
        final var event = EventsTestBuilder.studentEnrolled();
        final var partitionKey = event.payload().enrollmentId().toString();
        final var streamName = "example_stream";

        return new EventPublishRequest<>(streamName, partitionKey, event);
    }
}
