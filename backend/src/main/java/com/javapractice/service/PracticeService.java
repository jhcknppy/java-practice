package com.javapractice.service;

import com.javapractice.dto.AnswerRequest;
import com.javapractice.dto.AnswerResult;
import com.javapractice.dto.KnowledgePointVO;
import com.javapractice.dto.OptionVO;
import com.javapractice.dto.QuestionVO;
import com.javapractice.dto.SettleResult;
import com.javapractice.dto.WrongQuestionVO;
import com.javapractice.entity.AnswerRecord;
import com.javapractice.entity.Chapter;
import com.javapractice.entity.ChapterProgress;
import com.javapractice.entity.KnowledgePoint;
import com.javapractice.entity.Question;
import com.javapractice.entity.QuestionOption;
import com.javapractice.entity.User;
import com.javapractice.entity.UserKnowledgeMastery;
import com.javapractice.entity.WrongQuestion;
import com.javapractice.exception.BusinessException;
import com.javapractice.repository.AnswerRecordRepository;
import com.javapractice.repository.ChapterProgressRepository;
import com.javapractice.repository.ChapterRepository;
import com.javapractice.repository.KnowledgePointRepository;
import com.javapractice.repository.QuestionOptionRepository;
import com.javapractice.repository.QuestionRepository;
import com.javapractice.repository.UserKnowledgeMasteryRepository;
import com.javapractice.repository.UserRepository;
import com.javapractice.repository.WrongQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PracticeService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final ChapterRepository chapterRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final UserKnowledgeMasteryRepository masteryRepository;
    private final ChapterProgressRepository progressRepository;
    private final UserRepository userRepository;

    public PracticeService(QuestionRepository questionRepository,
                           QuestionOptionRepository optionRepository,
                           KnowledgePointRepository knowledgePointRepository,
                           ChapterRepository chapterRepository,
                           AnswerRecordRepository answerRecordRepository,
                           WrongQuestionRepository wrongQuestionRepository,
                           UserKnowledgeMasteryRepository masteryRepository,
                           ChapterProgressRepository progressRepository,
                           UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.knowledgePointRepository = knowledgePointRepository;
        this.chapterRepository = chapterRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.masteryRepository = masteryRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AnswerResult submit(Long userId, AnswerRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new BusinessException(404, "题目不存在"));
        boolean correct = question.getAnswer().equalsIgnoreCase(request.getAnswer().trim());

        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(question.getId());
        record.setChapterId(question.getChapterId());
        record.setKnowledgePointId(question.getKnowledgePointId());
        record.setUserAnswer(request.getAnswer());
        record.setCorrect(correct);
        answerRecordRepository.save(record);

        updateProgress(userId, question.getChapterId(), correct);
        updateMastery(userId, question.getKnowledgePointId(), correct);
        updateWrongQuestion(userId, question, correct);

        String kpTitle = knowledgePointRepository.findById(question.getKnowledgePointId())
                .map(KnowledgePoint::getTitle)
                .orElse("");
        return new AnswerResult(correct, question.getAnswer(), question.getAnalysis(), kpTitle);
    }

    public List<WrongQuestionVO> wrongQuestions(Long userId) {
        return wrongQuestionRepository.findByUserIdAndResolvedFalseOrderByUpdatedAtDesc(userId).stream()
                .map(this::toWrongQuestionVO)
                .collect(Collectors.toList());
    }

    public List<KnowledgePointVO> chapterReview(Long userId, Long chapterId) {
        List<KnowledgePoint> kps = knowledgePointRepository.findByChapterIdOrderBySortOrderAsc(chapterId);
        List<KnowledgePointVO> result = new ArrayList<>();
        for (KnowledgePoint kp : kps) {
            KnowledgePointVO vo = new KnowledgePointVO();
            vo.setId(kp.getId());
            vo.setTitle(kp.getTitle());
            vo.setContent(kp.getContent());
            masteryRepository.findByUserIdAndKnowledgePointId(userId, kp.getId()).ifPresent(m -> {
                vo.setCorrectCount(m.getCorrectCount());
                vo.setWrongCount(m.getWrongCount());
                int total = m.getCorrectCount() + m.getWrongCount();
                vo.setMastery(total == 0 ? null : m.getCorrectCount() * 1.0 / total);
            });
            result.add(vo);
        }
        return result;
    }

    public List<QuestionVO> reinforcements(Long userId, Long chapterId) {
        Set<Long> weakKpIds = wrongQuestionRepository.findByUserIdAndChapterIdAndResolvedFalse(userId, chapterId)
                .stream()
                .map(WrongQuestion::getKnowledgePointId)
                .collect(Collectors.toSet());
        if (weakKpIds.isEmpty()) {
            return List.of();
        }
        List<Question> questions = questionRepository.findByKnowledgePointIdIn(new ArrayList<>(weakKpIds));
        Set<Long> seen = new HashSet<>();
        List<QuestionVO> result = new ArrayList<>();
        for (Question q : questions) {
            if (seen.add(q.getId())) {
                result.add(toQuestionVO(q));
            }
        }
        return result;
    }

    @Transactional
    public SettleResult settle(Long userId, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BusinessException(404, "章节不存在"));
        ChapterProgress progress = getOrCreateProgress(userId, chapterId);
        int total = (int) questionRepository.countByChapterId(chapterId);
        boolean canComplete = total > 0 && progress.getAnsweredCount() >= total
                && progress.getCorrectCount() * 1.0 / total >= chapter.getPassRate();
        if (canComplete && !progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progressRepository.save(progress);
        }
        long completedCount = progressRepository.findByUserId(userId).stream()
                .filter(ChapterProgress::isCompleted)
                .count();
        int newLevel = (int) (completedCount / 3);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        boolean leveledUp = newLevel > user.getLevel();
        if (leveledUp) {
            user.setLevel(newLevel);
            userRepository.save(user);
        }
        return new SettleResult(canComplete, leveledUp, user.getLevel(), progress.getCorrectCount(), total);
    }

    private void updateProgress(Long userId, Long chapterId, boolean correct) {
        ChapterProgress progress = getOrCreateProgress(userId, chapterId);
        progress.setAnsweredCount(progress.getAnsweredCount() + 1);
        if (correct) {
            progress.setCorrectCount(progress.getCorrectCount() + 1);
        }
        progress.setUpdatedAt(LocalDateTime.now());
        progressRepository.save(progress);
    }

    private ChapterProgress getOrCreateProgress(Long userId, Long chapterId) {
        return progressRepository.findByUserIdAndChapterId(userId, chapterId)
                .orElseGet(() -> {
                    ChapterProgress p = new ChapterProgress();
                    p.setUserId(userId);
                    p.setChapterId(chapterId);
                    return p;
                });
    }

    private void updateMastery(Long userId, Long knowledgePointId, boolean correct) {
        UserKnowledgeMastery mastery = masteryRepository.findByUserIdAndKnowledgePointId(userId, knowledgePointId)
                .orElseGet(() -> {
                    UserKnowledgeMastery m = new UserKnowledgeMastery();
                    m.setUserId(userId);
                    m.setKnowledgePointId(knowledgePointId);
                    return m;
                });
        if (correct) {
            mastery.setCorrectCount(mastery.getCorrectCount() + 1);
        } else {
            mastery.setWrongCount(mastery.getWrongCount() + 1);
        }
        masteryRepository.save(mastery);
    }

    private void updateWrongQuestion(Long userId, Question question, boolean correct) {
        WrongQuestion wrong = wrongQuestionRepository.findByUserIdAndQuestionId(userId, question.getId())
                .orElseGet(() -> {
                    WrongQuestion w = new WrongQuestion();
                    w.setUserId(userId);
                    w.setQuestionId(question.getId());
                    w.setChapterId(question.getChapterId());
                    w.setKnowledgePointId(question.getKnowledgePointId());
                    return w;
                });
        if (correct) {
            wrong.setResolved(true);
        } else {
            wrong.setResolved(false);
            wrong.setWrongCount(wrong.getWrongCount() + 1);
        }
        wrongQuestionRepository.save(wrong);
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

    private WrongQuestionVO toWrongQuestionVO(WrongQuestion wrong) {
        Question question = questionRepository.findById(wrong.getQuestionId())
                .orElseThrow(() -> new BusinessException(500, "题目数据异常"));
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setId(wrong.getId());
        vo.setQuestionId(question.getId());
        vo.setType(question.getType());
        vo.setTitle(question.getTitle());
        vo.setCorrectAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        vo.setWrongCount(wrong.getWrongCount());
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
