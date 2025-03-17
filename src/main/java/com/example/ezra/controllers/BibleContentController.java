package com.example.ezra.controllers;

import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.services.BibleContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bible-content")
public class BibleContentController {

    @Autowired
    private BibleContentService bibleContentService;
    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }

    @PostMapping("/bulk-insert")
    public ResponseEntity<BibleContent> createContentTree(@RequestHeader("Authorization") String bearerToken, @RequestBody BibleContent content) {
        String token = extractToken(bearerToken);
        BibleContent savedContent = bibleContentService.saveContent(content, content.getParentId(), token);
        return ResponseEntity.ok(savedContent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BibleContent> updateContent(@RequestHeader("Authorization") String bearerToken, @PathVariable Long id, @RequestBody BibleContent updatedContent) {
        try {

            String token = extractToken(bearerToken);
            BibleContent updated = bibleContentService.updateContent(id, updatedContent, token);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/bulk-update")
    public ResponseEntity<List<BibleContent>> updateMultipleContents(
            @RequestBody List<BibleContent> updates,
            @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        List<BibleContent> updatedContents = bibleContentService.updateMultipleContents(updates, token);
        return ResponseEntity.ok(updatedContents);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@RequestHeader("Authorization") String bearerToken, @PathVariable Long id) {
        try {
            String token = extractToken(bearerToken);
            bibleContentService.deleteContent(id, token);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BibleContent>> getAllRootContent(@RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        List<BibleContent> contentList = bibleContentService.getRootContent(token);
        return ResponseEntity.ok(contentList);
    }

    @GetMapping("/chapters")
    public ResponseEntity<List<BibleContent>> getChaptersWithSubContents(@RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        List<BibleContent> chapters = bibleContentService.getAllChaptersWithSubContents(token);
        if (chapters.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<BibleContent> getContentWithSubContents(@RequestHeader("Authorization") String bearerToken, @PathVariable Long id) {
        String token = extractToken(bearerToken);
        Optional<BibleContent> content = bibleContentService.getContentById(id, token);
        return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("language/root")
    public ResponseEntity<List<BibleContent>> getRootContentByLanguage(
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        return ResponseEntity.ok(bibleContentService.getRootContentByLanguage(language, token));
    }

    @GetMapping("language/all")
    public ResponseEntity<List<BibleContent>> getAllChaptersWithSubContentsByLanguage(
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        return ResponseEntity.ok(bibleContentService.getAllChaptersWithSubContentsByLanguage(language, token));
    }

    @GetMapping("language/{id}")
    public ResponseEntity<Optional<BibleContent>> getContentByIdAndLanguage(
            @PathVariable Long id,
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        return ResponseEntity.ok(bibleContentService.getContentByIdAndLanguage(id, language, token));
    }
    @GetMapping("/search")
    public List<BibleContent> searchContent(@RequestParam String keyword, @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        return bibleContentService.searchContent(keyword, token);
    }
    @GetMapping("/searchByLanguage")
    public List<BibleContent> searchContentByLanguage(
            @RequestParam String keyword,
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        return bibleContentService.searchContentByLanguage(keyword, language, token);
    }
    @GetMapping("unsubscribed/language")
    public ResponseEntity<List<BibleContent>> getUnsubscribedContentByLanguage(
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        List<BibleContent> unsubscribedContent = bibleContentService.getUnsubscribedContentByLanguage(language, token);

        return ResponseEntity.ok(unsubscribedContent);
    }

}
