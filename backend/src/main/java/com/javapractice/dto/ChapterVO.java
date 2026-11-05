package com.javapractice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterVO {
    private Long id;
    private String name;
    private String description;
    private Double passRate;
    private boolean locked;
    private boolean completed;
    private int answered;
    private int total;
    private int correct;
}
