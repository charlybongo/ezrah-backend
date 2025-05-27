package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.models.userSubscription.UserSubscription;
import com.example.ezra.repositories.BibleContentRepository;
import com.example.ezra.repositories.UserRepository;
import com.example.ezra.repositories.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionService {

    private final UserRepository userRepository;
    private final BibleContentRepository bibleContentRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserSubscriptionService(UserRepository userRepository,
                                   BibleContentRepository bibleContentRepository,
                                   UserSubscriptionRepository subscriptionRepository,
                                   JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bibleContentRepository = bibleContentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.jwtUtil = jwtUtil;
    }

    public BibleContent subscribeUser(UUID userId, Long chapterId, String language, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        BibleContent selectedChapter = bibleContentRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        Long chapterGroup = selectedChapter.getChapterGroup();
        Pageable pageable = PageRequest.of(0, 10);
        List<BibleContent> matchingContents = bibleContentRepository
                .findFirstByChapterGroupAndLanguageAndType(chapterGroup, language.toLowerCase(), "Chapter");

        if (matchingContents.isEmpty()) {
            throw new RuntimeException("No content found for the given language and type");
        }

        BibleContent matchingContent = matchingContents.get(0);
        List<BibleContent> chaptersInGroup = bibleContentRepository.findByChapterGroup(chapterGroup, pageable).getContent();

        List<UserSubscription> newSubscriptions = chaptersInGroup.stream()
                .filter(chapter -> !subscriptionRepository.existsByUserIdAndChapterId(userId, chapter.getId()))
                .map(chapter -> new UserSubscription(user, chapter, chapterGroup))
                .toList();

        subscriptionRepository.saveAll(newSubscriptions);

        return matchingContent;
    }

    public List<UserSubscription> getUserSubscriptions(UUID userId, String language, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }
        return subscriptionRepository.findByUserId(userId).stream()
                .filter(subscription -> subscription.getChapter().getLanguage().equalsIgnoreCase(language))
                .collect(Collectors.toList());
    }
}
