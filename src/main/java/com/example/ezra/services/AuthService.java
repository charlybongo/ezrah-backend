package com.example.ezra.services;

import com.example.ezra.dtos.LoginResponse;
import com.example.ezra.enums.Roles;
import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<Map<String, String>> register(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalStateException("Email already exists!");
                });

        // ✅ Hash password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // ✅ Assign default role
        user.setRole(Roles.CUSTOMER);

        userRepository.save(user);

        // ✅ Return JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");

        return ResponseEntity.ok(response);
    }

    public LoginResponse login(String email, String password) {
        System.out.println("🔹 Attempting to authenticate user: " + email);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            throw new BadCredentialsException("Invalid email or password!");
        }

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found in database!"));

        System.out.println("✅ User authenticated: " + user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        System.out.println("✅ Generated JWT Token: " + token);

        return new LoginResponse(token, user);
    }

    public String updateUserDetails(UUID userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found!"));

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            Optional<User> userWithEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (userWithEmail.isPresent()) {
                throw new IllegalStateException("Email already in use!");
            }
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setCountry(updatedUser.getCountry());
        existingUser.setHouseNo(updatedUser.getHouseNo());
        existingUser.setPostalCode(updatedUser.getPostalCode());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existingUser);
        return "User details updated successfully!";
    }
}
