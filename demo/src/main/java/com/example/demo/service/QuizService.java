package com.example.demo.service;

import com.example.demo.model.Question;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizStatus;
import com.example.demo.model.User;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public List<Quiz> findAll() {
        return quizRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Quiz> findApproved() {
        return quizRepository.findApprovedWithCreator(QuizStatus.APPROVED);
    }

    public List<Quiz> findByCreator(User creator) {
        return quizRepository.findByCreatorOrderByCreatedAtDesc(creator);
    }

    public Quiz requireById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
    }

    public Quiz requireWithQuestions(Long id) {
        return quizRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
    }

    @Transactional
    public Quiz createQuiz(String title, int duration, User creator) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDuration(duration);
        quiz.setStatus(QuizStatus.PENDING);
        quiz.setCreator(creator);
        quiz.setCreatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId, Question question) {
        Quiz quiz = requireById(quizId);
        question.setQuiz(quiz);
        question.setCorrectOption(question.getCorrectOption() == null
                ? null
                : question.getCorrectOption().trim().toUpperCase());
        return questionRepository.save(question);
    }

    public List<Question> questionsForQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Transactional
    public void updateStatus(Long quizId, QuizStatus status) {
        Quiz quiz = requireById(quizId);
        quiz.setStatus(status);
        quizRepository.save(quiz);
    }

    public long countAll() {
        return quizRepository.count();
    }

    public long countByStatus(QuizStatus status) {
        return quizRepository.countByStatus(status);
    }
}
