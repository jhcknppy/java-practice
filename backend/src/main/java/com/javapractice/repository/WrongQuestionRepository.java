package com.javapractice.repository;

import com.javapractice.entity.WrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WrongQuestionRepository extends JpaRepository<WrongQuestion, Long> {

    Optional<WrongQuestion> findByUserIdAndQuestionId(Long userId, Long questionId);

    List<WrongQuestion> findByUserIdAndResolvedFalseOrderByUpdatedAtDesc(Long userId);

    List<WrongQuestion> findByUserIdAndChapterIdAndResolvedFalse(Long userId, Long chapterId);
}
