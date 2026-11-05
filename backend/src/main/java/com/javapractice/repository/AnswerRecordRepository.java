package com.javapractice.repository;

import com.javapractice.entity.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {
}
