package com.amine.project_service.controller;


import com.amine.project_service.entity.Project;
import com.amine.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getUserProjects(@PathVariable String userId) {
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }
}