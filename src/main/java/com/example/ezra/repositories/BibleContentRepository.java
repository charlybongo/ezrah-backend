package com.example.ezra.repositories;

import com.example.ezra.models.chapterModel.BibleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BibleContentRepository extends JpaRepository<BibleContent, Long> {

    @Query("SELECT bc FROM BibleContent bc WHERE bc.parentId IS NULL")
    List<BibleContent> findAllRootContent();

    List<BibleContent> findByParentId(Long parentId);

    @Query("SELECT bc FROM BibleContent bc WHERE (bc.parentId IS NULL OR bc.parentId = :parentId) AND bc.language = :language")
    List<BibleContent> findByParentIdAndLanguage(@Param("parentId") Long parentId, @Param("language") String language);

    Optional<BibleContent> findByIdAndLanguage(Long id, String language);

    List<BibleContent> findByLanguage(String language);

    // 🔹 New Query: Find first BibleContent by chapterGroup, language, and type
    @Query("SELECT bc FROM BibleContent bc WHERE bc.chapterGroup = :chapterGroup AND bc.language = :language AND bc.type = :type ORDER BY bc.id ASC")
    Optional<BibleContent> findFirstByChapterGroupAndLanguageAndType(@Param("chapterGroup") Long chapterGroup,
                                                                     @Param("language") String language,
                                                                     @Param("type") String type);


    // 🔹 Find all BibleContent by chapterGroup
    List<BibleContent> findByChapterGroup(Long chapterGroup);

    // 🔹 New Query: Find all unsubscribed BibleContent by language
    @Query("SELECT bc FROM BibleContent bc WHERE bc.language = :language AND bc.id NOT IN :subscribedChapterIds")
    List<BibleContent> findUnsubscribedContentByLanguage(@Param("language") String language, @Param("subscribedChapterIds") List<Long> subscribedChapterIds);

    // ✅ Find all content by language AND type
    List<BibleContent> findByLanguageAndType(String language, String type);
    // Search by content text (case insensitive)
    @Query("SELECT b FROM BibleContent b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BibleContent> searchByContent(@Param("keyword") String keyword);
    @Query("SELECT b FROM BibleContent b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.language = :language")
    List<BibleContent> searchByContentAndLanguage(@Param("keyword") String keyword, @Param("language") String language);
}
