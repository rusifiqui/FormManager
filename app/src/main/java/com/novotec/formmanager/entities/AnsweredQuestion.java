package com.novotec.formmanager.entities;

import java.io.Serializable;

/**
 * Created by jvilam on 15/04/2016.
 *
 */
public class AnsweredQuestion implements Serializable {

    private int id;
    private int idUserQuestion; // Id de la Pregunta del formulario
    private int userAnswerId;   // Id de la respuesta seleccionada del formulario. Solo para respuesta única y múltiple
    private String answer;      // Texto de la respuesta
    private String lat;         // Latitud
    private String lon;         // Longitud
    private String address;     // Dirección

    public AnsweredQuestion(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}

