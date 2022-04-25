package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.net.URI;

@Configuration
public class KinesisConfiguration {

    @Bean
    CloudWatchAsyncClient cloudWatchAsyncClient(Environment environment,
                                                AwsCredentialsProvider credentialsProvider,
                                                Region region) {
        return CloudWatchAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(environment.getRequiredProperty("aws.url.cloudwatch", String.class)))
            .region(region)
            .build();
    }

    @Bean
    DynamoDbAsyncClient dynamoDbAsyncClient(Environment environment,
                                            AwsCredentialsProvider credentialsProvider,
                                            Region region) {
        return DynamoDbAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(environment.getRequiredProperty("aws.url.dynamodb", String.class)))
            .region(region)
            .build();
    }

    @Bean
    KinesisAsyncClient kinesisAsyncClient(Environment environment,
                                          AwsCredentialsProvider credentialsProvider,
                                          Region region) {
        return KinesisAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(environment.getRequiredProperty("aws.url.kinesis", String.class)))
            .region(region)
            .build();
    }
}
