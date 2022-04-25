package com.example.event;

public interface EventConsumer {

    <P extends EventPayload, M extends EventMetadata> void consume(Event<P, M> event) throws EventException;
}
