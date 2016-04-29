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
    private int currentQuestion;

    private int idForm;
    private int id;

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

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getIdForm() {
        return idForm;
    }

    public void setIdForm(int idForm) {
        this.idForm = idForm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método que devuelve el tipo de pregunta de una pregunta concreta.
     * @param q El número de pregunta
     * @return Tipo de pregunta
     */
    public int getQuestionType(int q){
        return formStructure.getQuestions().get(q).getQuestionType();
    }

    /**
     * Método que devuelve el texto de la prgunta de una pregunta concreta
     * @param q El número de pregunta
     * @return Texto de la pregunta
     */
    public String getQuestionText(int q){
        return formStructure.getQuestions().get(q).getQuestion();
    }

    public String[] getAnswers(int q){
        Vector<Answer> answers = formStructure.getQuestions().get(q).getAnswers();
        String a[] = new String[answers.size()];
        for(int i = 0; i < answers.size(); i++){
            a[i] = new String();
            a[i] = answers.get(i).getAnswer();
        }
        return a;
    }

    public void addAnswer(AnsweredQuestion a){
        answeredQuestions.add(a);
    }

    /**
     * Método que devuelve el número de preguntas del formulario
     * @return Número de preguntas
     */
    public int getQuestionCount(){
        return formStructure.getQuestions().size();
    }


}