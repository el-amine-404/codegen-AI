package com.amine.llm_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWorkflowState {

    private String userDescription;
    private List<UseCase> suggestedUseCases;
    private String selectedUseCases;
    private String architecture;
    private String languagesAndTools;
    private List<FilePath> filePaths;


    @Override
    public String toString() {
        return "ProjectWorkflowState{" +
                "userDescription='" + userDescription + '\'' +
                ", suggestedUseCases=" + suggestedUseCases +
                ", selectedUseCases=" + selectedUseCases +
                ", architecture='" + architecture + '\'' +
                ", languagesAndTools=" + languagesAndTools +
                '}';
    }
}
