package com.example.ezra.repositories;

import com.example.ezra.models.userSubscription.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByUserId(UUID userId);


    boolean existsByUserIdAndChapterId(UUID userId, Long chapterId);
}
