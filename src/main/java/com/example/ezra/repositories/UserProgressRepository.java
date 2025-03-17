package com.example.ezra.repositories;

import com.example.ezra.models.userProgress.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    // ✅ Find all progress records for a specific verse
    List<UserProgress> findByVerseId(Long verseId);

    // ✅ Find all progress records for a specific user
    List<UserProgress> findByUserId(UUID userId);
}
