package com.example.ezra.controllers;

import com.example.ezra.models.imageModel.Image;
import com.example.ezra.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("file") MultipartFile file) throws IOException {

        String token = extractToken(bearerToken);
        Long id = imageService.saveImage(file);
        return ResponseEntity.ok("Image uploaded successfully with ID: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long id) {

        String token = extractToken(bearerToken);
        Image image = imageService.getImageById(id);
        return ResponseEntity.ok()
                .header("Content-Type", image.getMimetype())
                .body(image.getData());
    }

    @GetMapping
    public List<Image> getAllImages(
            @RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        return imageService.getAllImages();
    }
}
