package com.example.streaming.configuration;

import com.example.event.Event;
import com.example.event.EventConsumer;
import com.example.event.EventConsumerFactory;
import com.example.event.EventException;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public final class StreamConsumerFactory implements EventConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamConsumerFactory.class);

    private final ApplicationContext applicationContext;

    public StreamConsumerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <P extends EventPayload, M extends EventMetadata> EventConsumer findByEvent(Event<P, M> event) throws EventException {
        LOGGER.info("Trying to find event consumer for event {}", event);

        try {
            return applicationContext.getBean(event.getClass().getSimpleName() + "Consumer", EventConsumer.class);
        } catch (BeansException exception) {
            throw new StreamException(exception);
        }
    }
}
