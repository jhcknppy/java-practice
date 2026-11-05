package com.javapractice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionVO {
    private Long id;
    private String type;
    private String title;
    private Integer difficulty;
    private String knowledgePointTitle;
    private List<OptionVO> options;
}
