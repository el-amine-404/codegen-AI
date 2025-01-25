package com.amine.project_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic projectEventsTopic() {
        return TopicBuilder.name("project-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}