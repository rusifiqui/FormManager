package com.novotec.formmanager.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

/**
 * Created by jvilam on 15/04/2016.
 *
 */
public class AnsweredQuestion implements Serializable {

    private int id;
    private int idUserForm;     // Id del formulario de usuario
    private int idUserQuestion; // Id de la Pregunta del formulario
    private int userAnswerId;   // Id de la respuesta seleccionada del formulario. Solo para respuesta única y múltiple
    private String answer;      // Texto de la respuesta
    private String lat;         // Latitud
    private String lon;         // Longitud
    private String address;     // Dirección
    private Date createDate;
    private String author;

    private Vector<Integer> answersIds;
    private Vector<String> answers;

    public AnsweredQuestion(){
        answers = new Vector<>();
        answersIds = new Vector<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUserForm() {
        return idUserForm;
    }

    public void setIdUserForm(int idUserForm) {
        this.idUserForm = idUserForm;
    }

    public int getIdUserQuestion() {
        return idUserQuestion;
    }

    public void setIdUserQuestion(int idUserQuestion) {
        this.idUserQuestion = idUserQuestion;
    }

    public int getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(int userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Vector<String> getAnswers() {
        return answers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAnswers(Vector<String> answers) {
        this.answers = answers;
    }

    public Vector<Integer> getAnswersIds() {
        return answersIds;
    }

    public void setAnswersIds(Vector<Integer> answersIds) {
        this.answersIds = answersIds;
    }

    public void addMultipleAnswer(String a){
        answers.add(a);
    }

    public void addMultipleAnswerId(int aI){
        answersIds.add(aI);
    }
}

