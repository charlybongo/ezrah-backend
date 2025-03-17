package com.example.ezra.models.chapterModel;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class BibleContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;  // Links to another BibleContent (Verse -> Chapter)
    private Long chapterGroup;
    private String type; // e.g., chapter, verse, statement, question, etc.

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String language;

    @JdbcTypeCode(SqlTypes.JSON)  // ✅ Correct way for Hibernate 6+ with PostgreSQL JSONB
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // Price field

    @Transient
    private List<BibleContent> children; // Used for nested data
}
