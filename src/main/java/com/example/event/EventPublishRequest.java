package com.example.event;

public record EventPublishRequest<P extends EventPayload, M extends EventMetadata>(String streamName,
                                                                                   String partitionKey,
                                                                                   Event<P, M> event) {
}
