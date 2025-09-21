package com.example.ezra.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class AboutPageRequest {
    private Long id; // optional: update specific record
    private String language; // e.g., "en"
    private String title;    // optional title
    private String content;  // HTML/Markdown/Text
    private Map<String, Object> metadata; // optional
}
