package com.example.demo.service;

import com.example.demo.entity.Question;
import com.example.demo.entity.Quiz;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private QuizAnalyticsService quizAnalyticsService;

    @InjectMocks
    private QuizService quizService;

    private User sampleCreator;
    private User sampleParticipant;
    private Quiz sampleQuiz;

    @BeforeEach
    void setUp() {
        sampleCreator = User.builder().id(1L).name("Creator").email("creator@test.com").build();
        sampleParticipant = User.builder().id(2L).name("Participant").email("user@test.com").build();
        
        Question q1 = Question.builder().id(1L).questionText("Q1").correctoption("A").build();
        Question q2 = Question.builder().id(2L).questionText("Q2").correctoption("B").build();

        sampleQuiz = Quiz.builder()
                .id(10L)
                .title("Java Core Assessment")
                .duration(15)
                .status(Quiz.Status.PENDING)
                .creator(sampleCreator)
                .questions(List.of(q1, q2))
                .build();
    }

    @Test
    @DisplayName("Should create quiz in PENDING status")
    void testCreateQuiz() {
        when(quizRepository.save(any(Quiz.class))).thenReturn(sampleQuiz);

        Quiz created = quizService.createQuiz(sampleQuiz, sampleCreator);

        assertNotNull(created);
        assertEquals(Quiz.Status.PENDING, created.getStatus());
        assertEquals(sampleCreator, created.getCreator());
        verify(quizRepository, times(1)).save(sampleQuiz);
    }

    @Test
    @DisplayName("Should approve quiz successfully")
    void testApproveQuiz() {
        when(quizRepository.findById(10L)).thenReturn(Optional.of(sampleQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(sampleQuiz);

        quizService.approveQuiz(10L);

        assertEquals(Quiz.Status.APPROVED, sampleQuiz.getStatus());
        verify(quizRepository, times(1)).save(sampleQuiz);
    }

    @Test
    @DisplayName("Should accurately calculate participant score on submission")
    void testSubmitQuizScoring() {
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Answer 1 is correct ('A'), Answer 2 is incorrect ('C')
        List<String> participantAnswers = List.of("A", "C");

        Result result = quizService.submitQuiz(sampleQuiz, sampleParticipant, participantAnswers);

        assertNotNull(result);
        assertEquals(1, result.getScore(), "Score must be 1 for 1 correct out of 2");
        assertEquals(2, result.getTotalQuestions());
        verify(resultRepository, times(1)).save(any(Result.class));
        verify(quizAnalyticsService, times(1)).recordAttemptAnalytics(eq(10L), eq(2L), eq(1), eq(2));
    }
}
