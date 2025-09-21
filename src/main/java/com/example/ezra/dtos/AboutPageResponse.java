package com.example.ezra.dtos;

import com.example.ezra.models.pages.AboutPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutPageResponse {
    private Long id;
    private String language;
    private String title;
    private String content;
    private Map<String, Object> metadata;

    public static AboutPageResponse fromEntity(AboutPage entity) {
        return new AboutPageResponse(
                entity.getId(),
                entity.getLanguage(),
                entity.getTitle(),
                entity.getContent(),
                entity.getMetadata()
        );
    }
}
