package com.example.ezra.services;

import com.example.ezra.models.book.BookModel;
import com.example.ezra.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String getBookNameById(Long id) {
        return bookRepository.findBookNameById(id);
    }

    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }
}
