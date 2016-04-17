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

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Calendar;

public class NameAndDescriptionDesignActivity extends AppCompatActivity {

    Form form;

    EditText formName;
    EditText formDescription;
    EditText formAuthor;
    CheckBox tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_and_description_design);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        formName = (EditText) findViewById(R.id.editTextFormDesignName);
        formDescription = (EditText) findViewById(R.id.editTextFormDesignDescription);
        formAuthor = (EditText) findViewById(R.id.editTextFormAuthor);
        tracking = (CheckBox) findViewById(R.id.checkBoxTracking);

        // Botón flotante para continuar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContinue);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Comprobamos que los campos hayan sido completados
                    if(formName.getText().length() == 0 || formDescription.getText().length() == 0
                            || formAuthor.getText().length() == 0){
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        // Comprobamos que no exista un formulario con el nombre introducido
                        if(!DbHelper.existsForm(getApplicationContext(),formName.getText().toString())) {
                            // Se crea el nuevo formulario y se llama a la actividad de selección de pregunta
                            form = new Form();
                            form.setName(formName.getText().toString());
                            form.setDescription(formDescription.getText().toString());
                            form.setAuthor(formAuthor.getText().toString());
                            form.setCreateDate(Calendar.getInstance().getTime());
                            form.setIsTracked(tracking.isChecked());
                            Intent intent = new Intent(getApplicationContext(), SelectQuestionTypeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra("form", form);
                            intent.putExtra("mode", SelectQuestionTypeActivity.MODE_FIRST_QUESTION);
                            startActivity(intent);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.existing_form, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });
        }
    }

}
