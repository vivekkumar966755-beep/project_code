package com.example.demo.controller;

import com.example.demo.entity.Quiz;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.QuizService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ParticipantController {

    private final QuizService quizService;
    private final UserService userService;

    @GetMapping("/quizzes")
    public String listQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.getApprovedQuizzes());
        return "participant/quiz-list";
    }

    @GetMapping("/quizzes/{id}/take")
    public String takeQuiz(@PathVariable Long id, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        if (quiz.getStatus() != Quiz.Status.APPROVED) {
            return "redirect:/quizzes";
        }
        
        // Explicit Session Management (Servlets & Web Integration)
        session.setAttribute("ACTIVE_QUIZ_ID", id);
        session.setAttribute("QUIZ_START_TIME", System.currentTimeMillis());

        model.addAttribute("quiz", quiz);
        return "participant/take-quiz";
    }

    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam Map<String, String> params,
                             Principal principal,
                             HttpSession session,
                             HttpServletRequest request,
                             Model model) {
        Quiz quiz = quizService.getQuizById(id).orElseThrow();
        User participant = userService.findByEmail(principal.getName()).orElseThrow();

        // Invalidate active session exam tokens
        session.removeAttribute("ACTIVE_QUIZ_ID");
        session.removeAttribute("QUIZ_START_TIME");

        List<String> answers = new ArrayList<>();
        quiz.getQuestions().forEach(q -> {
            String answer = params.get("question_" + q.getId());
            answers.add(answer != null ? answer : "");
        });

        Result result = quizService.submitQuiz(quiz, participant, answers);
        model.addAttribute("quiz", quiz);
        model.addAttribute("score", result.getScore());
        model.addAttribute("totalQuestions", result.getTotalQuestions());
        return "participant/quiz-result";
    }

    @GetMapping("/participant/history")
    public String history(Model model, Principal principal) {
        User participant = userService.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("results", quizService.getResultsByParticipant(participant));
        return "participant/history";
    }
}
