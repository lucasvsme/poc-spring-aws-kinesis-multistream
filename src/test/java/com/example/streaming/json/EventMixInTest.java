package com.example.streaming.json;

import com.example.event.Event;
import com.example.testing.EventsTestBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(JSONConfiguration.class)
class EventMixInTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach(ApplicationContext context) {
        this.objectMapper = context.getBean(ObjectMapper.class);
    }

    @Test
    void deserializingCourseCreated() throws JsonProcessingException {
        final var object = EventsTestBuilder.courseCreated();
        final var json = EventsTestBuilder.readJson("CourseCreated.json");

        final var event = objectMapper.readValue(json, Event.class);

        assertEquals(object, event);
    }

    @Test
    void deserializingStudentCreated() throws JsonProcessingException {
        final var object = EventsTestBuilder.studentCreated();
        final var json = EventsTestBuilder.readJson("StudentCreated.json");

        final var event = objectMapper.readValue(json, Event.class);

        assertEquals(object, event);
    }

    @Test
    void deserializingStudentEnrolled() throws JsonProcessingException {
        final var object = EventsTestBuilder.studentEnrolled();
        final var json = EventsTestBuilder.readJson("StudentEnrolled.json");

        final var event = objectMapper.readValue(json, Event.class);

        assertEquals(object, event);
    }
}