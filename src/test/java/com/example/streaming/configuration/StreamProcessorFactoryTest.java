package com.example.streaming.configuration;

import com.example.testing.ExampleStreamConfiguration;
import com.example.testing.ExampleStreamProcessor;
import com.example.testing.KinesisTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.kinesis.common.StreamIdentifier;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig({
    KinesisTestConfiguration.class,
    ExampleStreamConfiguration.class,
    StreamProcessorFactoryTestConfiguration.class
})
class StreamProcessorFactoryTest {

    private ShardRecordProcessorFactory factory;

    @DynamicPropertySource
    private static void setApplicationProperties(DynamicPropertyRegistry registry) {
        registry.add("streams.example.name", () -> "example_stream");
        registry.add("streams.example.application", () -> "example_application");
    }

    @BeforeEach
    public void beforeEach(ApplicationContext context) {
        this.factory = new StreamProcessorFactory(context);
    }

    @Test
    void findingStreamProcessorByBeanName() {
        final var streamIdentifier = StreamIdentifier.singleStreamInstance("example_stream");

        final var processor = factory.shardRecordProcessor(streamIdentifier);

        assertNotNull(processor);
        assertEquals(ExampleStreamProcessor.class, processor.getClass());
    }

    @Test
    void notFindingStreamProcessorByNameMeansImplementationError() {
        final var streamIdentifier = StreamIdentifier.singleStreamInstance("not_example_stream");

        final var exception = assertThrows(
            StreamException.class,
            () -> factory.shardRecordProcessor(streamIdentifier)
        );

        assertEquals("Tried to create a record processor for unknown stream", exception.getMessage());
        assertNull(exception.getCause());
    }
}