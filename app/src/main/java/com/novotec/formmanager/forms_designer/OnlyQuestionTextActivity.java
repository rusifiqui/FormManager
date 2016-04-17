package com.novotec.formmanager.forms_designer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.entities.Question;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Calendar;

public class OnlyQuestionTextActivity extends AppCompatActivity {

    private Form form;
    EditText questionText;
    int questionType;
    CheckBox mandatoryAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_question_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionText = (EditText) findViewById(R.id.editTextOnlyQuestionText);
        mandatoryAnswer = (CheckBox) findViewById(R.id.checkBoxMandatoryQuestion);

        setData();

        // Botón continuar. Se almacenan los datos en la estructura de datos y se llama a la actividad para introducir una
        // nueva pregunta.
        FloatingActionButton fabContinue = (FloatingActionButton) findViewById(R.id.fabContinueOnlyQuestionText);
        if (fabContinue != null) {
            fabContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(questionText.getText().length() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        setForm();
                        Intent intent = new Intent(getApplicationContext(), SelectQuestionTypeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("form", form);
                        intent.putExtra("mode", SelectQuestionTypeActivity.MODE_ADD_QUESTION);
                        startActivity(intent);

                    }
                }
            });
        }

        FloatingActionButton fabFinish = (FloatingActionButton) findViewById(R.id.fabFinishOnlyQuestionText);
        if (fabFinish != null) {
            fabFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(questionText.getText().length() == 0) {
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
        Question q = new Question();
        q.setAuthor(form.getAuthor());
        q.setCreateDate(Calendar.getInstance().getTime());
        q.setQuestion(questionText.getText().toString());
        q.setQuestionType(questionType);
        q.setMandatoryAnswer(mandatoryAnswer.isChecked());
        form.addQuestion(q);
    }

}
