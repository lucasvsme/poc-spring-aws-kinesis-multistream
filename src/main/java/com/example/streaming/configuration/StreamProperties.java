package com.example.streaming.configuration;

public record StreamProperties(Id id, Stream stream, Application application) {

    public record Id(String value) {
    }

    public record Stream(String value) {
    }

    public record Application(String value) {
    }
}
