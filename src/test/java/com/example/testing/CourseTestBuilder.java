package com.example.testing;

import com.example.course.Course;
import com.example.course.NewCourse;

import java.util.UUID;

public final class CourseTestBuilder {

    private CourseTestBuilder() {
    }

    public static NewCourse newCourse() {
        final var code = "CS101";
        final var title = "Introduction to Computer Science";
        final var rating = 4;

        return new NewCourse(code, title, rating);
    }

    public static Course course() {
        final var courseId = UUID.randomUUID();
        final var newCourse = CourseTestBuilder.newCourse();

        return new Course(courseId, newCourse);
    }
}
