package com.example.streaming.configuration;

import com.example.AWSConfiguration;
import com.example.KinesisConfiguration;
import com.example.event.EventPublishRequest;
import com.example.streaming.json.JSONConfiguration;
import com.example.testing.EventPublishRequestTestBuilder;
import com.example.testing.JsonProcessingExceptionStub;
import com.example.testing.Testing;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig({
    AWSConfiguration.class,
    KinesisConfiguration.class,
    JSONConfiguration.class,
    StreamPublisherTestConfiguration.class
})
@Testcontainers
class StreamPublisherTest {

    private static final EventPublishRequest<?, ?> EVENT_PUBLISH_REQUEST =
        EventPublishRequestTestBuilder.create();

    @Container
    private static final LocalStackContainer CONTAINER =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.12.15"))
            .withServices(
                LocalStackContainer.Service.CLOUDWATCH,
                LocalStackContainer.Service.DYNAMODB,
                LocalStackContainer.Service.KINESIS
            );

    @DynamicPropertySource
    private static void setApplicationProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.accessKey", CONTAINER::getAccessKey);
        registry.add("aws.secretKey", CONTAINER::getSecretKey);
        registry.add("aws.region", CONTAINER::getRegion);
        registry.add("aws.url.cloudwatch", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.CLOUDWATCH));
        registry.add("aws.url.dynamodb", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB));
        registry.add("aws.url.kinesis", () -> CONTAINER.getEndpointOverride(LocalStackContainer.Service.KINESIS));
    }

    @BeforeAll
    public static void beforeAll(ApplicationContext applicationContext) {
        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);

        final var createStreamRequest = CreateStreamRequest.builder()
            .streamName(EVENT_PUBLISH_REQUEST.streamName())
            .shardCount(1)
            .build();

        kinesisAsyncClient.createStream(createStreamRequest)
            .join();
    }

    @Test
    void publishingEvents(ApplicationContext applicationContext) {
        final var objectMapper = applicationContext.getBean(ObjectMapper.class);
        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);
        final var eventPublisher = new StreamPublisher(objectMapper, kinesisAsyncClient);

        assertDoesNotThrow(() -> eventPublisher.publish(EVENT_PUBLISH_REQUEST));
    }

    @Test
    void serializationError(ApplicationContext applicationContext) {
        final var objectMapper = Mockito.mock(ObjectMapper.class);
        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);
        final var eventPublisher = new StreamPublisher(objectMapper, kinesisAsyncClient);

        Testing.mockObjectMapperToFail(objectMapper);

        final var exception = assertThrows(
            StreamException.class,
            () -> eventPublisher.publish(EVENT_PUBLISH_REQUEST)
        );

        assertEquals("Error serializing event to JSON", exception.getMessage());
        assertEquals(JsonProcessingExceptionStub.class, exception.getCause().getClass());
        Testing.verifyObjectMapperFailed(objectMapper);
    }

    @Test
    void streamError(ApplicationContext applicationContext) {
        final var objectMapper = applicationContext.getBean(ObjectMapper.class);
        final var kinesisAsyncClient = Mockito.mock(KinesisAsyncClient.class);
        final var eventPublisher = new StreamPublisher(objectMapper, kinesisAsyncClient);

        Testing.mockKinesisAsyncClientToFail(kinesisAsyncClient);

        final var exception = assertThrows(
            StreamException.class,
            () -> eventPublisher.publish(EVENT_PUBLISH_REQUEST)
        );

        assertEquals("Error publishing event to stream", exception.getMessage());
        assertEquals(SdkException.class, exception.getCause().getClass());
        Testing.verifyKinesisAsyncClientFailed(kinesisAsyncClient);
    }
}