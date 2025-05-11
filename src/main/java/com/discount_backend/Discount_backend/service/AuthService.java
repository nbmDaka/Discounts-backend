package com.discount_backend.Discount_backend.service;


import com.discount_backend.Discount_backend.dto.SignupRequest;
import com.discount_backend.Discount_backend.entity.*;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.exception.UserAlreadyExistsException;
import com.discount_backend.Discount_backend.repository.*;
import com.discount_backend.Discount_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private VerificationTokenRepository tokenRepo;
    @Autowired private PasswordEncoder pwEncoder;
    @Autowired private JavaMailSender mailSender;
    @Autowired private AuthenticationManager authManager;
    @Autowired public JwtUtil jwtUtil;

    @Transactional
    public void register(SignupRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(pwEncoder.encode(req.getPassword()));
        userRepo.save(user);

        // assign default ROLE_USER
        Role role = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        UserRole ur = new UserRole();
        ur.setUser(user);
        ur.setRole(role);
        user.getUserRoles().add(ur);

        // create & send verification token
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setUser(user);
        vToken.setExpiryDate(Instant.now().plusSeconds(86400));
        tokenRepo.save(vToken);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getUsername());
        mail.setSubject("Please activate your account");
        mail.setText("Click to verify: http://localhost:8080/api/auth/verify?token=" + token);
        mailSender.send(mail);
    }

    public void verifyAccount(String token) {
        VerificationToken vToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        if (vToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Token expired");
        }
        User user = vToken.getUser();
        user.setEnabled(true);
        userRepo.save(user);
        tokenRepo.delete(vToken);
    }

    public com.discount_backend.Discount_backend.dto.AuthResponse authenticate(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepo.findByUsername(username).get();
        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
        String access = jwtUtil.generateAccessToken(username, roles);
        String refresh = jwtUtil.generateRefreshToken(username);
        return new com.discount_backend.Discount_backend.dto.AuthResponse(access, refresh, roles);
    }
}
