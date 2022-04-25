package com.example.event;

public interface EventConsumerFactory {

    <P extends EventPayload, M extends EventMetadata> EventConsumer findByEvent(Event<P, M> event) throws EventException;
}
