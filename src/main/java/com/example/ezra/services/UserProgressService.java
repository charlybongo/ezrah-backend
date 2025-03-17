package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import com.example.ezra.models.userProgress.UserProgress;
import com.example.ezra.repositories.BibleContentRepository;
import com.example.ezra.repositories.UserProgressRepository;
import com.example.ezra.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ✅ Mark a verse as read and update progress
    public UserProgress markVerseAsRead(UUID userId, Long verseId, int pagesCovered, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        // ✅ Ensure progress only sets the passed value, not accumulates it
        progress.setProgress(pagesCovered);

        return progressRepository.save(progress);
    }


    // ✅ Fetch user progress by verse ID
    public List<UserProgress> getUserProgress(Long verseId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return progressRepository.findByVerseId(verseId);
    }

    // ✅ Fetch user progress by user ID
    public List<UserProgress> getUserProgressByUserId(UUID userId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return progressRepository.findByUserId(userId);
    }

    // ✅ Fetch progress for all verses in a chapter
    public List<UserProgress> getProgressByChapter(Long chapterId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtil.validateToken(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        List<BibleContent> versesInChapter = bibleContentRepository.findByParentId(chapterId);

        return versesInChapter.stream()
                .flatMap(verse -> progressRepository.findByVerseId(verse.getId()).stream())
                .collect(Collectors.toList());
    }
}
