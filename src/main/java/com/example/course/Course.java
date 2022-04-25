package com.example.course;

import java.util.UUID;

public record Course(UUID id, String code, String title, int rating) {

    public Course(UUID id, NewCourse newCourse) {
        this(id, newCourse.code(), newCourse.title(), newCourse.rating());
    }
}
