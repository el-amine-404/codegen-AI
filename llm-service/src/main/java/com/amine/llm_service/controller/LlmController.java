package com.amine.llm_service.controller;

import com.amine.llm_service.entity.FilePath;
import com.amine.llm_service.entity.ProjectWorkflowState;
import com.amine.llm_service.entity.UseCase;
import com.amine.llm_service.service.FileService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm")
public class LlmController {
    @Autowired
    private FileService fileService;

    private final ChatClient chatClient;
    private ProjectWorkflowState projectWorkflowState = new ProjectWorkflowState();

    public LlmController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }


    @GetMapping("/hello")
    ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }


    @PostMapping("/suggest-use-cases")
    public ResponseEntity<List<UseCase>> suggestUseCases(@RequestBody Map<String, String> requestBody){
        String userDescription = requestBody.get("userDescription");

        // Validate input
        if (userDescription == null || userDescription.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        projectWorkflowState.setUserDescription(userDescription);

        SystemMessage systemMessage = new SystemMessage("""
                You are a software architect. Your task is to suggest a list of microservices based on the user's description.
                For each microservice, provide a brief description of its functionality.
                """);
        UserMessage userMessage = new UserMessage(userDescription);
        Prompt zeroShotPrompt = new Prompt(List.of(systemMessage,userMessage));

        List<UseCase> suggestedUseCases = chatClient.prompt(zeroShotPrompt)
                .call()
                .entity(new ParameterizedTypeReference<List<UseCase>>() {});

        projectWorkflowState.setSuggestedUseCases(suggestedUseCases);
        // Logging 5 (for debugging)
        System.out.println("State is now: " + projectWorkflowState.toString());

        return ResponseEntity.ok(suggestedUseCases);
    }

    @PostMapping("/select-use-cases")
    public ResponseEntity selectUseCases(@RequestBody  Map<String, String> requestBody) {
        String selectedUseCases = requestBody.get("selectedUseCases");
        if (selectedUseCases == null || selectedUseCases.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        projectWorkflowState.setSelectedUseCases(selectedUseCases);
        // Logging 5 (for debugging)
        System.out.println("State is now: " + projectWorkflowState.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/select-architecture")
    public ResponseEntity<Void> selectArchitecture(@RequestBody Map<String, String> requestBody) {
        String architecture = requestBody.get("architecture");

        if (architecture == null || architecture.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        projectWorkflowState.setArchitecture(architecture);

        // Logging 5 (for debugging)
        System.out.println("Selected Architecture: " + architecture);
        System.out.println("State is now: " + projectWorkflowState.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/select-technologies")
    public ResponseEntity<Void> selectTechnologies(@RequestBody Map<String, String> requestBody) {
        String selectedTechnologies = requestBody.get("selectedTechnologies");

        if (selectedTechnologies == null || selectedTechnologies.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        projectWorkflowState.setLanguagesAndTools(selectedTechnologies);

        // Logging 5 (for debugging)
        System.out.println("Selected languages and tools: " + selectedTechnologies);
        System.out.println("State is now: " + projectWorkflowState.toString());

        return ResponseEntity.ok().build();
    }



    @GetMapping("/generate-zip")
    public ResponseEntity<byte[]> createZip() throws IOException {

        if(projectWorkflowState.getUserDescription() == null || projectWorkflowState.getUserDescription().isBlank()
                || projectWorkflowState.getSelectedUseCases() == null || projectWorkflowState.getUserDescription().isBlank()
                || projectWorkflowState.getArchitecture() == null || projectWorkflowState.getArchitecture().isBlank()
                || projectWorkflowState.getLanguagesAndTools() == null || projectWorkflowState.getLanguagesAndTools().isBlank()
        ){
            throw new RuntimeException("all the descriptions must be filled");
        }

//        Due to the limit of the free LLM api, the process is devided into 2 steps :
//            step 1: get file paths
//            step 2: generate the zip file (loop over each filepath and ask the llm to give the content)

        List<FilePath> filePaths = fileService.getFilePaths(projectWorkflowState);
        projectWorkflowState.setFilePaths(filePaths);

        // Create and zip the files
        Path zipPath = fileService.createAndZipFiles(projectWorkflowState);

        // Convert the ZIP file to a byte array
        File zipFile = zipPath.toFile();
        byte[] zipBytes = Files.readAllBytes(zipPath);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }


    


}
