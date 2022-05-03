package com.example.streaming.consumers;

import com.example.event.Event;
import com.example.event.EventConsumer;
import com.example.event.EventException;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;
import com.example.student.StudentCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class StudentCreatedConsumer implements EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCreatedConsumer.class);

    @Override
    public <P extends EventPayload, M extends EventMetadata> void consume(Event<P, M> event) throws EventException {
        final var studentCreated = (StudentCreated) event;

        LOGGER.info("StudentCreated event consumed: {}", studentCreated);
    }
}
