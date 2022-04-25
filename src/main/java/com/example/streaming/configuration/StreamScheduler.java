package com.example.streaming.configuration;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.retrieval.RetrievalConfig;

import java.util.UUID;

public abstract class StreamScheduler extends Scheduler {

    protected StreamScheduler(final ConfigsBuilder configsBuilder, final RetrievalConfig retrievalConfig) {
        super(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            retrievalConfig
        );
    }

    public static ConfigsBuilder createConfigsBuilder(final ApplicationContext applicationContext,
                                                      final StreamProperties properties) {
        final var streamName = properties.stream();
        final var applicationName = properties.application();

        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);
        final var dynamoDbAsyncClient = applicationContext.getBean(DynamoDbAsyncClient.class);
        final var cloudWatchAsyncClient = applicationContext.getBean(CloudWatchAsyncClient.class);

        final var workerIdentifier = UUID.randomUUID();
        final var shardRecordProcessorFactory = applicationContext.getBean(ShardRecordProcessorFactory.class);

        return new ConfigsBuilder(
            streamName.value(),
            applicationName.value(),
            kinesisAsyncClient,
            dynamoDbAsyncClient,
            cloudWatchAsyncClient,
            workerIdentifier.toString(),
            shardRecordProcessorFactory
        );
    }

    public static RetrievalConfig createRetrievalConfig(final ApplicationContext applicationContext,
                                                        final StreamProperties properties) {
        final var kinesisAsyncClient = applicationContext.getBean(KinesisAsyncClient.class);

        final var streamName = properties.stream();
        final var applicationName = properties.application();

        return new RetrievalConfig(kinesisAsyncClient, streamName.value(), applicationName.value());
    }

    public static void logSchedulerCreated(final StreamProperties properties, final Logger logger) {
        logger.info(
            "Scheduler created for application {} to process records from {}",
            properties.application(),
            properties.stream()
        );
    }
}
