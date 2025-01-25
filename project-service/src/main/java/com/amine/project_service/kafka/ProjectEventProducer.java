package com.amine.project_service.kafka;


import com.amine.project_service.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProjectEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProjectCreatedEvent(Project project) {
        kafkaTemplate.send("project-events", Map.of(
                "type", "PROJECT_CREATED",
                "projectId", project.getId(),
                "userId", project.getUserId(),
                "timestamp", LocalDateTime.now()
        ));
    }
}