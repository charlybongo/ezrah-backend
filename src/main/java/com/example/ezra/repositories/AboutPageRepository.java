package com.example.ezra.repositories;

import com.example.ezra.models.pages.AboutPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AboutPageRepository extends JpaRepository<AboutPage, Long> {
    Page<AboutPage> findByLanguage(String language, Pageable pageable);
    Optional<AboutPage> findFirstByLanguage(String language);
    Optional<AboutPage> findFirstByOrderByIdAsc();
}

