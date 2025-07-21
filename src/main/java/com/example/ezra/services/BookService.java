package com.example.ezra.services;

import com.example.ezra.helpers.JwtUtil;
import com.example.ezra.models.authModel.User;
import com.example.ezra.models.book.BookModel;
import com.example.ezra.repositories.BookRepository;
import com.example.ezra.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String getBookNameById(Long id) {
        return bookRepository.findBookNameById(id);
    }


    public BookModel addBook(BookModel bookModel, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);
        return bookRepository.save(bookModel);
    }

    public List<BookModel> getAllBooks( String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        return bookRepository.findAll();
    }

    public void deleteBookById(Long id, String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jwtUtil.validateToken(token, user);

        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book with ID " + id + " does not exist");
        }

        bookRepository.deleteById(id);
    }

}
