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

        System.out.println("Chapter Group: " + chapterGroup);
        Pageable pageable = PageRequest.of(0, 10);
  BibleContent matchingContent = bibleContentRepository
                .findFirstByChapterGroupAndLanguageAndType(chapterGroup, language.toLowerCase(), "Chapter") // Ensure lowercase matching
                .orElseThrow(() -> new RuntimeException("No content found for the given language and type"));

        System.out.println("Matching Content Found: " + matchingContent.getId());
  List<BibleContent> chaptersInGroup = bibleContentRepository.findByChapterGroup(chapterGroup,pageable).getContent();
        System.out.println("Chapters in Group: " + chaptersInGroup.size());

        List<UserSubscription> newSubscriptions = chaptersInGroup.stream()
                .filter(chapter -> !subscriptionRepository.existsByUserIdAndChapterId(userId, chapter.getId())) // Avoid duplicates
                .map(chapter -> new UserSubscription(user, chapter, chapterGroup))
                .toList();

        System.out.println("New Subscriptions Count: " + newSubscriptions.size());

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
