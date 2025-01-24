package com.amine.llm_service.controller;

import com.amine.llm_service.entity.ProjectWorkflowState;
import com.amine.llm_service.entity.UseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm")
public class LlmController {

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

        return ResponseEntity.ok(suggestedUseCases);
    }

    

}
