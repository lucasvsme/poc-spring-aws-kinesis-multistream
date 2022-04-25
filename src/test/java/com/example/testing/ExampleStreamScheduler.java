package com.example.testing;

import com.example.streaming.configuration.StreamProperties;
import com.example.streaming.configuration.StreamScheduler;
import org.springframework.context.ApplicationContext;

public class ExampleStreamScheduler extends StreamScheduler {

    public ExampleStreamScheduler(ApplicationContext context, StreamProperties properties) {
        super(createConfigsBuilder(context, properties), createRetrievalConfig(context, properties));
    }
}
