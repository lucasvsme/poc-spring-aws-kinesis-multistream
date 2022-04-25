package com.example.streaming.configuration;

import com.example.event.Event;
import com.example.event.EventConsumer;
import com.example.event.EventConsumerFactory;
import com.example.testing.EventsTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreamConsumerFactoryTest {

    private static final Event<?, ?> EVENT = EventsTestBuilder.studentEnrolled();

    private final EventConsumer eventConsumer = Mockito.mock(EventConsumer.class);
    private EventConsumerFactory eventConsumerFactory;

    @BeforeEach
    public void beforeEach() {
        final var applicationContext = new GenericApplicationContext();
        applicationContext.registerBean("StudentEnrolledConsumer", EventConsumer.class, () -> eventConsumer);
        applicationContext.refresh();

        this.eventConsumerFactory = new StreamConsumerFactory(applicationContext);
    }

    @Test
    void findingConsumerByEvent() {
        final var consumer = eventConsumerFactory.findByEvent(EVENT);

        assertNotNull(consumer);
    }

    @Test
    void throwingExceptionWhenNoneConsumerFoundByEvent() {
        final var event = EventsTestBuilder.courseCreated();

        final var exception = assertThrows(
            StreamException.class,
            () -> eventConsumerFactory.findByEvent(event)
        );

        assertEquals("Could not find event consumer", exception.getMessage());
        assertEquals(NoSuchBeanDefinitionException.class, exception.getCause().getClass());
    }
}