package com.amine.project_service.service;
import com.amine.project_service.entity.Project;
import com.amine.project_service.kafka.ProjectEventProducer;
import com.amine.project_service.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectEventProducer eventProducer;

    public Project createProject(Project project) {
        Project savedProject = projectRepository.save(project);
        eventProducer.sendProjectCreatedEvent(savedProject);
        return savedProject;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getUserProjects(String userId) {
        return projectRepository.findByUserId(userId);
    }
}
