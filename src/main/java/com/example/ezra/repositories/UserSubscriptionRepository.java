package com.example.ezra.repositories;

import com.example.ezra.models.userSubscription.UserSubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByUserId(UUID userId);

    List<UserSubscription> findByChapterId(Long chapterId);
    boolean existsByUserIdAndChapterId(UUID userId, Long chapterId);
}
