package com.sdms.controller;

import com.sdms.dto.LoginRequest;
import com.sdms.dto.RegisterRequest;
import com.sdms.model.User;
import com.sdms.model.UserInfoResponse;
import com.sdms.jwt.JwtUtil;
import com.sdms.repository.UserRepository;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Logger logger =
        LoggerFactory.getLogger(AuthController.class);

    // ================= LOGIN =================
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user and returns a JWT token"
    )
    @PostMapping("/login")
    public Map<String, String> login(
            @Valid @RequestBody LoginRequest request) {

        logger.info("Login request received for username: {}", request.getUsername());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        logger.info("User authenticated successfully: {}", authentication.getName());

        String token =
                jwtUtil.generateToken(authentication.getName());

        logger.debug("JWT token generated for user: {}", authentication.getName());

        String role =
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority();

        return Map.of(
            "token", token,
            "role", role
        );
    }

    // ================= REGISTER =================
    @Operation(
    summary = "Register user",
    description = "Creates a new user account"
    )
    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request) {

            logger.info("Register request received for username: {}", request.getUsername());

        if (userRepository
                .findByUsername(request.getUsername())
                .isPresent()) {
           logger.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        // 🔐 BCrypt
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        // ✅ DIRECT ENUM
        user.setRole(request.getRole());

        userRepository.save(user);

        logger.info("User registered successfully: {}", request.getUsername());

        return "User Registered Successfully";
    }


    // ================= CURRENT USER =================
    @Operation(
    summary = "Get current user",
    description = "Returns information about the authenticated user",
    security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/me")
    public UserInfoResponse me(Authentication auth) {

        if (auth == null) {
              logger.error("Unauthorized access attempt to /me");
            throw new RuntimeException(
                "Unauthorized - JWT not processed");
        }
         logger.info("Fetching current user: {}", auth.getName());
        return new UserInfoResponse(
            auth.getName(),
            auth.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
        );
    }
}
