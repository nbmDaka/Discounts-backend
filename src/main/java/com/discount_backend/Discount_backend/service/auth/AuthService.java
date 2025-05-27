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
import com.discount_backend.Discount_backend.service.sendGrid.SendGridService;
import com.discount_backend.Discount_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.dao.DataIntegrityViolationException;

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
    private final JavaMailSender mailSender;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final SendGridService sendGridService;

    @Autowired
    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       VerificationTokenRepository tokenRepo,
                       PasswordEncoder pwEncoder,
                       JavaMailSender mailSender,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil,
                        SendGridService sendGridService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.tokenRepo = tokenRepo;
        this.pwEncoder = pwEncoder;
        this.mailSender = mailSender;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.sendGridService = sendGridService;
    }

    @Transactional
    public void register(SignupRequest req) {
        // --- 1. Check for existing username ---
        if (userRepo.existsByUsername(req.getUsername())) {
            logger.warn("Registration attempt failed: Username '{}' already taken.", req.getUsername());
            throw new UserAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(pwEncoder.encode(req.getPassword()));

        // --- 2. Handle UserProfile setup (Potential NullPointerException) ---
        // Ensure user.getProfile() is not null. If it is, you need to initialize it.
        // If UserProfile is lazily loaded or not automatically set, you MUST initialize it here.
        // For example, if User.profile is @OneToOne and LAZY, it might be null initially.
        // A common pattern is:
        if (user.getProfile() == null) {
            user.setProfile(new UserProfile()); // Initialize UserProfile if it's not done in User constructor
        }
        UserProfile profile = user.getProfile(); // Now 'profile' should not be null

        // Defensive check (optional, but good if unsure about UserProfile initialization):
        if (profile == null) {
            logger.error("Registration failed for username '{}': UserProfile is still null after initialization attempt. Please review User entity and its UserProfile association.", req.getUsername());
            throw new RuntimeException("Internal server error: User profile not initialized.");
        }

        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        // --- 3. Critical: Ensure email is a valid email address ---
        // Assuming req.getUsername() *is* the email. If not, add a req.getEmail() field to SignupRequest
        profile.setEmail(req.getUsername()); // Assuming username is email. If SignupRequest has email field, use req.getEmail() here.

        try {
            userRepo.save(user);
            logger.info("User '{}' saved to database.", req.getUsername());
        } catch (DataIntegrityViolationException e) {
            logger.error("Database integrity violation during user save for '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to save user due to database constraint violation. Please check unique fields.", e);
        } catch (Exception e) {
            logger.error("Unexpected error saving user '{}' to database: {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to save user due to unexpected database error.", e);
        }

        // --- 4. Assign default ROLE_USER ---
        try {
            Role role = roleRepo.findByName("user")
                    .orElseThrow(() -> new ResourceNotFoundException("Default role 'user' not found. Please ensure it exists in the database.", "user_role"));
            // ^^^ CHANGED THIS LINE to match your ResourceNotFoundException constructor
            // If it expects a Long ID, you might need to pass 0L or null (e.g., , null) or ( , 0L)

            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setRole(role);
            user.getUserRoles().add(ur);
            // Note: You might need to save user or userRole separately if not cascading properly
            userRepo.save(user); // If userRoles are cascaded from User, this will save the UserRole. If not, you need userRoleRepo.save(ur);
            logger.info("Assigned 'user' role to user '{}'.", req.getUsername());
        } catch (ResourceNotFoundException e) {
            logger.error("Role assignment failed for '{}': {}", req.getUsername(), e.getMessage(), e);
            throw e; // Re-throw the specific exception
        } catch (Exception e) {
            logger.error("Unexpected error during role assignment for '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to assign default role.", e);
        }

        // --- 5. Create & save verification token ---
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setUser(user);
        vToken.setExpiryDate(Instant.now().plusSeconds(86400));
        try {
            tokenRepo.save(vToken);
            logger.info("Verification token generated and saved for user '{}'.", req.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save verification token for user '{}': {}", req.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Error saving verification token.", e);
        }


        // --- 6. Send verification email ---
        try {
            try {
                String email = user.getProfile().getEmail();
                String subject = "Please activate your account";
                String verificationUrl = "http://209.97.172.192:3000/api/auth/verify?token=" + token;
                String body = "Click to verify your account: " + verificationUrl;

                sendGridService.sendEmail(email, subject, body);
                logger.info("Verification email sent to '{}'.", email);
            } catch (Exception e) {
                logger.error("Failed to send verification email to '{}': {}", user.getProfile().getEmail(), e.getMessage(), e);
                throw new RuntimeException("Error sending verification email. Please check SendGrid configuration.", e);
            }
            logger.info("Verification email sent to '{}'.", user.getProfile().getEmail());
        } catch (MailException e) { // Catch specific MailException
            logger.error("Failed to send verification email to '{}': {}", user.getProfile().getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error sending verification email. Please check mail configuration and recipient address.", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while sending email to '{}': {}", user.getProfile().getEmail(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during email sending.", e);
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
            throw new org.springframework.security.core.AuthenticationException("Invalid username or password") {};
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
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
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
