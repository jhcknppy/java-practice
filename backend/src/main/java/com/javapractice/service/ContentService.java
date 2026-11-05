package com.javapractice.service;

import com.javapractice.dto.ChapterVO;
import com.javapractice.dto.ModuleVO;
import com.javapractice.dto.OptionVO;
import com.javapractice.dto.QuestionVO;
import com.javapractice.entity.Chapter;
import com.javapractice.entity.KnowledgePoint;
import com.javapractice.entity.Module;
import com.javapractice.entity.Question;
import com.javapractice.entity.QuestionOption;
import com.javapractice.exception.BusinessException;
import com.javapractice.repository.ChapterProgressRepository;
import com.javapractice.repository.ChapterRepository;
import com.javapractice.repository.KnowledgePointRepository;
import com.javapractice.repository.ModuleRepository;
import com.javapractice.repository.QuestionOptionRepository;
import com.javapractice.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final ChapterProgressRepository progressRepository;

    public ContentService(ModuleRepository moduleRepository,
                          ChapterRepository chapterRepository,
                          KnowledgePointRepository knowledgePointRepository,
                          QuestionRepository questionRepository,
                          QuestionOptionRepository optionRepository,
                          ChapterProgressRepository progressRepository) {
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.knowledgePointRepository = knowledgePointRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.progressRepository = progressRepository;
    }

    public List<ModuleVO> modules(Long userId) {
        return moduleRepository.findAllByOrderBySortOrderAsc().stream()
                .map(module -> toModuleVO(module, userId))
                .collect(Collectors.toList());
    }

    public Chapter getChapter(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BusinessException(404, "章节不存在"));
    }

    public List<KnowledgePoint> knowledgePoints(Long chapterId) {
        return knowledgePointRepository.findByChapterIdOrderBySortOrderAsc(chapterId);
    }

    public List<QuestionVO> questions(Long chapterId) {
        return questionRepository.findByChapterIdOrderByIdAsc(chapterId).stream()
                .map(this::toQuestionVO)
                .collect(Collectors.toList());
    }

    public List<QuestionVO> questionsByIds(List<Long> ids) {
        return ids.stream()
                .map(id -> questionRepository.findById(id).orElse(null))
                .filter(q -> q != null)
                .map(this::toQuestionVO)
                .collect(Collectors.toList());
    }

    private ModuleVO toModuleVO(Module module, Long userId) {
        ModuleVO vo = new ModuleVO();
        vo.setId(module.getId());
        vo.setName(module.getName());
        vo.setDescription(module.getDescription());
        List<Chapter> chapters = chapterRepository.findByModuleIdOrderBySortOrderAsc(module.getId());
        boolean previousCompleted = true;
        java.util.List<ChapterVO> chapterVOs = new java.util.ArrayList<>();
        for (Chapter chapter : chapters) {
            ChapterVO cvo = new ChapterVO();
            cvo.setId(chapter.getId());
            cvo.setName(chapter.getName());
            cvo.setDescription(chapter.getDescription());
            cvo.setPassRate(chapter.getPassRate());
            cvo.setLocked(!previousCompleted);
            cvo.setTotal((int) questionRepository.countByChapterId(chapter.getId()));
            progressRepository.findByUserIdAndChapterId(userId, chapter.getId()).ifPresent(p -> {
                cvo.setCompleted(p.isCompleted());
                cvo.setAnswered(p.getAnsweredCount());
                cvo.setCorrect(p.getCorrectCount());
            });
            chapterVOs.add(cvo);
            previousCompleted = previousCompleted && cvo.isCompleted();
        }
        vo.setChapters(chapterVOs);
        return vo;
    }

    private QuestionVO toQuestionVO(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setType(question.getType());
        vo.setTitle(question.getTitle());
        vo.setDifficulty(question.getDifficulty());
        knowledgePointRepository.findById(question.getKnowledgePointId())
                .ifPresent(kp -> vo.setKnowledgePointTitle(kp.getTitle()));
        List<OptionVO> options = optionRepository.findByQuestionIdOrderBySortOrderAsc(question.getId())
                .stream()
                .map(o -> new OptionVO(o.getLabel(), o.getContent()))
                .collect(Collectors.toList());
        vo.setOptions(options);
        return vo;
    }
}
