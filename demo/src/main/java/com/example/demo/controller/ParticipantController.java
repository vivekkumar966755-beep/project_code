package com.example.demo.controller;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizStatus;
import com.example.demo.model.Result;
import com.example.demo.model.User;
import com.example.demo.service.QuizService;
import com.example.demo.service.ResultService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ParticipantController {

    private final QuizService quizService;
    private final ResultService resultService;
    private final UserService userService;

    public ParticipantController(QuizService quizService,
                                 ResultService resultService,
                                 UserService userService) {
        this.quizService = quizService;
        this.resultService = resultService;
        this.userService = userService;
    }

    @GetMapping("/quizzes")
    public String listAvailableQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.findApproved());
        return "participant/quiz-list";
    }

    @GetMapping("/quizzes/{id}/take")
    public String takeQuiz(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.requireWithQuestions(id);
        if (quiz.getStatus() != QuizStatus.APPROVED || quiz.getQuestions().isEmpty()) {
            return "redirect:/quizzes";
        }
        model.addAttribute("quiz", quiz);
        return "participant/take-quiz";
    }

    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam Map<String, String> allParams,
                             Authentication authentication,
                             Model model) {
        User participant = userService.requireByEmail(authentication.getName());
        Quiz quiz = quizService.requireWithQuestions(id);
        Result result = resultService.gradeAndSave(participant, quiz, allParams);
        model.addAttribute("quiz", quiz);
        model.addAttribute("result", result);
        model.addAttribute("score", result.getScore());
        model.addAttribute("totalQuestions", result.getTotalQuestions());
        return "participant/quiz-result";
    }

    @GetMapping("/participant/history")
    public String history(Authentication authentication, Model model) {
        User participant = userService.requireByEmail(authentication.getName());
        model.addAttribute("results", resultService.historyFor(participant));
        return "participant/history";
    }
}
