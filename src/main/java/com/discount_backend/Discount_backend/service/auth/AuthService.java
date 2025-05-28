package com.discount_backend.Discount_backend.service.auth;


import com.discount_backend.Discount_backend.dto.auth.AuthResponse;
import com.discount_backend.Discount_backend.dto.auth.SignupRequest;
import com.discount_backend.Discount_backend.entity.auth.VerificationToken;
import com.discount_backend.Discount_backend.entity.role.Role;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.entity.user.UserRole;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.exception.UserAlreadyExistsException;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import com.discount_backend.Discount_backend.repository.auth.VerificationTokenRepository;
import com.discount_backend.Discount_backend.repository.role.RoleRepository;
import com.discount_backend.Discount_backend.service.mailSender.MailSenderService;
import com.discount_backend.Discount_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder pwEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MailSenderService emailService;


    @Autowired
    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       VerificationTokenRepository tokenRepo,
                       PasswordEncoder pwEncoder,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil,
                        MailSenderService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.tokenRepo = tokenRepo;
        this.pwEncoder = pwEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Transactional
    public void register(SignupRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            logger.warn("Registration attempt failed: Username '{}' already taken.", req.getUsername());
            throw new UserAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(pwEncoder.encode(req.getPassword()));

        if (user.getProfile() == null) {
            user.setProfile(new UserProfile());
        }
        UserProfile profile = user.getProfile();
        if (profile == null) {
            logger.error("Registration failed for username '{}': UserProfile is still null.", req.getUsername());
            throw new RuntimeException("Internal server error: User profile not initialized.");
        }

        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getUsername());

        try {
            userRepo.save(user);
            logger.info("User '{}' saved to database.", req.getUsername());
        } catch (DataIntegrityViolationException e) {
            logger.error("Database integrity violation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user due to database constraint.", e);
        } catch (Exception e) {
            logger.error("Unexpected DB error saving user '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Unexpected error saving user.", e);
        }

        try {
            Role role = roleRepo.findByName("user")
                    .orElseThrow(() -> new ResourceNotFoundException("Default role 'user' not found.", "user_role"));

            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setRole(role);
            user.getUserRoles().add(ur);
            userRepo.save(user);

            logger.info("Assigned 'user' role to '{}'.", req.getUsername());
        } catch (Exception e) {
            logger.error("Error assigning role to '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to assign default role.", e);
        }

        // Create token
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setUser(user);
        vToken.setExpiryDate(Instant.now().plusSeconds(86400));
        try {
            tokenRepo.save(vToken);
            logger.info("Token saved for '{}'.", req.getUsername());
        } catch (Exception e) {
            logger.error("Token save failed for '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to save verification token.", e);
        }

        // Send email via Mailjet
        try {
            String email = user.getProfile().getEmail();
            String subject = "Please activate your account";
            String verificationUrl =
                    "http://5.223.52.23:3000/api/auth/verify?token=" + token;
            String textBody =
                    "Hello " + profile.getFirstName() + ",\n\n" +
                            "Please activate your account by visiting the link:\n" +
                            verificationUrl + "\n\n" +
                            "Thank you!";

            emailService.sendVerificationEmail(email, subject, textBody);
            logger.info("Verification email sent to '{}'.", email);
        } catch (Exception e) {
            logger.error("Mailjet error sending to '{}': {}", user.getProfile().getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email.", e);
        }

        logger.info("User '{}' registered successfully.", req.getUsername());
    }


    @Transactional
    public void verifyAccount(String token) {
        VerificationToken vToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token", id));
        if (vToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Token expired", id);
        }
        User user = vToken.getUser();
        user.setEnabled(true);
        userRepo.save(user);
        tokenRepo.delete(vToken);
    }

    public AuthResponse authenticate(String username, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", id));

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
        String access = jwtUtil.generateAccessToken(username, roles);
        String refresh = jwtUtil.generateRefreshToken(username);
        return new AuthResponse(access, refresh, roles);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Missing refresh token"
            );
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }
        String username = jwtUtil.getUsername(refreshToken);
        // bypass authenticationManager here since we already trust the refresh token
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", id));
        List<String> roles = user.getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());
        String newAccess  = jwtUtil.generateAccessToken(username, roles);
        String newRefresh = jwtUtil.generateRefreshToken(username);
        return new AuthResponse(newAccess, newRefresh, roles);
    }
}
