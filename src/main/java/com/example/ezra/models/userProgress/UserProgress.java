package com.example.ezra.models.userProgress;

import com.example.ezra.models.authModel.User;
import com.example.ezra.models.chapterModel.BibleContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "user_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "verse_id"}))
public class UserProgress {


    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "verse_id", nullable = false)
    private BibleContent verse;
    @Setter
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private BibleContent chapter;

    @Column(nullable = false)
    private Integer progress = 0; // ✅ Change from int to Integer

    @Setter
    @Column(nullable = false)
    private LocalDateTime readAt = LocalDateTime.now();

    // ✅ Constructors
    public UserProgress() {}

    public UserProgress(User user, BibleContent verse, Integer progress, BibleContent chapter) {
        this.user = user;
        this.verse = verse;
        this.chapter = chapter;
        this.progress = (progress == null) ? 0 : progress; // ✅ Prevent null values
        this.readAt = LocalDateTime.now();
    }

    public void setProgress(Integer progress) {
        this.progress = (progress == null) ? 0 : progress;
    }

    public void incrementProgress(int pages) {
        this.progress += pages;
    }

}