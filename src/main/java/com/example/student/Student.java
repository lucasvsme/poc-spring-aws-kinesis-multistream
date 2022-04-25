package com.example.student;

import java.util.UUID;

public record Student(UUID id, String firstName, String lastName) {

    public Student(UUID id, NewStudent newStudent) {
        this(id, newStudent.firstName(), newStudent.lastName());
    }
}
