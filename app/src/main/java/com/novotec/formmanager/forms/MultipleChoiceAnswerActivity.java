package com.novotec.formmanager.forms;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;

public class MultipleChoiceAnswerActivity extends AppCompatActivity {

    private AnsweredForm form;
    private boolean mandatory = false;
    private TextView questionTextView;
    String[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayoutQuestions);
        questionTextView = (TextView) findViewById(R.id.textViewQuestionMultipleChoiceQuestion);

        getParameters();

        // Se establecen las opciones de respuesta
        final CheckBox[] cb = new CheckBox[answers.length];
        for(int i = 0; i < answers.length; i++) {
            cb[i] = new CheckBox(this);
            cb[i].setText(answers[i]);
            if (ll != null) {
                ll.addView(cb[i]);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // TODO
            }
        });
    }

    private void getParameters(){
        Bundle parameters = getIntent().getExtras();
        if(parameters != null){
            if(parameters.containsKey("form")){
                form = (AnsweredForm) parameters.get("form");
            }else{
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if(parameters.containsKey("question")) {
                questionTextView.setText(parameters.getString("question"));
            }else{
                questionTextView.setText(R.string.question_error);
            }
            if(parameters.containsKey("answers")) {
                answers = parameters.getStringArray("answers");
            }else{
                answers = new String[]{getResources().getString(R.string.answers_error)};
            }
            if(parameters.containsKey("mandatory"))
                mandatory = parameters.getBoolean("mandatory");
        }
    }

}
