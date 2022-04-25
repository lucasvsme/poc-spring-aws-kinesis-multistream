package com.example.event;

import java.io.Serial;

public abstract class EventException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7379039662654879221L;

    public EventException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
