package com.javapractice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgePointVO {
    private Long id;
    private String title;
    private String content;
    private Double mastery;
    private Integer correctCount;
    private Integer wrongCount;
}
