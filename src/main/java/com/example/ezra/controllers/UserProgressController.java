package com.example.ezra.controllers;

import com.example.ezra.models.userProgress.UserProgress;
import com.example.ezra.services.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/progress")
public class UserProgressController {

    @Autowired
    private UserProgressService userProgressService;

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // ✅ Remove "Bearer " prefix
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }

    // ✅ Mark verse as read
    @PostMapping("/read")
    public UserProgress markVerseAsRead(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam UUID userId,
            @RequestParam Long verseId,
            @RequestParam int progress) {

        String token = extractToken(bearerToken);
        return userProgressService.markVerseAsRead(userId, verseId, progress, token);
    }

    // ✅ Fetch progress for a specific user
    @GetMapping("/user/{userId}")
    public List<UserProgress> getUserProgress(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable UUID userId) {

        String token = extractToken(bearerToken);
        return userProgressService.getUserProgressByUserId(userId, token);
    }

    // ✅ Fetch progress for a specific verse
    @GetMapping("/verse/{verseId}")
    public List<UserProgress> getUserProgressByVerse(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long verseId) {

        String token = extractToken(bearerToken);
        return userProgressService.getUserProgress(verseId, token);
    }

    // ✅ Fetch progress for an entire chapter
    @GetMapping("/chapter/{chapterId}")
    public List<UserProgress> getProgressByChapter(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long chapterId) {

        String token = extractToken(bearerToken);
        return userProgressService.getProgressByChapter(chapterId, token);
    }
}
