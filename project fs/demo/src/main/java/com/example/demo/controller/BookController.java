package com.example.demo.controller;

import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.Books;
import com.example.demo.model.Suggestion;
import com.example.demo.service.BooksRepository;
import com.example.demo.service.SolutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("api/book")
@RestController
@CrossOrigin
public class BookController {
    BooksRepository booksRepository;

    @Autowired//מאפשר הזרקת תלויות
    public BookController(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    //מחזיר את כל הספרים
    @GetMapping("/getBooks")
    public ResponseEntity<List<Books>> getBooks() {
        try {
            return new ResponseEntity<>(booksRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            //כאן השרת לא יקרוס
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
