package com.example.ezra.controllers;

import com.example.ezra.models.book.BookModel;
import com.example.ezra.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // ✅ Remove "Bearer " prefix
        }
        throw new RuntimeException("Authorization token is missing or invalid");
    }

    @GetMapping("/{id}/name")
    public String getBookNameById(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long id) {

        String token = extractToken(bearerToken);
        // Optional: use `token` to verify access or log requests
        return bookService.getBookNameById(id);
    }

    @GetMapping
    public List<BookModel> getAllBooks(
            @RequestHeader("Authorization") String bearerToken) {

        String token = extractToken(bearerToken);
        // Optional: validate token if needed
        return bookService.getAllBooks();
    }
}
