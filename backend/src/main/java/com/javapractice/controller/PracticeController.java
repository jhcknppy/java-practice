package com.javapractice.controller;

import com.javapractice.dto.ApiResponse;
import com.javapractice.dto.AnswerRequest;
import com.javapractice.dto.AnswerResult;
import com.javapractice.dto.KnowledgePointVO;
import com.javapractice.dto.QuestionVO;
import com.javapractice.dto.SettleResult;
import com.javapractice.dto.WrongQuestionVO;
import com.javapractice.service.PracticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PracticeController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/answers")
    public ApiResponse<AnswerResult> submit(@RequestAttribute("userId") Long userId,
                                            @RequestBody AnswerRequest request) {
        return ApiResponse.ok(practiceService.submit(userId, request));
    }

    @GetMapping("/wrong-questions")
    public ApiResponse<List<WrongQuestionVO>> wrongQuestions(@RequestAttribute("userId") Long userId) {
        return ApiResponse.ok(practiceService.wrongQuestions(userId));
    }

    @GetMapping("/chapters/{id}/review")
    public ApiResponse<List<KnowledgePointVO>> review(@PathVariable Long id,
                                                      @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok(practiceService.chapterReview(userId, id));
    }

    @GetMapping("/chapters/{id}/reinforcements")
    public ApiResponse<List<QuestionVO>> reinforcements(@PathVariable Long id,
                                                        @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok(practiceService.reinforcements(userId, id));
    }

    @PostMapping("/chapters/{id}/settle")
    public ApiResponse<SettleResult> settle(@PathVariable Long id,
                                            @RequestAttribute("userId") Long userId) {
        return ApiResponse.ok(practiceService.settle(userId, id));
    }
}
