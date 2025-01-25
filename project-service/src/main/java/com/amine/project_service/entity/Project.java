package com.amine.project_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String projectName;

    private String description;

    @ElementCollection
    private List<String> techStack;

    private String status; // CREATED, GENERATING, COMPLETED, FAILED

    @Column(updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    private String zipFileLocation;
}