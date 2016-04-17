package com.novotec.formmanager.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Created by jvilam on 15/04/2016.
 *
 */
public class AnsweredForm implements Serializable{

    private Form formStructure;

    private Vector<AnsweredQuestion> answeredQuestions;

    private Date createDate;
    private String userName;
    private String description;

    public AnsweredForm(){
        answeredQuestions = new Vector<>();
    }

    public Form getFormStructure() {
        return formStructure;
    }

    public void setFormStructure(Form formStructure) {
        this.formStructure = formStructure;
    }

    public Vector<AnsweredQuestion> getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(Vector<AnsweredQuestion> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}