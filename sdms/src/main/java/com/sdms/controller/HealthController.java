package com.sdms.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> health() {
        return Map.of(
                "application", "Secure Document Management System API",
                "version", "1.0",
                "swagger", "/swagger-ui/index.html",
                "health", "/actuator/health"
        );
    }
}