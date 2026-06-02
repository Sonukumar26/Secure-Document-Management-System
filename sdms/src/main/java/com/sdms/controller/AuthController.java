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

@RestController
@RequestMapping("/auth")
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

    // ================= LOGIN =================

    @PostMapping("/login")
    public Map<String, String> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String token =
                jwtUtil.generateToken(authentication.getName());

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

    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request) {

        if (userRepository
                .findByUsername(request.getUsername())
                .isPresent()) {

            throw new RuntimeException("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());

        // 🔐 BCrypt
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        // ✅ DIRECT ENUM
        user.setRole(request.getRole());cd 

        userRepository.save(user);

        return "User Registered Successfully";
    }


    // ================= CURRENT USER =================

    @GetMapping("/me")
    public UserInfoResponse me(Authentication auth) {

        if (auth == null) {
            throw new RuntimeException(
                "Unauthorized - JWT not processed");
        }

        return new UserInfoResponse(
            auth.getName(),
            auth.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
        );
    }
}
