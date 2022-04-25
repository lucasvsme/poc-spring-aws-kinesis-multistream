package com.example.testing;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@Configuration
public class KinesisTestConfiguration {

    @Bean
    CloudWatchAsyncClient cloudWatchAsyncClient() {
        return Mockito.mock(CloudWatchAsyncClient.class);
    }

    @Bean
    DynamoDbAsyncClient dynamoDbAsyncClient() {
        return Mockito.mock(DynamoDbAsyncClient.class);
    }

    @Bean
    KinesisAsyncClient kinesisAsyncClient() {
        return Mockito.mock(KinesisAsyncClient.class);
    }
}
