package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Subjects {
@GeneratedValue
 @Id
 private Long id;
private String subjectName;
@OneToMany(mappedBy = "subject")
private List<Books> books;

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public String getSubjectName() {
  return subjectName;
 }

 public void setSubjectName(String subjectName) {
  this.subjectName = subjectName;
 }

 public List<Books> getBooks() {
  return books;
 }

 public void setBooks(List<Books> books) {
  this.books = books;
 }

 public Subjects() {
 }

 public Subjects(Long id, String subjectName, List<Books> books) {
  this.id = id;
  this.subjectName = subjectName;
  this.books = books;
 }
}
