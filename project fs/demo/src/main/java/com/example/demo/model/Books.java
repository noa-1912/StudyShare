package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Books {
    @GeneratedValue
    @Id
    private Long id;
    private String bookName;
    private String author;
    private String description;
    @ManyToOne
    private Subjects subject;
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Suggestion> suggestion;
    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Solutions> solutions;
    private String grade;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    public List<Suggestion> getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(List<Suggestion> suggestion) {
        this.suggestion = suggestion;
    }

    public List<Solutions> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solutions> solutions) {
        this.solutions = solutions;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Books() {
    }

    public Books(Long id, String bookName, String author, String description, Subjects subject, List<Suggestion> suggestion, List<Solutions> solutions,String grade) {
        this.id = id;
        this.bookName = bookName;
        this.author = author;
        this.description = description;
        this.subject = subject;
        this.suggestion = suggestion;
        this.solutions = solutions;
        this.grade=grade;
    }
}
