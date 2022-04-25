package com.example.streaming.configuration;

import com.example.event.EventException;
import com.example.event.EventMetadata;
import com.example.event.EventPayload;
import com.example.event.EventPublisher;
import com.example.event.EventPublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

@Component
public final class StreamPublisher implements EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamPublisher.class);

    private final ObjectMapper objectMapper;
    private final KinesisAsyncClient kinesisAsyncClient;

    public StreamPublisher(ObjectMapper objectMapper, KinesisAsyncClient kinesisAsyncClient) {
        this.objectMapper = objectMapper;
        this.kinesisAsyncClient = kinesisAsyncClient;
    }

    @Override
    public <P extends EventPayload, M extends EventMetadata> void publish(EventPublishRequest<P, M> publishRequest) throws EventException {
        final var streamName = publishRequest.streamName();
        final var partitionKey = publishRequest.partitionKey();
        final var event = publishRequest.event();
        LOGGER.debug("Publishing event to stream {} in partition {}: {}", streamName, partitionKey, event);

        try {
            final var request = PutRecordRequest.builder()
                .streamName(streamName)
                .partitionKey(partitionKey)
                .data(SdkBytes.fromByteArray(objectMapper.writeValueAsBytes(event)))
                .build();

            final var response = kinesisAsyncClient.putRecord(request)
                .join();

            LOGGER.info("Event {} published: {}", event.id(), response.sdkHttpResponse().statusText());
        } catch (JsonProcessingException exception) {
            throw new StreamException(exception);
        } catch (SdkException exception) {
            throw new StreamException(exception);
        }
    }
}
