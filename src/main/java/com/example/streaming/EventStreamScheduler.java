package com.example.streaming;

import com.example.streaming.configuration.StreamProperties;
import com.example.streaming.configuration.StreamScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public final class EventStreamScheduler extends StreamScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStreamScheduler.class);

    public EventStreamScheduler(ApplicationContext context, StreamProperties properties) {
        super(createConfigsBuilder(context, properties), createRetrievalConfig(context, properties));
        logSchedulerCreated(properties, LOGGER);
    }
}
