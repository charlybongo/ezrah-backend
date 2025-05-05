package com.example.ezra.controllers;

import com.example.ezra.dtos.LoginResponse;
import com.example.ezra.enums.Roles;
import com.example.ezra.models.authModel.User;
import com.example.ezra.repositories.UserRepository;
import com.example.ezra.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for User Authentication")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        String result = String.valueOf(authService.register(user));

        Map<String, String> response = new HashMap<>();
        response.put("message", result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate a user and return a JWT token with user details")
    public ResponseEntity<LoginResponse> login(@RequestParam String email, @RequestParam String password) {
        LoginResponse loginResponse = authService.login(email, password);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/update/{userId}")
    @Operation(summary = "Update User Details", description = "Update user profile information")
    public ResponseEntity<String> updateUser(@PathVariable UUID userId, @RequestBody Map<String, Object> updatedFields) {
        // Fetch the existing user from the database
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found!"));

        // Loop through the fields in the updatedFields map and update the user's properties
        updatedFields.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    existingUser.setFirstName((String) value);
                    break;
                case "lastName":
                    existingUser.setLastName((String) value);
                    break;
                case "email":
                    existingUser.setEmail((String) value);
                    break;
                case "phone":
                    existingUser.setPhone((String) value);
                    break;
                case "country":
                    existingUser.setCountry((String) value);
                    break;
                case "houseNo":
                    existingUser.setHouseNo((String) value);
                    break;
                case "postalCode":
                    existingUser.setPostalCode((String) value);
                    break;
                case "password":
                    if (value != null && !((String) value).isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode((String) value));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field: " + key);
            }
        });

        userRepository.save(existingUser);
        return ResponseEntity.ok("User details updated successfully!");
    }


    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Invalidate the current user's JWT token")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}