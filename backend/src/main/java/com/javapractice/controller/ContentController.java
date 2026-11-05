package com.javapractice.controller;

import com.javapractice.dto.ApiResponse;
import com.javapractice.dto.ModuleVO;
import com.javapractice.dto.QuestionVO;
import com.javapractice.entity.Chapter;
import com.javapractice.entity.KnowledgePoint;
import com.javapractice.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/modules")
    public ApiResponse<List<ModuleVO>> modules(@RequestAttribute("userId") Long userId) {
        return ApiResponse.ok(contentService.modules(userId));
    }

    @GetMapping("/chapters/{id}")
    public ApiResponse<Chapter> chapter(@PathVariable Long id) {
        return ApiResponse.ok(contentService.getChapter(id));
    }

    @GetMapping("/chapters/{id}/knowledge-points")
    public ApiResponse<List<KnowledgePoint>> knowledgePoints(@PathVariable Long id) {
        return ApiResponse.ok(contentService.knowledgePoints(id));
    }

    @GetMapping("/chapters/{id}/questions")
    public ApiResponse<List<QuestionVO>> questions(@PathVariable Long id) {
        return ApiResponse.ok(contentService.questions(id));
    }
}
