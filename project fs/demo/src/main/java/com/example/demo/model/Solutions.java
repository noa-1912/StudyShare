package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Solutions extends Suggestion{
    private double avg;

    public Solutions() {
    }

    public Solutions(Long id, int page, int exercise, int section, int subSection, String content, LocalDate uploadDate, Users user, Books books, double avg, List<Comments>comments, String imagePath) {
        super(id, page, exercise, section, subSection, content, uploadDate, user,books, imagePath );
        this.avg = avg;
        this.comments=comments;
    }
    public Solutions(double avg) {
        this.avg = avg;
    }


    @OneToMany(mappedBy = "solution")
    @JsonIgnore
    private  List<Comments> comments;

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public double getAvg() {
        return avg;
    }
    public void setAvg(double avg) {
        this.avg = avg;
    }



}
