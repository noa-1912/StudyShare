package com.example.demo.service;

import com.example.demo.model.Solutions;
import com.example.demo.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface  SolutionsRepository extends JpaRepository<Solutions, Long> {
    List<Solutions> findSolutionsByBook_IdAndPageAndExercise(Long bookId, int page, int exercise);
}
