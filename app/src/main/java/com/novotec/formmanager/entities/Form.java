package com.novotec.formmanager.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Created by jvilam on 04/04/2016.
 *
 */
public class Form implements Serializable{

    private int idForm;
    private String name;
    private String description;
    private String author;
    private Date createDate;
    private boolean isTracked = false;
    private Vector<Question> questions;

    private boolean existingForm;

    public int getIdForm() {
        return idForm;
    }

    public void setIdForm(int idForm) {
        this.idForm = idForm;
    }

    public Form(){
        questions = new Vector<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void setIsTracked(boolean isTracked) {
        this.isTracked = isTracked;
    }

    public Vector<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Vector<Question> questions) {
        this.questions = questions;
    }

    public boolean addQuestion(Question q){
        return this.questions.add(q);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isExistingForm() {
        return existingForm;
    }

    public void setExistingForm(boolean existingForm) {
        this.existingForm = existingForm;
    }
}
