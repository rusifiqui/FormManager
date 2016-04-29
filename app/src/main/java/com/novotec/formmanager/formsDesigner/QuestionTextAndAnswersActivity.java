package com.novotec.formmanager.formsDesigner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Answer;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.entities.Question;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Calendar;

/**
 * Actividad que sirve para generar un formulario con una pregunta y varias respuestas, independientemente se
 * si se trata de un formulario de respuesta única o múltiple.
 * @author jvilam
 * @version 1
 * @since 12/04/2016
 */
public class QuestionTextAndAnswersActivity extends AppCompatActivity {

    Form form;
    int questionType;

    EditText question;
    EditText newAnswer;
    ListView answers;
    ImageButton addAnswer;
    CheckBox mandatoryAnswer;

    String[] answersAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_text_and_answers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = (EditText) findViewById(R.id.editTextQuestionAndAnswerQuestion);
        newAnswer = (EditText) findViewById(R.id.editTextNewAnswer);
        answers = (ListView) findViewById(R.id.listViewPossibleAnswers);
        addAnswer = (ImageButton) findViewById(R.id.buttonAddAnswer);
        mandatoryAnswer = (CheckBox) findViewById(R.id.checkBoxMandatoryQuestionAnswers);

        setData();
        String[] a = {""};
        setAdapter(a);

        // Se respuesta a la lista.
        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newAnswer.getText().length() > 0) {
                    if (answersAux == null) {
                        answersAux = new String[1];
                        answersAux[0] = newAnswer.getText().toString();
                        setAdapter(answersAux);
                        newAnswer.setText("");
                    }else{
                        String[] a = new String[answersAux.length + 1];

                        System.arraycopy(answersAux, 0, a, 0, answersAux.length);

                        a[a.length-1] = newAnswer.getText().toString();
                        answersAux = a;
                        setAdapter(a);
                        newAnswer.setText("");
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Botón continuar. Se almacenan los datos en la estructura de datos y se llama a la actividad para introducir una
        // nueva pregunta.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContinueQuestionAndAnswer);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Si no hay al menos dos preguntas, no se permite guardar, ya que los formularios de respuesta única
                    // y respuesta múltiple deben permitir seleccionar varias respuestas.
                    if(answers.getAdapter() != null && answers.getAdapter().getCount() > 1) {
                        setForm();
                        Intent intent = new Intent(getApplicationContext(), SelectQuestionTypeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("form", form);
                        intent.putExtra("mode", SelectQuestionTypeActivity.MODE_ADD_QUESTION);
                        startActivity(intent);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.no_answers, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
        FloatingActionButton fabFinish = (FloatingActionButton) findViewById(R.id.fabFinishQuestionAndAnswer);
        if (fabFinish != null) {
            fabFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(answers.getAdapter() == null && answers.getAdapter().getCount() < 2) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        setForm();
                        Toast toast;
                        if(DbHelper.saveFormDesign(form, getApplicationContext())){
                            toast = Toast.makeText(getApplicationContext(), R.string.form_saved, Toast.LENGTH_SHORT);
                            toast.show();
                        }else{
                            if(form.isExistingForm()){
                                toast = Toast.makeText(getApplicationContext(), R.string.existing_form, Toast.LENGTH_SHORT);
                            }else{
                                toast = Toast.makeText(getApplicationContext(), R.string.form_not_saved, Toast.LENGTH_SHORT);
                            }
                            toast.show();
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /**
     * Método que añade las preguntas a la lista de respuestas.
     * @param values Las respuestas
     */
    private void setAdapter(String[] values){
        // ArrayAdapter para la lista de elementos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        answers.setAdapter(adapter);
        answers.setSelection(answers.getCount() - 1);
    }

    /**
     * Recupera los parámetros pasadaos a la actividad.
     */
    private void setData() {
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            if (parameters.containsKey("form")) {
                form = (Form) parameters.get("form");
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if (parameters.containsKey("question_type")) {
                questionType = parameters.getInt("question_type");
            } else {
                throw new RuntimeException(getResources().getString(R.string.question_type_error));
            }
        }
    }

    private void setForm(){
        // Se almacena la pregunta.
        Question q = new Question();
        q.setAuthor(form.getAuthor());
        q.setCreateDate(Calendar.getInstance().getTime());
        q.setQuestion(question.getText().toString());
        q.setQuestionType(questionType);
        q.setMandatoryAnswer(mandatoryAnswer.isChecked());

        // Se almacenan las respuestas.
        for (int i = 0; i < answers.getAdapter().getCount(); i++) {
            Answer answer = new Answer();
            answer.setAnswer(answers.getAdapter().getItem(i).toString());
            answer.setAnswerType(1);
            answer.setAuthor(form.getAuthor());
            answer.setCreateDate(Calendar.getInstance().getTime());
            q.addAnswer(answer);
        }
        // Se añade la pregunta con las respuestas a la estructura del formulario y se llama a al actividad de
        // selección de pregunta.
        form.addQuestion(q);
    }

}
