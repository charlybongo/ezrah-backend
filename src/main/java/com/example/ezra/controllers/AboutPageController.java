package com.example.ezra.controllers;

import com.example.ezra.dtos.AboutPageRequest;
import com.example.ezra.dtos.AboutPageResponse;
import com.example.ezra.services.AboutPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages")
public class AboutPageController {

    @Autowired
    private AboutPageService aboutPageService;

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }

    // Admin: create or update the About page for a language
    @PostMapping("/about")
    public ResponseEntity<AboutPageResponse> upsertAbout(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody AboutPageRequest request
    ) {
        String token = extractToken(bearerToken);
        AboutPageResponse response = aboutPageService.upsertAboutPage(request, token);
        return ResponseEntity.ok(response);
    }
}

