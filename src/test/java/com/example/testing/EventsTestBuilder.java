package com.example.testing;

import com.example.course.Course;
import com.example.course.CourseCreated;
import com.example.enrollment.StudentEnrolled;
import com.example.event.EventMetadata;
import com.example.student.Student;
import com.example.student.StudentCreated;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EventsTestBuilder {

    private EventsTestBuilder() {
    }

    public static String readJson(String filename) {
        final var classpath = Path.of("src", "test", "resources");
        final var eventJsonFilename = Path.of("events", filename);

        try (final var lines = Files.lines(classpath.resolve(eventJsonFilename))) {
            return lines.collect(Collectors.joining());
        } catch (IOException exception) {
            Assertions.fail(exception);
        }

        return null; // Unreachable code
    }

    public static CourseCreated courseCreated() {
        final var id = UUID.fromString("2630f200-3696-4832-85c9-3651ef9738c4");
        final var code = "CS101";
        final var title = "Introduction to Computer Science";
        final var rating = 4;
        final var course = new Course(id, code, title, rating);

        final var payload = new CourseCreated.Payload(course);
        final var metadata = new EventMetadata.Empty();

        return new CourseCreated(payload, metadata);
    }

    public static StudentCreated studentCreated() {
        final var id = UUID.fromString("511937ad-ef50-4c22-8899-17017090e0d2");
        final var firstName = "John";
        final var lastName = "Smith";
        final var student = new Student(id, firstName, lastName);

        final var payload = new StudentCreated.Payload(student);
        final var metadata = new EventMetadata.Empty();

        return new StudentCreated(payload, metadata);
    }

    public static StudentEnrolled studentEnrolled() {
        final var enrollmentId = UUID.fromString("999e002a-69ae-453a-9d5a-11b3c5c22d46");
        final var studentId = UUID.fromString("56a3da44-5278-405e-bf4b-311b344ea9e9");
        final var courseId = UUID.fromString("3851b640-2a53-4f3b-8759-079612ea5a1a");

        final var payload = new StudentEnrolled.Payload(enrollmentId);
        final var metadata = new StudentEnrolled.Metadata(studentId, courseId);

        return new StudentEnrolled(payload, metadata);
    }
}
