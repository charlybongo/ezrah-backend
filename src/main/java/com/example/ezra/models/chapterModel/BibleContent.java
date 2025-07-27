package com.example.ezra.models.chapterModel;

import com.example.ezra.models.book.BookModel;
import com.example.ezra.models.imageModel.Image;
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

    private Long parentId;
    private Long chapterGroup;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String language;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Transient
    private List<BibleContent> children;

    // ✅ Add book reference
    @ManyToOne(optional = true) // allow null
    @JoinColumn(name = "book_id", nullable = true)
    private BookModel book;


    @ManyToOne(optional = true)
    @JoinColumn(name = "image_id", nullable = true)
    private Image image;
}
