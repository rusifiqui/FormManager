package com.novotec.formmanager.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Created by jvilam on 04/04/2016.
 *
 */
public class Question implements Serializable {

    private int idQuestion;
    private int idFormDesign;
    private String question;
    private String author;
    private int questionType;
    private Date createDate;
    private boolean mandatoryAnswer;

    private Vector<Answer> answers;

    public Question(){
        answers = new Vector<>();
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdFormDesign() {
        return idFormDesign;
    }

    public void setIdFormDesign(int idFormDesign) {
        this.idFormDesign = idFormDesign;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Vector<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Vector<Answer> answers) {
        this.answers = answers;
    }

    public boolean isMandatoryAnswer() {
        return mandatoryAnswer;
    }

    public void setMandatoryAnswer(boolean mandatoryAnswer) {
        this.mandatoryAnswer = mandatoryAnswer;
    }

    public Answer getAnswer(int i){
        return answers.get(i);
    }

    public void addAnswer(Answer a){
        answers.add(a);
    }
}