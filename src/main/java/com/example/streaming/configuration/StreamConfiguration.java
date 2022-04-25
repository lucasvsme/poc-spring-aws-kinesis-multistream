package com.example.streaming.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import software.amazon.kinesis.coordinator.Scheduler;

@Configuration
public class StreamConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamConfiguration.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final var applicationContext = event.getApplicationContext();
        final var environment = applicationContext.getEnvironment();

        final var isStreamingEnabled = environment.getRequiredProperty("aws.kinesis.enabled", Boolean.class);
        if (!isStreamingEnabled) {
            LOGGER.info("Streaming is not enabled. Skipping schedulers creation.");
            return;
        }

        LOGGER.info("Streaming is enabled. Creating schedulers...");
        applicationContext.getBeansOfType(Scheduler.class)
            .forEach((name, scheduler) -> {
                LOGGER.info("Starting scheduler {}", name);

                final var thread = new Thread(scheduler);
                thread.setDaemon(true);
                thread.start();
            });
    }
}
