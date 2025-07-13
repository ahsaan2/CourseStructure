package com.example.coursestructure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Course Search API is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 