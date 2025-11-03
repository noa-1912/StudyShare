package com.example.demo.service;

import com.example.demo.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  SuggestionRepository extends JpaRepository<Suggestion, Long> {
}

