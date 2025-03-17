package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.repositories.BibleContentRepository;
import com.example.ezra.repositories.UserRepository;
import com.example.ezra.repositories.UserSubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BibleContentService {

    @Autowired
    private BibleContentRepository bibleContentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public BibleContent saveContent(BibleContent content, Long parentId, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);

        if (parentId != null && parentId != 0) {
            Optional<BibleContent> parent = bibleContentRepository.findById(parentId);
            if (parent.isEmpty()) {
                throw new RuntimeException("Parent with id " + parentId + " does not exist");
            }
            content.setParentId(parentId);
        } else {
            content.setParentId(null);
        }

        BibleContent savedContent = bibleContentRepository.save(content);
        if (content.getChildren() != null) {
            for (BibleContent child : content.getChildren()) {
                saveContent(child, savedContent.getId(), token);
            }
        }

        return savedContent;
    }

    public List<BibleContent> getRootContent(String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        return bibleContentRepository.findByParentId(null);
    }


    public List<BibleContent> getAllChaptersWithSubContents(String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        List<BibleContent> rootChapters = bibleContentRepository.findByParentId(null);
        return rootChapters.stream()
                .map(this::loadSubContents)
                .collect(Collectors.toList());
    }

    private BibleContent loadSubContents(BibleContent content) {
        List<BibleContent> children = bibleContentRepository.findByParentId(content.getId());
        content.setChildren(children.stream().map(this::loadSubContents).collect(Collectors.toList()));
        return content;
    }

    public Optional<BibleContent> getContentById(Long id, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        Optional<BibleContent> content = bibleContentRepository.findById(id);
        return content.map(this::loadSubContents);
    }

    public BibleContent updateContent(Long id, BibleContent updatedContent, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        return bibleContentRepository.findById(id)
                .map(existingContent -> {
                    existingContent.setType(updatedContent.getType());
                    existingContent.setContent(updatedContent.getContent());
                    existingContent.setMetadata(updatedContent.getMetadata());
                    return bibleContentRepository.save(existingContent);
                }).orElseThrow(() -> new RuntimeException("Content not found"));
    }

    public void deleteContent(Long id, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        List<BibleContent> children = bibleContentRepository.findByParentId(id);
        for (BibleContent child : children) {
            deleteContent(child.getId(), token);
        }
        bibleContentRepository.deleteById(id);
    }

    public List<BibleContent> getRootContentByLanguage(String language, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        return bibleContentRepository.findByParentIdAndLanguage(null, language);
    }

    public List<BibleContent> getAllChaptersWithSubContentsByLanguage(String language, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        List<BibleContent> rootChapters = bibleContentRepository.findByParentIdAndLanguage(null, language);
        return rootChapters.stream()
                .map(this::loadSubContents)
                .collect(Collectors.toList());
    }
    public List<BibleContent> searchContent(String keyword, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);

        return bibleContentRepository.searchByContent(keyword);
    }
    public List<BibleContent> searchContentByLanguage(String keyword, String language, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);

        return bibleContentRepository.searchByContentAndLanguage(keyword, language);
    }

    public Optional<BibleContent> getContentByIdAndLanguage(Long id, String language, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        Optional<BibleContent> content = bibleContentRepository.findByIdAndLanguage(id, language);
        return content.map(this::loadSubContents);
    }
    public List<BibleContent> getUnsubscribedContentByLanguage(String language, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);

        // ✅ Get all chapters the user is subscribed to
        List<Long> subscribedChapterIds = userSubscriptionRepository.findByUserId(user.getId())
                .stream()
                .map(subscription -> subscription.getChapter().getId()) // Extract chapter IDs
                .toList();

        // ✅ If user has no subscriptions, return all content for the language
        if (subscribedChapterIds.isEmpty()) {
            return bibleContentRepository.findByLanguage(language);
        }

        // ✅ Fetch content not in subscribed list
        return bibleContentRepository.findUnsubscribedContentByLanguage(language, subscribedChapterIds);
    }

    @Transactional
    public List<BibleContent> updateMultipleContents(List<BibleContent> updates, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);


        List<Long> ids = updates.stream().map(BibleContent::getId).toList();

        // ✅ Find the existing content in the database
        List<BibleContent> existingContents = bibleContentRepository.findAllById(ids);

        if (existingContents.isEmpty()) {
            throw new RuntimeException("No valid content found for the provided IDs");
        }

        // ✅ Update the fields for each content item
        for (BibleContent existing : existingContents) {
            for (BibleContent update : updates) {
                if (existing.getId().equals(update.getId())) {
                    existing.setType(update.getType());
                    existing.setContent(update.getContent());
                    existing.setMetadata(update.getMetadata());
                }
            }
        }

        // ✅ Save all updates in bulk
        return bibleContentRepository.saveAll(existingContents);
    }

}
