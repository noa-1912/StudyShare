package com.example.demo.controller;

import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.*;
import com.example.demo.service.ImageUtils;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.service.BooksRepository;
import com.example.demo.service.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RequestMapping("api/comments")
@RestController
@CrossOrigin
public class CommentsController {

    CommentsRepository commentsRepository;

    @Autowired//מאפשר הזרקת תלויות
    public CommentsController(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }


    //שליפת תגובות לפי מזהה  של פתרון ID
    @GetMapping("/getComments/{id}")
    public ResponseEntity<List<Comments>> getById(@PathVariable long id){

        List<Comments> comments = commentsRepository.findAllBySolutionId(id);

        if (comments.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }



    //העלאת תגובה חדשה
   // @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/uploadComment")
    public ResponseEntity<?> add(@RequestBody Comments c) {

        // בדיקה שהמשתמש קיים
        if (c.getUser() == null || c.getUser().getId() == null)
            return new ResponseEntity<>("User missing", HttpStatus.BAD_REQUEST);

        // בדיקה שהפתרון קיים
        if (c.getSolution() == null || c.getSolution().getId() == null)
            return new ResponseEntity<>("Solution missing", HttpStatus.BAD_REQUEST);

        try {
            Comments saved = commentsRepository.save(c);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error saving comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
