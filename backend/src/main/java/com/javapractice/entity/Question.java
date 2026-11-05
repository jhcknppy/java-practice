package com.javapractice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "practice_question")
public class Question {

    public static final String TYPE_CHOICE = "CHOICE";
    public static final String TYPE_TRUE_FALSE = "TRUE_FALSE";
    public static final String TYPE_CODE = "CODE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chapterId;

    @Column(nullable = false)
    private Long knowledgePointId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String analysis;

    @Column(nullable = false, length = 20)
    private String answer;

    @Column(nullable = false)
    private Integer difficulty = 1;
}
