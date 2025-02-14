package com.example.ezra.controllers;

import com.example.ezra.enums.Roles;
import com.example.ezra.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for User Authentication") // ✅ Add Swagger Tag
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account") // ✅ Add Swagger Operation
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam Roles role) {
        return authService.register(email, password, Roles.valueOf(String.valueOf(role)));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate a user and return a token") // ✅ Add Swagger Operation
    public String login(@RequestParam String email, @RequestParam String password) {
        return authService.login(email, password);
    }
}
