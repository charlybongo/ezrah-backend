package com.example.ezra.services;

import com.example.ezra.models.imageModel.Image;
import com.example.ezra.repositories.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Long saveImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setFilename(file.getOriginalFilename());
        image.setMimetype(file.getContentType());
        byte[] imageBytes = file.getBytes();
        image.setData(imageBytes);
        return imageRepository.save(image).getId();
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }
}
