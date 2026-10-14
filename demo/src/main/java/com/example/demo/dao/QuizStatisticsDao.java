package com.example.demo.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QuizStatisticsDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Demonstrates native Spring JDBC query execution (JDBC integration requirement).
     * Calculates aggregate platform statistics directly through raw SQL queries.
     */
    public Map<String, Object> getAggregatedPlatformMetrics() {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM users) AS total_users,
                (SELECT COUNT(*) FROM quizzes WHERE status = 'APPROVED') AS published_quizzes,
                (SELECT COUNT(*) FROM results) AS total_attempts,
                (SELECT COALESCE(AVG(score * 1.0 / NULLIF(total_questions, 0)), 0) * 100 FROM results) AS avg_score_pct
            """;
        return jdbcTemplate.queryForMap(sql);
    }
}
