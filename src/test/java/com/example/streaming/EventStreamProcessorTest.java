package com.example.streaming;

import com.example.event.Event;
import com.example.streaming.configuration.StreamException;
import com.example.streaming.configuration.StreamProcessor;
import com.example.streaming.json.JSONConfiguration;
import com.example.testing.EventsTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig({
    JSONConfiguration.class,
    EventStreamProcessorTestConfiguration.class
})
class EventStreamProcessorTest {

    private StreamProcessor streamProcessor;

    @BeforeEach
    public void beforeEach(ApplicationContext applicationContext) {
        this.streamProcessor = applicationContext.getBean(StreamProcessor.class);
    }

    static Stream<Arguments> events() {
        return Stream.of(
            Arguments.of(
                EventsTestBuilder.courseCreated(),
                EventsTestBuilder.readJson("CourseCreated.json")
            ),
            Arguments.of(
                EventsTestBuilder.studentEnrolled(),
                EventsTestBuilder.readJson("StudentEnrolled.json")
            ),
            Arguments.of(
                EventsTestBuilder.studentEnrolled(),
                EventsTestBuilder.readJson("StudentCreated.json")
            )
        );
    }

    @ParameterizedTest
    @MethodSource("events")
    void processingEvent(Event<?, ?> object, String json) {
        final var record = KinesisClientRecord.builder()
            .data(SdkBytes.fromString(json, Charset.defaultCharset()).asByteBuffer())
            .partitionKey(object.id().toString())
            .build();

        assertDoesNotThrow(() -> streamProcessor.processRecord(record));
    }

    @Test
    void failingToProcessEventDueToSerializationIssue() {
        final var record = KinesisClientRecord.builder()
            .data(SdkBytes.fromString("invalid json", Charset.defaultCharset()).asByteBuffer())
            .partitionKey(UUID.randomUUID().toString())
            .build();

        final var exception = assertThrows(
            StreamException.class,
            () -> streamProcessor.processRecord(record)
        );

        assertEquals("Error deserializing event to JSON", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }
}