package com.javapractice.repository;

import com.javapractice.entity.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {

    List<KnowledgePoint> findByChapterIdOrderBySortOrderAsc(Long chapterId);
}
