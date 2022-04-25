package com.example.course;

import com.example.event.EventPublisher;
import com.example.streaming.configuration.StreamProperties;
import com.example.testing.CourseTestBuilder;
import com.example.testing.StreamPropertiesTestBuilder;
import com.example.testing.Testing;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourseServiceDefaultTest {

    private final StreamProperties streamProperties =
        StreamPropertiesTestBuilder.create();

    private final EventPublisher eventPublisher =
        Mockito.mock(EventPublisher.class);

    private final CourseService courseService =
        new CourseServiceDefault(streamProperties, eventPublisher);

    @Test
    void createPublishesCourseCreatedEvent() {
        Testing.mockCourseCreatedEventPublished(eventPublisher, streamProperties);
        final var newCourse = CourseTestBuilder.newCourse();

        final var course = courseService.create(newCourse);

        assertNotNull(course.id());
        assertEquals(newCourse.code(), course.code());
        assertEquals(newCourse.title(), course.title());
        assertEquals(newCourse.rating(), course.rating());
        Testing.verifyCourseCreatedEventPublished(eventPublisher, course.id());
    }
}