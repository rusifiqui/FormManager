package com.novotec.formmanager.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jvilam on 04/04/2016.
 *
 */
public class Answer implements Serializable {

    private int idAnser;
    private int idQuesionDesign;
    private String answer;
    private String author;
    private int answerType;
    private Date createDate;

    public Answer(){}

    public int getId() {
        return idAnser;
    }

    public void setIdAnser(int id) {
        this.idAnser = id;
    }

    public int getIdQuesionDesign() {
        return idQuesionDesign;
    }

    public void setIdQuesionDesign(int idQuesionDesign) {
        this.idQuesionDesign = idQuesionDesign;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
