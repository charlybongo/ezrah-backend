package com.example.ezra.models.userSubscription;

import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chapter_id"}))
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private BibleContent chapter;

    @Column(name = "chapter_group", nullable = false)
    private Long chapterGroup; // ✅ New Field

    @Column(nullable = false)
    private LocalDateTime subscribedAt = LocalDateTime.now();

    // ✅ Constructors
    public UserSubscription() {}

    public UserSubscription(User user, BibleContent chapter, Long chapterGroup) {
        this.user = user;
        this.chapter = chapter;
        this.chapterGroup = chapterGroup;
        this.subscribedAt = LocalDateTime.now();
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BibleContent getChapter() {
        return chapter;
    }

    public void setChapter(BibleContent chapter) {
        this.chapter = chapter;
    }

    public Long getChapterGroup() {
        return chapterGroup;
    }

    public void setChapterGroup(Long chapterGroup) {
        this.chapterGroup = chapterGroup;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }

    @Override
    public String toString() {
        return "UserSubscription{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", chapter=" + chapter.getId() +
                ", chapterGroup=" + chapterGroup +
                ", subscribedAt=" + subscribedAt +
                '}';
    }
}
