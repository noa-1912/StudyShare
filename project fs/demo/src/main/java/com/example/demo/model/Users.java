package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Users {
    @GeneratedValue
    @Id
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate date;
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Solutions> solutions;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Suggestion> suggestion;
    @OneToMany(mappedBy = "user")
    private List<Comments> comments;
    private String imagePath;
    @ManyToMany
    @JsonIgnore
    private Set<Role> roles=new HashSet<>();

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Solutions> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solutions> solutions) {
        this.solutions = solutions;
    }

    public List<Suggestion> getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(List<Suggestion> suggestion) {
        this.suggestion = suggestion;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public Users() {
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Users(Long id, String name, String email, String password, LocalDate date, List<Solutions> solutions, List<Suggestion> suggestions, List<Comments> comments, String imagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.date = date;
        this.solutions = solutions;
        this.suggestion = suggestion;
        this.comments = comments;
        this.imagePath = imagePath;
    }
}
