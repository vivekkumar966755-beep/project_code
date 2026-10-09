package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizStatus;
import com.example.demo.model.User;
import com.example.demo.service.QuizService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/creator")
public class CreatorController {

    private final QuizService quizService;
    private final UserService userService;

    public CreatorController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User creator = userService.requireByEmail(authentication.getName());
        List<Quiz> quizzes = quizService.findByCreator(creator);
        long pending = quizzes.stream().filter(q -> q.getStatus() == QuizStatus.PENDING).count();
        long approved = quizzes.stream().filter(q -> q.getStatus() == QuizStatus.APPROVED).count();
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("quizCount", quizzes.size());
        model.addAttribute("pendingCount", pending);
        model.addAttribute("approvedCount", approved);
        return "creator/dashboard";
    }

    @GetMapping("/quizzes")
    public String myQuizzes(Authentication authentication, Model model) {
        User creator = userService.requireByEmail(authentication.getName());
        model.addAttribute("quizzes", quizService.findByCreator(creator));
        return "creator/my-quizzes";
    }

    @GetMapping("/quizzes/create")
    public String showCreateQuizForm() {
        return "creator/create-quiz";
    }

    @PostMapping("/quizzes/create")
    public String createQuiz(@RequestParam String title,
                             @RequestParam int duration,
                             Authentication authentication) {
        User creator = userService.requireByEmail(authentication.getName());
        Quiz savedQuiz = quizService.createQuiz(title, duration, creator);
        return "redirect:/creator/quizzes/" + savedQuiz.getId() + "/add-questions";
    }

    @GetMapping("/quizzes/{quizId}/add-questions")
    public String showAddQuestionForm(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.requireById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.questionsForQuiz(quizId));
        return "creator/add-question";
    }

    @PostMapping("/quizzes/{quizId}/add-questions")
    public String addQuestionToQuiz(@PathVariable Long quizId,
                                    @RequestParam String questionText,
                                    @RequestParam String optionA,
                                    @RequestParam String optionB,
                                    @RequestParam String optionC,
                                    @RequestParam String optionD,
                                    @RequestParam String correctoption,
                                    RedirectAttributes redirectAttributes) {
        Question question = new Question();
        question.setQuestionText(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOption(correctoption);
        quizService.addQuestion(quizId, question);
        redirectAttributes.addFlashAttribute("success", "Question added.");
        return "redirect:/creator/quizzes/" + quizId + "/add-questions";
    }
}
