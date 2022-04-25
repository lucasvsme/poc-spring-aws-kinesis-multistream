package com.example.testing;

import com.example.event.EventException;
import com.example.streaming.configuration.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

public final class ExampleStreamProcessor extends StreamProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleStreamProcessor.class);

    public ExampleStreamProcessor() {
        super(LOGGER);
    }

    @Override
    public void processRecord(KinesisClientRecord records) throws EventException {
        // No-op
    }
}
