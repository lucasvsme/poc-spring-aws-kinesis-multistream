package com.example.streaming.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import software.amazon.kinesis.coordinator.Scheduler;

import java.util.Map;

@SuppressWarnings("resource")
class StreamConfigurationTest {

    @Test
    void startingAllSchedulersOnApplicationStartup() throws InterruptedException {
        // Arrange
        final var context = new GenericApplicationContext();

        final var scheduler1 = Mockito.mock(Scheduler.class);
        final var scheduler2 = Mockito.mock(Scheduler.class);
        final var scheduler3 = Mockito.mock(Scheduler.class);
        context.registerBean("Scheduler1", Scheduler.class, () -> scheduler1);
        context.registerBean("Scheduler2", Scheduler.class, () -> scheduler2);
        context.registerBean("Scheduler3", Scheduler.class, () -> scheduler3);

        context.addApplicationListener(new StreamConfiguration());
        context.setEnvironment(createEnvironment(Map.ofEntries(
            Map.entry("aws.kinesis.enabled", true)
        )));

        // Act
        context.refresh();

        // Assert
        Thread.sleep(1000); // Waiting for the threads to start
        Mockito.verify(scheduler1, Mockito.times(1))
            .run();
        Mockito.verify(scheduler2, Mockito.times(1))
            .run();
        Mockito.verify(scheduler3, Mockito.times(1))
            .run();
    }

    @Test
    void disablingAllSchedulersUsingApplicationProperty() {
        // Arrange
        final var context = new GenericApplicationContext();

        final var scheduler1 = Mockito.mock(Scheduler.class);
        final var scheduler2 = Mockito.mock(Scheduler.class);
        final var scheduler3 = Mockito.mock(Scheduler.class);
        context.registerBean("Scheduler1", Scheduler.class, () -> scheduler1);
        context.registerBean("Scheduler2", Scheduler.class, () -> scheduler2);
        context.registerBean("Scheduler3", Scheduler.class, () -> scheduler3);

        context.addApplicationListener(new StreamConfiguration());
        context.setEnvironment(createEnvironment(Map.ofEntries(
            Map.entry("aws.kinesis.enabled", false)
        )));

        // Act
        context.refresh();

        // Assert
        Mockito.verifyNoInteractions(scheduler1, scheduler2, scheduler3);
    }

    private ConfigurableEnvironment createEnvironment(Map<String, Object> properties) {
        final var environment = new StandardEnvironment();
        final var propertySources = environment.getPropertySources();
        final var propertySource = new MapPropertySource("default", properties);

        propertySources.addFirst(propertySource);

        return environment;
    }
}