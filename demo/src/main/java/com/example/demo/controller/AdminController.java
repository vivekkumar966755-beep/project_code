package com.example.demo.controller;

import com.example.demo.model.QuizStatus;
import com.example.demo.model.Role;
import com.example.demo.repository.ResultRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final QuizService quizService;
    private final ResultRepository resultRepository;

    public AdminController(UserRepository userRepository,
                           QuizService quizService,
                           ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.quizService = quizService;
        this.resultRepository = resultRepository;
    }

    @GetMapping({"/dashboard", ""})
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("adminCount", userRepository.countByRole(Role.ADMIN));
        model.addAttribute("creatorCount", userRepository.countByRole(Role.CREATOR));
        model.addAttribute("participantCount", userRepository.countByRole(Role.PARTICIPANT));
        model.addAttribute("quizCount", quizService.countAll());
        model.addAttribute("pendingCount", quizService.countByStatus(QuizStatus.PENDING));
        model.addAttribute("approvedCount", quizService.countByStatus(QuizStatus.APPROVED));
        model.addAttribute("resultCount", resultRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
                redirectAttributes.addFlashAttribute("error", "The last admin account cannot be deleted.");
                return;
            }
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User removed.");
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/quizzes")
    public String viewQuizzes(Model model) {
        model.addAttribute("quizzes", quizService.findAll());
        return "admin/quizzes";
    }

    @PostMapping("/quizzes/approve/{id}")
    public String approveQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.updateStatus(id, QuizStatus.APPROVED);
        redirectAttributes.addFlashAttribute("success", "Quiz approved and published.");
        return "redirect:/admin/quizzes";
    }

    @PostMapping("/quizzes/reject/{id}")
    public String rejectQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.updateStatus(id, QuizStatus.REJECTED);
        redirectAttributes.addFlashAttribute("success", "Quiz rejected.");
        return "redirect:/admin/quizzes";
    }
}
