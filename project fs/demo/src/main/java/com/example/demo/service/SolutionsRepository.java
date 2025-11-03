package com.example.demo.service;

import com.example.demo.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  SolutionsRepository extends JpaRepository<Suggestion, Long> {
}
