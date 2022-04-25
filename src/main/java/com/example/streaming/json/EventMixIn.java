package com.example.streaming.json;

import com.example.course.CourseCreated;
import com.example.enrollment.StudentEnrolled;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;
import com.example.student.StudentCreated;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CourseCreated.class, name = CourseCreated.TYPE),
    @JsonSubTypes.Type(value = StudentCreated.class, name = StudentCreated.TYPE),
    @JsonSubTypes.Type(value = StudentEnrolled.class, name = StudentEnrolled.TYPE),
})
public interface EventMixIn<P extends EventPayload, M extends EventMetadata> {

    default UUID id() {
        return UUID.randomUUID();
    }

    String type();

    P payload();

    M metadata();
}
