package com.novotec.formmanager.forms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Calendar;
import java.util.Vector;

/**
 * Clase que permite seleccionar uno de los formularios disponibles y comenzar a rellenarlo.
 * @author jvilam
 * @version 1
 * @since 15/04/2016
 */
public class SelectFormActivity extends AppCompatActivity {

    private Vector<Integer> formIds;

    TextView tvAvailableForms;
    ListView formsList;
    EditText description;
    EditText userName;

    boolean selectForm = false;
    int selectedFormId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvAvailableForms = (TextView) findViewById(R.id.textViewAvailableForms);
        description = (EditText) findViewById(R.id.editTextComments);
        userName = (EditText) findViewById(R.id.editTextUserSelect);

        formsList = (ListView) findViewById(R.id.listViewFormSelect);
        if (formsList != null) formsList.requestFocus();

        // Se recuperan los formularios disponibles
        getFormsTypes();
        // Se guarda el formulario seleccionado y se indica que se ha realizado una selección
        formsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectForm = true;
                selectedFormId = position;
            }
        });

        // Botón que inicia el formulario
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectForm &&
                            description.getText().toString().length() > 0 &&
                            userName.getText().toString().length() > 0) {
                        AnsweredForm aForm = new AnsweredForm();
                        aForm.setFormStructure(DbHelper.getFormDesign(getApplicationContext(), formIds.get(selectedFormId)));
                        aForm.setCreateDate(Calendar.getInstance().getTime());
                        aForm.setDescription(description.getText().toString());
                        aForm.setUserName(userName.getText().toString());
                        aForm.setCurrentQuestion(1);
                        int questionType = aForm.getQuestionType(0);
                        generateFormStep(questionType, aForm);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    /**
     * Método que recupera los formularios que están disponibles para ser cumplimentados por el usuario
     */
    private void getFormsTypes() {

        String[] forms;
        formIds = new Vector<>();
        Vector<String> auxQuestion = new Vector<>();

        DbHelper expensesHelper =
                new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(getResources().getString(R.string.select_forms), null);
        if(cursor != null){
            while(cursor.moveToNext()){
                formIds.add(cursor.getInt(0));
                auxQuestion.add(cursor.getString(1));
            }
            forms = new String[auxQuestion.size()];
            for(int i = 0; i < auxQuestion.size(); i++){
                forms[i] = auxQuestion.get(i);
            }

            // ArrayAdapter para la lista de elementos
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, forms);
            formsList.setAdapter(adapter);
            cursor.close();
            db.close();
        }
    }

    /** Método que genera una pregunta del formulario.
     * @param questionType Tipo de pregunta
     * @param aForm Información del formulario
     */
    private void generateFormStep(int questionType, AnsweredForm aForm){
        switch(questionType){
            case 1:
                Intent intentSingleQuestion = new Intent(getApplicationContext(), SingleAnswerActivity.class);
                intentSingleQuestion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentSingleQuestion.putExtra("form", aForm);
                intentSingleQuestion.putExtra("question", aForm.getQuestionText(0));
                intentSingleQuestion.putExtra("answers", aForm.getAnswers(0));
                intentSingleQuestion.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentSingleQuestion);
                break;
            case 2:
                Intent intentMultipleAnswer = new Intent(getApplicationContext(), MultipleChoiceAnswerActivity.class);
                intentMultipleAnswer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentMultipleAnswer.putExtra("form", aForm);
                intentMultipleAnswer.putExtra("question", aForm.getQuestionText(0));
                intentMultipleAnswer.putExtra("answers", aForm.getAnswers(0));
                intentMultipleAnswer.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentMultipleAnswer);
                break;
            case 3:
                Intent intentPhoto = new Intent(getApplicationContext(), PhotoAnswerActivity.class);
                //intentPhoto.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentPhoto.putExtra("form", aForm);
                intentPhoto.putExtra("question", aForm.getQuestionText(0));
                intentPhoto.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentPhoto);
                break;
            case 4:
                Intent intentLocation = new Intent(getApplicationContext(), MapAnswerActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentLocation.putExtra("form", aForm);
                intentLocation.putExtra("question", aForm.getQuestionText(0));
                intentLocation.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentLocation);
                break;
            case 5:
                Intent intentText = new Intent(getApplicationContext(), TextAnswerActivity.class);
                intentText.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentText.putExtra("form", aForm);
                intentText.putExtra("question", aForm.getQuestionText(0));
                intentText.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentText);
                break;
            case 6:
                Intent intentBarCode = new Intent(getApplicationContext(), BarCodeAnswerActivity.class);
                intentBarCode.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentBarCode.putExtra("form", aForm);
                intentBarCode.putExtra("question", aForm.getQuestionText(0));
                intentBarCode.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                startActivity(intentBarCode);
                break;
            default:
                throw new RuntimeException(getResources().getString(R.string.question_type_error));
        }
    }

}
