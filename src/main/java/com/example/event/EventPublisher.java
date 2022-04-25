package com.example.event;

public interface EventPublisher {

    <P extends EventPayload, M extends EventMetadata> void publish(EventPublishRequest<P, M> request) throws EventException;
}
