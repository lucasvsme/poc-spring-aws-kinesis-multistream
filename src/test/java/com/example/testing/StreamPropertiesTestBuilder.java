package com.example.testing;

import com.example.streaming.configuration.StreamProperties;

public final class StreamPropertiesTestBuilder {

    private StreamPropertiesTestBuilder() {
    }

    public static StreamProperties create() {
        return new StreamProperties(
            new StreamProperties.Id("id"),
            new StreamProperties.Stream("stream"),
            new StreamProperties.Application("application")
        );
    }
}
