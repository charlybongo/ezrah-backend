package com.example.ezra.controllers;

import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.models.userSubscription.UserSubscription;
import com.example.ezra.services.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscription")
public class UserSubscriptionController {

    @Autowired
    private UserSubscriptionService userSubscriptionService;
    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }
    @PostMapping("/subscribe")
    public BibleContent subscribeUser(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam UUID userId,
            @RequestParam Long chapterId,
            @RequestParam String language

            ) {

        String token = extractToken(bearerToken);
        return userSubscriptionService.subscribeUser(userId, chapterId, language, token);
    }
    @GetMapping("/{userId}")
    public List<UserSubscription> getUserSubscriptions(@RequestHeader("Authorization") String bearerToken, @RequestParam String language ,@PathVariable UUID userId) {
        String token = extractToken(bearerToken);
        return userSubscriptionService.getUserSubscriptions(userId, language,token);
    }
}
