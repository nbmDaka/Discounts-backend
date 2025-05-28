package com.discount_backend.Discount_backend.controller;


import com.discount_backend.Discount_backend.dto.auth.AuthResponse;
import com.discount_backend.Discount_backend.dto.auth.LoginRequest;
import com.discount_backend.Discount_backend.dto.auth.SignupRequest;
import com.discount_backend.Discount_backend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
        AuthResponse tokens = authService.authenticate(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        // Expect header: "Bearer <refreshToken>"
        if (!authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Authorization header");
        }
        String refreshToken = authHeader.substring(7);
        AuthResponse tokens = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear access_token cookie
        ResponseCookie clearAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Immediately expire
                .sameSite("Lax")
                .build();

        // Clear refresh_token cookie
        ResponseCookie clearRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", clearAccessCookie.toString());
        response.addHeader("Set-Cookie", clearRefreshCookie.toString());

        return ResponseEntity.ok().build();
    }

}
