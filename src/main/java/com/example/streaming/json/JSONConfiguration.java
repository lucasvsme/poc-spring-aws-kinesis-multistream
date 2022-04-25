package com.example.streaming.json;

import com.example.event.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
            .addMixIn(Event.class, EventMixIn.class);
    }
}
