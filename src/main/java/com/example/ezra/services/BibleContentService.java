package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.helpers.PagedResponse;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.models.imageModel.Image;
import com.example.ezra.models.userProgress.UserProgress;
import com.example.ezra.models.userSubscription.UserSubscription;
import com.example.ezra.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private ImageRepository imageRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;
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

        // Save the image first if present
        if (content.getImage() != null && content.getImage().getId() == null) {
            Image savedImage = imageRepository.save(content.getImage());
            content.setImage(savedImage);
        }

        BibleContent savedContent = bibleContentRepository.save(content);

        if (content.getChildren() != null) {
            for (BibleContent child : content.getChildren()) {
                saveContent(child, savedContent.getId(), token);
            }
        }

        return savedContent;
    }

    public PagedResponse<BibleContent> getRootContent(String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        Page<BibleContent> pageResult = bibleContentRepository.findByParentId(null, pageable);
        return new PagedResponse<>(pageResult);
    }
    public PagedResponse<BibleContent> getAllChaptersWithSubContents(String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        Page<BibleContent> rootChapters = bibleContentRepository.findByParentId(null, pageable);
        List<BibleContent> contentList = rootChapters.stream()
                .map(content -> loadSubContents(content, pageable))
                .toList();
        return new PagedResponse<>(rootChapters);
    }

    private BibleContent loadSubContents(BibleContent content, Pageable pageable) {
        List<BibleContent> children = bibleContentRepository.findByParentId(content.getId(), pageable).getContent();
        content.setChildren(children.stream()
                .map(child -> loadSubContents(child, pageable))
                .collect(Collectors.toList()));
        return content;
    }

    public Optional<BibleContent> getContentById(Long id, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        Pageable pageable = PageRequest.of(0, 50);
        Optional<BibleContent> content = bibleContentRepository.findById(id);
        return content.map(c -> loadSubContents(c, pageable));
    }

    public BibleContent updateContent(Long id, BibleContent updatedContent, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        return bibleContentRepository.findById(id)
                .map(existingContent -> {
                    existingContent.setType(updatedContent.getType());
                    existingContent.setChapterGroup(updatedContent.getChapterGroup());
                    existingContent.setBook(updatedContent.getBook());
                    if (updatedContent.getImage() != null && updatedContent.getImage().getId() == null) {
                        Image savedImage = imageRepository.save(updatedContent.getImage());
                        updatedContent.setImage(savedImage);
                    }
                    existingContent.setContent(updatedContent.getContent());
                    existingContent.setMetadata(updatedContent.getMetadata());
                    return bibleContentRepository.save(existingContent);
                }).orElseThrow(() -> new RuntimeException("Content not found"));
    }

    public void deleteContent(Long id, String token, Pageable pageable) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        List<BibleContent> children =  bibleContentRepository.findByParentId(id,pageable).getContent();
        for (BibleContent child : children) {
            deleteContent(child.getId(), token, pageable);
        }
        bibleContentRepository.deleteById(id);
    }

    public PagedResponse<BibleContent> getRootContentByLanguage(String language, String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        Page<BibleContent> pageResult = bibleContentRepository.findByParentIdAndLanguage(null, language, pageable);
        return new PagedResponse<>(pageResult);
    }

    public PagedResponse<BibleContent> getAllChaptersWithSubContentsByLanguage(String language, String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        Page<BibleContent> rootChapters = bibleContentRepository.findByParentIdAndLanguage(null, language, pageable);
        List<BibleContent> contentList = rootChapters.stream()
                .map(content -> loadSubContents(content, pageable))
                .toList();
        return new PagedResponse<>(rootChapters);
    }
    public PagedResponse<BibleContent> searchContent(String keyword, String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        Page<BibleContent> searchResult = bibleContentRepository.searchByContent(keyword, pageable);
        return new PagedResponse<>(searchResult);
    }
    public PagedResponse<BibleContent> searchContentByLanguage(String keyword, String language, String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        Page<BibleContent> searchResult = bibleContentRepository.searchByContentAndLanguage(keyword, language, pageable);
        return new PagedResponse<>(searchResult);
    }

    public Optional<BibleContent> getContentByIdAndLanguage(Long id, String language, String token, Pageable pageable) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token,user);
        Optional<BibleContent> content = bibleContentRepository.findByIdAndLanguage(id, language);
        return content.map(c -> loadSubContents(c, pageable));
    }
    public List<BibleContent> getByChapterGroupAndLanguage(Long chapterGroup, String language, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        List<BibleContent> roots = bibleContentRepository.findByChapterGroupAndLanguage(chapterGroup, language);
        Pageable pageable = PageRequest.of(0, 50);
        return roots.stream()
                .map(root -> loadSubContents(root,pageable)) // include children recursively
                .collect(Collectors.toList());
    }



    public PagedResponse<BibleContent> getUnsubscribedContentByLanguage(String language, String token, Pageable pageable) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtUtil.validateToken(token, user);
        List<Long> subscribedChapterIds = userSubscriptionRepository.findByUserId(user.getId())
                .stream()
                .map(subscription -> subscription.getChapter().getId())
                .collect(Collectors.toList());
        Page<BibleContent> pageResult;
        if (subscribedChapterIds.isEmpty()) {
            pageResult = bibleContentRepository.findByLanguage(language, pageable);
        } else {
            pageResult = bibleContentRepository.findUnsubscribedContentByLanguage(language, subscribedChapterIds, pageable);
        }
        return new PagedResponse<>(pageResult);
    }

    @Transactional
    public List<BibleContent> updateMultipleContents(List<BibleContent> updates, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        List<Long> ids = updates.stream().map(BibleContent::getId).toList();
        List<BibleContent> existingContents = bibleContentRepository.findAllById(ids);

        if (existingContents.isEmpty()) {
            throw new RuntimeException("No valid content found for the provided IDs");
        }
        for (BibleContent existing : existingContents) {
            for (BibleContent update : updates) {
                if (existing.getId().equals(update.getId())) {
                    existing.setType(update.getType());
                    existing.setContent(update.getContent());
                    existing.setMetadata(update.getMetadata());
                }
            }
        }
        return bibleContentRepository.saveAll(existingContents);
    }
    @Transactional
    public void deleteContentAndAllChildren(Long id, String token) {
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        List<UserProgress>  progerssDelete= userProgressRepository.findByChapterId(id);
        userProgressRepository.deleteAll(progerssDelete);

        List<UserSubscription>  subscriptionDelete= userSubscriptionRepository.findByChapterId(id);
        userSubscriptionRepository.deleteAll(subscriptionDelete);
        deleteChildrenRecursively(id);
        bibleContentRepository.deleteById(id);
    }

    private void deleteChildrenRecursively(Long parentId) {
        List<BibleContent> children = bibleContentRepository.findByParentId(parentId);
        for (BibleContent child : children) {
            deleteChildrenRecursively(child.getId()); // Recursively delete subchildren
            bibleContentRepository.deleteById(child.getId());
        }
    }

    private List<Long> collectAllChapterIds(Long parentId) {
        List<Long> ids = new ArrayList<>();
        ids.add(parentId);
        List<BibleContent> children = bibleContentRepository.findByParentId(parentId);
        for (BibleContent child : children) {
            ids.addAll(collectAllChapterIds(child.getId()));
        }
        return ids;
    }

}
