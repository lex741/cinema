package com.lex.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Configuration
public class CinemaConfig {

    // singleton bean
    @Bean
    public DateTimeFormatter uiDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    }

    // prototype bean
    @Bean
    @Scope("prototype")
    public OperationContext operationContext() {
        return new OperationContext(UUID.randomUUID().toString());
    }

    public static class OperationContext {
        private final String requestId;

        public OperationContext(String requestId) {
            this.requestId = requestId;
        }

        public String getRequestId() {
            return requestId;
        }
    }
}
