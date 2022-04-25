package com.example.testing;

import com.example.student.NewStudent;
import com.example.student.Student;

import java.util.UUID;

public final class StudentTestBuilder {

    private StudentTestBuilder() {
    }

    public static NewStudent newStudent() {
        final var firstName = "John";
        final var lastName = "Smith";

        return new NewStudent(firstName, lastName);
    }

    public static Student student() {
        final var studentId = UUID.randomUUID();
        final var newStudent = StudentTestBuilder.newStudent();

        return new Student(studentId, newStudent);
    }
}
