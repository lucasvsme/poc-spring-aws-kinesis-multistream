package com.example.streaming.configuration;

import com.example.event.EventException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.BeansException;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.IOException;
import java.io.Serial;

public final class StreamException extends EventException {

    @Serial
    private static final long serialVersionUID = -4146245509402692451L;

    public StreamException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StreamException(SdkException exception) {
        super("Error publishing event to stream", exception);
    }

    public StreamException(JsonProcessingException exception) {
        super("Error serializing event to JSON", exception);
    }

    public StreamException(IOException exception) {
        super("Error deserializing event to JSON", exception);
    }

    public StreamException(BeansException exception) {
        super("Could not find event consumer", exception);
    }
}
