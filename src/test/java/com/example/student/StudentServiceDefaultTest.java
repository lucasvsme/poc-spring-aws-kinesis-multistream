package com.example.student;

import com.example.event.EventPublisher;
import com.example.streaming.configuration.StreamProperties;
import com.example.testing.StreamPropertiesTestBuilder;
import com.example.testing.StudentTestBuilder;
import com.example.testing.Testing;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StudentServiceDefaultTest {

    private final StreamProperties streamProperties =
        StreamPropertiesTestBuilder.create();

    private final EventPublisher eventPublisher =
        Mockito.mock(EventPublisher.class);

    private final StudentService studentService =
        new StudentServiceDefault(streamProperties, eventPublisher);

    @Test
    void createPublishesStudentCreatedEvent() {
        Testing.mockStudentCreatedEventPublished(eventPublisher, streamProperties);
        final var newStudent = StudentTestBuilder.newStudent();

        final var student = studentService.create(newStudent);

        assertNotNull(student.id());
        assertEquals(newStudent.firstName(), student.firstName());
        assertEquals(newStudent.lastName(), student.lastName());
        Testing.verifyStudentCreatedEventPublished(eventPublisher, student.id());
    }
}