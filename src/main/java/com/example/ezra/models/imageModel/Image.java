package com.example.ezra.models.imageModel;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String mimetype;


    @Column(name = "data", columnDefinition = "BYTEA") // Ensure bytea in PostgreSQL
    private byte[] data;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
