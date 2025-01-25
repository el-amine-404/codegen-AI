package com.amine.llm_service.service;

import com.amine.llm_service.entity.FilePath;
import com.amine.llm_service.entity.ProjectWorkflowState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {

    private final ChatClient chatClient;

    public FileService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<FilePath> getFilePaths(ProjectWorkflowState projectWorkflowState){

        SystemMessage systemMessage = new SystemMessage("""
                You are a software architect. Your task is to give the full list of the file paths needed (do not exceed 10 files)
                to generate a complete project that respects the criterias that will be given after. the code must not contain comments or chracter'`' (code 96)
                """);
        UserMessage userMessage = new UserMessage(
                "Project description: " + projectWorkflowState.getUserDescription()
                        + ", Selected architecture: " + projectWorkflowState.getArchitecture()
                        + ", Selected use cases: " + projectWorkflowState.getSelectedUseCases()
                        + ", Selected technologies: " + projectWorkflowState.getLanguagesAndTools());
        Prompt zeroShotPrompt = new Prompt(List.of(systemMessage, userMessage));

        return chatClient.prompt(zeroShotPrompt)
                .call()
                .entity(new ParameterizedTypeReference<List<FilePath>>() {});
    }


    public Path createAndZipFiles(ProjectWorkflowState projectWorkflowState) throws IOException {
        // Create a temporary directory
        Path tempDirectory = Files.createTempDirectory("tempFiles");

        // Create files under the temporary directory
        for (FilePath filePath : projectWorkflowState.getFilePaths()) {

            SystemMessage systemMessage = new SystemMessage("""
                you are a senior software engineer give me the code for the file path that wil be given later
                """);
            UserMessage userMessage = new UserMessage("Project description: "+ projectWorkflowState.getUserDescription()
                    + "use cases: " + projectWorkflowState.getSelectedUseCases()
                    + "architecture: " + projectWorkflowState.getArchitecture()
                    + "technologies and tools: " + projectWorkflowState.getLanguagesAndTools()
                    + "file path: " + filePath);
            Prompt zeroShotPrompt = new Prompt(List.of(systemMessage,userMessage));
            String content = chatClient.prompt(zeroShotPrompt)
                    .call()
                    .content();

            System.out.println("COOONTENT ----> " + content);

            Path file = tempDirectory.resolve(filePath.filePath());
            Files.createDirectories(file.getParent()); // Create parent directories
            Files.createFile(file); // Create the file
            Files.writeString(file, content);
        }

        // Create a ZIP file
        Path zipFilePath = Files.createTempFile("files", ".zip");
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(tempDirectory).forEach(path -> {
                if (!Files.isDirectory(path)) {
                    try {
                        String zipEntryName = tempDirectory.relativize(path).toString();
                        zipOut.putNextEntry(new ZipEntry(zipEntryName));
                        Files.copy(path, zipOut);
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        }

        return zipFilePath; // Return the path to the ZIP file
    }
}

