package com.example.ezra.services;

import com.example.ezra.dtos.AboutPageRequest;
import com.example.ezra.dtos.AboutPageResponse;
import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.pages.AboutPage;
import com.example.ezra.repositories.AboutPageRepository;
import com.example.ezra.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AboutPageService {

    @Autowired
    private AboutPageRepository aboutPageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public Optional<AboutPageResponse> getAboutPage(String language) {
        Optional<AboutPage> byLang = aboutPageRepository.findFirstByLanguage(language);
        if (byLang.isPresent()) {
            return byLang.map(AboutPageResponse::fromEntity);
        }
        return aboutPageRepository.findFirstByOrderByIdAsc().map(AboutPageResponse::fromEntity);
    }

    public AboutPageResponse upsertAboutPage(AboutPageRequest request, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        AboutPage target = null;

        if (request.getId() != null) {
            target = aboutPageRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("About content not found"));
        } else if (request.getLanguage() != null) {
            target = aboutPageRepository.findFirstByLanguage(request.getLanguage()).orElse(null);
        }

        if (target == null) {
            target = new AboutPage();
            target.setLanguage(request.getLanguage());
        }

        if (request.getTitle() != null) target.setTitle(request.getTitle());
        if (request.getContent() != null) target.setContent(request.getContent());
        if (request.getMetadata() != null) target.setMetadata(request.getMetadata());

        AboutPage saved = aboutPageRepository.save(target);
        return AboutPageResponse.fromEntity(saved);
    }
}
