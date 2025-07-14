package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.models.userProgress.UserProgress;
import com.example.ezra.repositories.BibleContentRepository;
import com.example.ezra.repositories.UserProgressRepository;
import com.example.ezra.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class UserProgressService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BibleContentRepository bibleContentRepository;
    @Autowired
    private UserProgressRepository progressRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public UserProgress markVerseAsRead(UUID userId, Long verseId, int pagesCovered, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<BibleContent> allVerses = bibleContentRepository.findAll();
        System.out.println("Fetched " + allVerses.size() + " entries");
        allVerses.forEach(v -> System.out.println("Verse ID: " + v.getId()));

        BibleContent verse = bibleContentRepository.findById(verseId)
                .orElseThrow(() -> new RuntimeException("Verse not found"));

        BibleContent chapter = bibleContentRepository.findById(verse.getParentId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        List<UserProgress> progressList = progressRepository.findByVerseId(verseId);

        UserProgress progress;
        if (!progressList.isEmpty()) {
            progress = progressList.get(0);
        } else {
            progress = new UserProgress(user, verse, 0, chapter);
        }

        progress.setProgress(pagesCovered);

        return progressRepository.save(progress);
    }


    public List<UserProgress> getUserProgress(Long verseId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return progressRepository.findByVerseId(verseId);
    }

    public List<UserProgress> getUserProgressByUserId(UUID userId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return progressRepository.findByUserId(userId);
    }

    public List<UserProgress> getProgressByChapter(Long chapterId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }
        Pageable pageable = PageRequest.of(0, 10);
        List<BibleContent> versesInChapter =  bibleContentRepository.findByParentId(chapterId,pageable).getContent();

        return versesInChapter.stream()
                .flatMap(verse -> progressRepository.findByVerseId(verse.getId()).stream())
                .collect(Collectors.toList());
    }
}
