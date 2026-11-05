package com.javapractice.repository;

import com.javapractice.entity.UserKnowledgeMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserKnowledgeMasteryRepository extends JpaRepository<UserKnowledgeMastery, Long> {

    Optional<UserKnowledgeMastery> findByUserIdAndKnowledgePointId(Long userId, Long knowledgePointId);
}
