package com.example.event;

import java.util.UUID;

public interface Event<P extends EventPayload, M extends EventMetadata> {

    default UUID id() {
        return UUID.randomUUID();
    }

    String type();

    P payload();

    M metadata();
}
