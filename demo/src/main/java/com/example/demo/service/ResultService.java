package com.example.demo.service;

import com.example.demo.model.Question;
import com.example.demo.model.Quiz;
import com.example.demo.model.Result;
import com.example.demo.model.User;
import com.example.demo.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Transactional
    public Result gradeAndSave(User participant, Quiz quiz, Map<String, String> answers) {
        int score = 0;
        List<Question> questions = quiz.getQuestions();
        for (Question question : questions) {
            String selected = answers.get("question_" + question.getId());
            if (selected != null && selected.equalsIgnoreCase(question.getCorrectOption())) {
                score++;
            }
        }

        Result result = new Result();
        result.setParticipant(participant);
        result.setQuiz(quiz);
        result.setScore(score);
        result.setTotalQuestions(questions.size());
        result.setTakenAt(LocalDateTime.now());
        return resultRepository.save(result);
    }

    public List<Result> historyFor(User participant) {
        return resultRepository.findByParticipantWithQuiz(participant);
    }

    public long countFor(User participant) {
        return resultRepository.countByParticipant(participant);
    }

    public long countAll() {
        return resultRepository.count();
    }
}
