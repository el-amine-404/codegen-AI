package com.amine.llm_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/llm")
public class LlmController {

    @GetMapping("/hello")
    ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

}
