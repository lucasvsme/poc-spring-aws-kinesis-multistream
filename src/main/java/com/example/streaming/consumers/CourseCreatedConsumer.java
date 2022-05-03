package com.example.streaming.consumers;

import com.example.course.CourseCreated;
import com.example.event.Event;
import com.example.event.EventConsumer;
import com.example.event.EventException;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class CourseCreatedConsumer implements EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseCreatedConsumer.class);

    @Override
    public <P extends EventPayload, M extends EventMetadata> void consume(Event<P, M> event) throws EventException {
        final var courseCreated = (CourseCreated) event;

        LOGGER.info("CourseCreated event consumed: {}", courseCreated);
    }
}
