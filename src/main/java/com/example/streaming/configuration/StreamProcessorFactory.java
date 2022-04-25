package com.example.streaming.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.common.StreamIdentifier;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public final class StreamProcessorFactory implements ShardRecordProcessorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamProcessorFactory.class);

    private final ApplicationContext applicationContext;

    public StreamProcessorFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        throw new StreamException("Tried to create a record processor for unknown stream", null);
    }

    @Override
    public ShardRecordProcessor shardRecordProcessor(StreamIdentifier streamIdentifier) {
        final var streamName = streamIdentifier.streamName();
        LOGGER.debug("Trying to find stream processor for stream {}", streamName);

        return applicationContext.getBeansOfType(StreamProperties.class)
            .values().stream()
            .filter(properties -> properties.stream().value().equals(streamName))
            .peek(properties -> LOGGER.debug("Creating stream processor: {}", properties))
            .map(properties -> applicationContext.getBean(properties.id().value() + "Processor", ShardRecordProcessor.class))
            .findFirst()
            .orElseGet(this::shardRecordProcessor);
    }
}
