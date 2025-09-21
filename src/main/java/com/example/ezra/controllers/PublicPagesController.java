package com.example.ezra.controllers;

import com.example.ezra.dtos.AboutPageResponse;
import com.example.ezra.services.AboutPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/public/pages")
public class PublicPagesController {

    @Autowired
    private AboutPageService aboutPageService;

    @GetMapping("/about")
    public ResponseEntity<AboutPageResponse> getAboutPage(
            @RequestParam(name = "language", defaultValue = "en") String language
    ) {
        Optional<AboutPageResponse> about = aboutPageService.getAboutPage(language);
        return about.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
