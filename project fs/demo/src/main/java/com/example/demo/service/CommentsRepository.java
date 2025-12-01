package com.example.demo.service;

import com.example.demo.model.Books;
import com.example.demo.model.Comments;
import com.example.demo.model.Solutions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllBySolutionId(Long solutionId);

}
