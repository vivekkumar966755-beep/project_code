package com.example.demo.repository;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizStatus;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByStatusOrderByCreatedAtDesc(QuizStatus status);

    List<Quiz> findByCreatorOrderByCreatedAtDesc(User creator);

    List<Quiz> findAllByOrderByCreatedAtDesc();

    long countByStatus(QuizStatus status);

    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findWithQuestionsById(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.creator WHERE q.status = :status ORDER BY q.createdAt DESC")
    List<Quiz> findApprovedWithCreator(@Param("status") QuizStatus status);
}
