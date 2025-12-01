package com.example.demo.dto;

import com.example.demo.model.Comments;
import com.example.demo.model.Solutions;
import com.example.demo.model.Suggestion;

import java.time.LocalDate;
import java.util.List;

public class UsersDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate date;
    private String imagePath;
    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

}
