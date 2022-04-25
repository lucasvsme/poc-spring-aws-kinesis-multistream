package com.example.testing;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Serial;

public final class JsonProcessingExceptionStub extends JsonProcessingException {

    @Serial
    private static final long serialVersionUID = 601595754125219140L;

    public JsonProcessingExceptionStub() {
        super("JSON Processing Exception");
    }
}
