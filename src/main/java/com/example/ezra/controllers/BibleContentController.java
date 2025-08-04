package com.example.ezra.controllers;

import com.example.ezra.helpers.PagedResponse;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.dtos.BulkContentUpdateRequest;
import com.example.ezra.services.BibleContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<BibleContent>> updateMultipleContents(@RequestBody BulkContentUpdateRequest request,
                                                                    @RequestHeader("Authorization") String bearerToken) {
        String token = extractToken(bearerToken);
        List<BibleContent> updatedContents = bibleContentService.updateMultipleContents(request.getUpdates(), request.getDeleteIds(), token);
        return ResponseEntity.ok(updatedContents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@RequestHeader("Authorization") String bearerToken, @PathVariable Long id, @RequestParam int page, @RequestParam int size) {
        try {
            String token = extractToken(bearerToken);
            Pageable pageable = PageRequest.of(page, size);
            bibleContentService.deleteContent(id, token, pageable);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<PagedResponse<BibleContent>> getAllRootContent(@RequestHeader("Authorization") String bearerToken, @RequestParam int page, @RequestParam int size) {
        String token = extractToken(bearerToken);
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BibleContent> contentList = bibleContentService.getRootContent(token, pageable);
        return ResponseEntity.ok(contentList);
    }

    @GetMapping("/chapters")
    public ResponseEntity<PagedResponse<BibleContent>> getChaptersWithSubContents(@RequestHeader("Authorization") String bearerToken, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> chapters = bibleContentService.getAllChaptersWithSubContents(token, pageable);
        if (chapters.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }
    @DeleteMapping("/{id}/cascade")
    public ResponseEntity<Void> deleteContentTree(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long id) {
        try {
            String token = extractToken(bearerToken);
            bibleContentService.deleteContentAndAllChildren(id, token);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<BibleContent> getContentWithSubContents(@RequestHeader("Authorization") String bearerToken, @PathVariable Long id) {
        String token = extractToken(bearerToken);
        Optional<BibleContent> content = bibleContentService.getContentById(id, token);
        return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("language/root")
    public ResponseEntity<PagedResponse<BibleContent>> getRootContentByLanguage(@RequestParam String language, @RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String bearerToken) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> content = bibleContentService.getRootContentByLanguage(language, token, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("language/all")
    public ResponseEntity<PagedResponse<BibleContent>> getAllChaptersWithSubContentsByLanguage(@RequestParam String language, @RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String bearerToken) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> content = bibleContentService.getAllChaptersWithSubContentsByLanguage(language, token, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("language/{id}")
    public ResponseEntity<Optional<BibleContent>> getContentByIdAndLanguage(@PathVariable Long id, @RequestParam String language, @RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String bearerToken) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        Optional<BibleContent> content = bibleContentService.getContentByIdAndLanguage(id, language, token, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<BibleContent>> searchContent(@RequestParam String keyword, @RequestHeader("Authorization") String bearerToken, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> searchResults = bibleContentService.searchContent(keyword, token, pageable);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/searchByLanguage")
    public ResponseEntity<PagedResponse<BibleContent>> searchContentByLanguage(@RequestParam String keyword, @RequestParam String language, @RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String bearerToken) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> searchResults = bibleContentService.searchContentByLanguage(keyword, language, token, pageable);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("unsubscribed/language")
    public ResponseEntity<PagedResponse<BibleContent>> getUnsubscribedContentByLanguage(@RequestParam String language, @RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String bearerToken) {
        Pageable pageable = PageRequest.of(page, size);
        String token = extractToken(bearerToken);
        PagedResponse<BibleContent> unsubscribedContent = bibleContentService.getUnsubscribedContentByLanguage(language, token, pageable);
        return ResponseEntity.ok(unsubscribedContent);
    }

    @GetMapping("/group")
    public ResponseEntity<List<BibleContent>> getByChapterGroupAndLanguage(
            @RequestParam Long groupId,
            @RequestParam String language,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = extractToken(bearerToken);
        List<BibleContent> response = bibleContentService.getByChapterGroupAndLanguage(groupId, language, token);
        return ResponseEntity.ok(response);
    }


}
