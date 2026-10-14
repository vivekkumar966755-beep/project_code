package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class QuizAnalyticsService {

    /**
     * Executes asynchronous background telemetry and scoring calculation
     * utilizing the configured ThreadPoolTaskExecutor (demonstrating multi-threading).
     */
    @Async("quizTaskExecutor")
    public CompletableFuture<Void> recordAttemptAnalytics(Long quizId, Long participantId, int score, int total) {
        return CompletableFuture.runAsync(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                log.info("[Thread: {}] Calculating performance metrics for Quiz ID: {}, Participant: {}, Score: {}/{}",
                        threadName, quizId, participantId, score, total);
                // Simulate metric aggregation in background thread
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Analytics computation thread was interrupted", e);
            }
        });
    }
}
