package ru.amalyugin.orderservice.config;

import io.temporal.client.ActivityCompletionClient;
import io.temporal.client.WorkflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Bean
    public ActivityCompletionClient completionClient(WorkflowClient client) {
        return client.newActivityCompletionClient();
    }

}
