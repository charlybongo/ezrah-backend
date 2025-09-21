package com.example.ezra.repositories;

import com.example.ezra.models.chapterModel.BibleContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BibleContentRepository extends JpaRepository<BibleContent, Long> {

    @Query("SELECT bc FROM BibleContent bc WHERE bc.parentId IS NULL")
    Page<BibleContent> findAllRootContent(Pageable pageable);

    Page<BibleContent> findByParentId(Long parentId, Pageable pageable);
    List<BibleContent> findByParentId(Long parentId);


    @Query("SELECT bc FROM BibleContent bc WHERE (bc.parentId IS NULL OR bc.parentId = :parentId) AND bc.language = :language")
    Page<BibleContent> findByParentIdAndLanguage(@Param("parentId") Long parentId, @Param("language") String language, Pageable pageable);

    Optional<BibleContent> findByIdAndLanguage(Long id, String language);

    Page<BibleContent> findByLanguage(String language, Pageable pageable);

    @Query("SELECT bc FROM BibleContent bc WHERE bc.id = :id AND bc.language = :language  AND bc.type = :type ORDER BY bc.id ASC")
    Optional<BibleContent> findFirstByChapterGroupAndLanguageAndType(@Param("id") Long id,
                                                                     @Param("language") String language,
                                                                     @Param("type") String type);

    Page<BibleContent> findByChapterGroup(Long chapterGroup, Pageable pageable);
    List<BibleContent> findByChapterGroupAndLanguage(Long chapterGroup, String language);

    @Query("SELECT bc FROM BibleContent bc WHERE bc.language = :language AND bc.id NOT IN :subscribedChapterIds")
    Page<BibleContent> findUnsubscribedContentByLanguage(@Param("language") String language, @Param("subscribedChapterIds") List<Long> subscribedChapterIds, Pageable pageable);

    

    @Query("SELECT b FROM BibleContent b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BibleContent> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM BibleContent b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.language = :language")
    Page<BibleContent> searchByContentAndLanguage(@Param("keyword") String keyword, @Param("language") String language, Pageable pageable);
}
