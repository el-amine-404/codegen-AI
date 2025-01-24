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
    private List<UseCase> selectedUseCases;
    private String architecture;
    private List<String> languagesAndTools;



}
