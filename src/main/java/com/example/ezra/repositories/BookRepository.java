package com.example.ezra.repositories;

import com.example.ezra.models.book.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookModel, Long> {

    Optional<BookModel> findByBookName(String bookName);

    @Query("SELECT b.bookName FROM BookModel b WHERE b.id = :id")
    String findBookNameById(@Param("id") Long id);

    // Custom query to get only book names
    @Query("SELECT b.bookName FROM BookModel b")
    List<String> findAllBookNames();
}
