package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AWSConfiguration {

    @Bean
    AwsCredentialsProvider credentialsProvider(Environment environment) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
            environment.getRequiredProperty("aws.accessKey", String.class),
            environment.getRequiredProperty("aws.secretKey", String.class)
        ));
    }

    @Bean
    Region region(Environment environment) {
        return Region.of(environment.getRequiredProperty("aws.region", String.class));
    }
}
