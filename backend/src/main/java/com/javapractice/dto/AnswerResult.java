package com.javapractice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResult {
    private boolean correct;
    private String correctAnswer;
    private String analysis;
    private String knowledgePointTitle;
}
