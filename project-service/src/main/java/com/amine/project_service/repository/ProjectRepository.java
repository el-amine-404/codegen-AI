package com.amine.project_service.repository;

import com.amine.project_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(String userId);
    List<Project> findByStatus(String status);
}