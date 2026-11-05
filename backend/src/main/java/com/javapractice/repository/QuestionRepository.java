package com.javapractice.repository;

import com.javapractice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByChapterIdOrderByIdAsc(Long chapterId);

    List<Question> findByKnowledgePointIdIn(List<Long> knowledgePointIds);

    long countByChapterId(Long chapterId);

    boolean existsByChapterIdAndTitle(Long chapterId, String title);
}
