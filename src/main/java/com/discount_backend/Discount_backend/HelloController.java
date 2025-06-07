package com.discount_backend.Discount_backend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    @GetMapping("/")
    public String home() {
        return "Hello, Spring Boot is running! And the discount service is up and running!";
    }
}
