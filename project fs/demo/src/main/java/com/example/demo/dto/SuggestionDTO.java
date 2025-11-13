package com.example.demo.dto;


import com.example.demo.model.Users;

import java.time.LocalDate;

public class SuggestionDTO {

    private Long id;
    private int page;
    private int exercise;
    private int section;
    private int subSection;
    private String content;
    private LocalDate uploadDate;
    private String imagePath;
    private String image;

    private UsersDTO user;
//    private Books book;


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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UsersDTO getUserDTO() {
        return user;
    }

    public void setUserDTO(UsersDTO user) {
        this.user = user;
    }
//public UsersDTO getUser() {
//    return user;
//}
//
//    public void setUser(UsersDTO user) {
//        this.user = user;
//    }
}
