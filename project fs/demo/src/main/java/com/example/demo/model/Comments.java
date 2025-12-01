package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Comments {
    @GeneratedValue
    @Id
    private int id;
    private String commentText;
    private LocalDate commentDate;
    private int ratingValue;

    @ManyToOne

    private Users user;

    @ManyToOne

    private Solutions solution;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDate getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDate commentDate) {
        this.commentDate = commentDate;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Solutions getSolution() {
        return solution;
    }

    public void setSolution(Solutions solution) {
        this.solution = solution;
    }

    public Comments() {
    }

    public Comments(int id, String commentText, LocalDate commentDate, int ratingValue, Users user, Solutions solution) {
        this.id = id;
        this.commentText = commentText;
        this.commentDate = commentDate;
        this.ratingValue = ratingValue;
        this.user = user;
        this.solution = solution;
    }
}
