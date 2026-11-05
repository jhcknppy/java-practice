package com.javapractice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuleVO {
    private Long id;
    private String name;
    private String description;
    private List<ChapterVO> chapters;
}
