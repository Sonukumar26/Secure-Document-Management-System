package com.sdms.dto;

import com.sdms.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registration request payload")
public class RegisterRequest {

    @NotBlank
    @Schema(description = "Desired username")
    private String username;

    @NotBlank
    @Size(min = 3)
    @Schema(description = "Desired password")
    private String password;

    @Schema(description = "User's role")
    private Role role;   // 👈 ENUM

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
