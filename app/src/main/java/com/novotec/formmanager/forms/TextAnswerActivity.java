package com.novotec.formmanager.forms;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;

public class TextAnswerActivity extends AppCompatActivity {

    Form form;
    boolean mandatory = false;

    TextView question;
    EditText answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = (TextView) findViewById(R.id.textViewQuestionText);
        answer = (EditText) findViewById(R.id.editTextTextAnswer);

        getParameters();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Función que establece los parámetros de la actividad.
     * Los parámetros se reciben al crear la vista a través de un Intent
     */
    private void getParameters(){
        Bundle parameters = getIntent().getExtras();
        if(parameters != null){
            if(parameters.containsKey("form")){
                form = (Form) parameters.get("form");
            }else{
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if(parameters.containsKey("question")) {
                question.setText(parameters.getString("question"));
            }else{
                question.setText(R.string.question_error);
            }
            if(parameters.containsKey("mandatory"))
                mandatory = parameters.getBoolean("mandatory");
        }
    }

}
