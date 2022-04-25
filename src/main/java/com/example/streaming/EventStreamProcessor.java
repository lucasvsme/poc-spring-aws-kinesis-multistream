package com.example.streaming;

import com.example.event.Event;
import com.example.event.EventConsumerFactory;
import com.example.streaming.configuration.StreamException;
import com.example.streaming.configuration.StreamProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.io.IOException;

public final class EventStreamProcessor extends StreamProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStreamProcessor.class);

    private final EventConsumerFactory eventConsumerFactory;
    private final ObjectMapper objectMapper;

    public EventStreamProcessor(EventConsumerFactory eventConsumerFactory, ObjectMapper objectMapper) {
        super(LOGGER);
        this.eventConsumerFactory = eventConsumerFactory;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processRecord(KinesisClientRecord record) {
        final var bytes = SdkBytes.fromByteBuffer(record.data());

        try {
            final var event = objectMapper.readValue(bytes.asByteArray(), Event.class);
            final var eventConsumer = eventConsumerFactory.findByEvent(event);

            eventConsumer.consume(event);
        } catch (IOException exception) {
            throw new StreamException(exception);
        }
    }
}
