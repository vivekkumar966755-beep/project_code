package com.example.demo.repository;

import com.example.demo.model.Result;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("SELECT r FROM Result r JOIN FETCH r.quiz WHERE r.participant = :participant ORDER BY r.takenAt DESC")
    List<Result> findByParticipantWithQuiz(@Param("participant") User participant);

    long countByParticipant(User participant);
}
