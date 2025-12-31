package com.ecomm.AuroraFlames.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World! The AuroraFlames backend is running successfully!";
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint working! Backend is operational.";
    }

    @GetMapping("/status")
    public String status() {
        return "Backend status: OK";
    }
}
