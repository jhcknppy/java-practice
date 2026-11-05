package com.javapractice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WrongQuestionVO {
    private Long id;
    private Long questionId;
    private String type;
    private String title;
    private String correctAnswer;
    private String analysis;
    private String knowledgePointTitle;
    private Integer wrongCount;
    private List<OptionVO> options;
}
