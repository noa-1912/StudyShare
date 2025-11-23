package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Suggestion {
    @GeneratedValue
    @Id
    private Long id;
    private int page;
    private int exercise;
    private int section;
    private int subSection;
    private String content;
    private LocalDate uploadDate;
    private String imagePath;
    @JsonIgnoreProperties("suggestion")
    @ManyToOne

    private Users user;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Books book;

    public Books getBooks() {
        return book;
    }

    public void setBooks(Books books) {
        this.book = books;
    }

    public Suggestion() {
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public Suggestion(Long id, int page, int exercise, int section, int subSection, String content, LocalDate uploadDate, Users user, Books books, String imagePath) {
        this.id = id;
        this.page = page;
        this.exercise = exercise;
        this.section = section;
        this.subSection = subSection;
        this.content = content;
        this.uploadDate = uploadDate;
        this.user = user;
        this.book = books;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getSubSection() {
        return subSection;
    }

    public void setSubSection(int subSection) {
        this.subSection = subSection;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
