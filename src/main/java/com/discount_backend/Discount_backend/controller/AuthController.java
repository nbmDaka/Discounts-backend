package com.discount_backend.Discount_backend.controller;


import com.discount_backend.Discount_backend.dto.*;
import com.discount_backend.Discount_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest req) {
        authService.register(req);
        return ResponseEntity.ok("Registration successful. Check email for verification.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        authService.verifyAccount(token);
        return ResponseEntity.ok("Account activated.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.authenticate(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
        // validate & issue new tokens
        String username = authService.jwtUtil.getUsername(refreshToken);
        if (!authService.jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.authenticate(username, "" /*password unused*/));
    }
}
