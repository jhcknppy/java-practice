package com.javapractice.repository;

import com.javapractice.entity.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {

    Optional<ChapterProgress> findByUserIdAndChapterId(Long userId, Long chapterId);

    List<ChapterProgress> findByUserId(Long userId);
}
