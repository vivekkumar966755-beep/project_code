package com.example.demo.config;

import com.example.demo.model.Question;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizStatus;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    public DataInitializer(UserRepository userRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = ensureUser(adminName, adminEmail, adminPassword, Role.ADMIN);
        User creator = ensureUser("Ava Creator", "creator@quizplatform.local", "Creator@123", Role.CREATOR);
        ensureUser("Noah Participant", "participant@quizplatform.local", "Participant@123", Role.PARTICIPANT);

        if (quizRepository.count() == 0) {
            Quiz quiz = new Quiz();
            quiz.setTitle("Spring Boot Fundamentals");
            quiz.setDuration(10);
            quiz.setStatus(QuizStatus.APPROVED);
            quiz.setCreator(creator != null ? creator : admin);
            quiz.setCreatedAt(LocalDateTime.now());
            Quiz saved = quizRepository.save(quiz);

            addQuestion(saved, "Which annotation starts a Spring Boot application?",
                    "@SpringBootApplication", "@EnableAutoConfig", "@RestController", "@Entity", "A");
            addQuestion(saved, "Which starter provides Spring Security?",
                    "spring-boot-starter-web", "spring-boot-starter-security", "spring-boot-starter-data-jpa", "spring-boot-starter-thymeleaf", "B");
            addQuestion(saved, "What does JPA stand for?",
                    "Java Persistence API", "Java Platform Adapter", "JSON Processing API", "Jakarta Page Assembly", "A");
        }
    }

    private User ensureUser(String name, String email, String rawPassword, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    private void addQuestion(Quiz quiz, String text, String a, String b, String c, String d, String correct) {
        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(text);
        question.setOptionA(a);
        question.setOptionB(b);
        question.setOptionC(c);
        question.setOptionD(d);
        question.setCorrectOption(correct);
        questionRepository.save(question);
    }
}
