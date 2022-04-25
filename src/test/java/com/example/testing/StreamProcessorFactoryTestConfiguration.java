package com.example.testing;

import com.example.streaming.configuration.StreamProcessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Configuration
@Import({
    KinesisTestConfiguration.class,
    ExampleStreamConfiguration.class
})
public class StreamProcessorFactoryTestConfiguration {

    @Bean
    ShardRecordProcessorFactory shardRecordProcessorFactory(ApplicationContext applicationContext) {
        return new StreamProcessorFactory(applicationContext);
    }
}
