package com.example.ezra.repositories;

import com.example.ezra.models.imageModel.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
