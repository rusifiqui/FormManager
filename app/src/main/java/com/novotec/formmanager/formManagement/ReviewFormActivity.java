package com.novotec.formmanager.formManagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.forms.MapAnswerActivity;
import com.novotec.formmanager.forms.MultipleChoiceAnswerActivity;
import com.novotec.formmanager.forms.PhotoAnswerActivity;
import com.novotec.formmanager.forms.SingleAnswerActivity;
import com.novotec.formmanager.forms.TextAnswerActivity;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Vector;

public class ReviewFormActivity extends AppCompatActivity {

    Vector<Integer> formIds;
    ListView formsList;
    boolean selectedForm = false;
    int selectedFormId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        formsList = (ListView) findViewById(R.id.listViewReviewForm);

        getFormsTypes();

        // Se guarda el formulario seleccionado y se indica que se ha realizado una selección.
        formsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedForm = true;
                selectedFormId = formIds.get(position);
            }
        });

        // Botón para ver el formulario seleccionado
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedForm) {
                        AnsweredForm form = DbHelper.getForm(getApplicationContext(), selectedFormId);
                        generateFormStep(form.getQuestionType(0), form);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.select_form_to_view, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }

        // Botón para eliminar el formulario seleccionado
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        if (fabDelete != null) {
            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedForm){
                        createDeleteDialog(selectedFormId);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.select_form, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    /**
     * Método que recupera los formularios que están disponibles.
     */
    private void getFormsTypes() {

        String[] forms;
        formIds = new Vector<>();
        Vector<String> auxQuestion = new Vector<>();

        DbHelper expensesHelper =
                new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(getResources().getString(R.string.select_review_forms), null);


        if(cursor != null){
            while(cursor.moveToNext()){
                formIds.add(cursor.getInt(0));
                auxQuestion.add(cursor.getInt(0) + ". " + getResources().getString(R.string.type) + ": " + cursor.getString(1)
                        + "\n" + getResources().getString(R.string.date) + ": " + cursor.getString(2)
                        + "\n" + getResources().getString(R.string.description) + ": " + cursor.getString(3) + "\n");
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
                intentSingleQuestion.putExtra("mode", MapAnswerActivity.REVIEW_MODE);
                startActivity(intentSingleQuestion);
                break;
            case 2:
                Intent intentMultipleAnswer = new Intent(getApplicationContext(), MultipleChoiceAnswerActivity.class);
                intentMultipleAnswer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentMultipleAnswer.putExtra("form", aForm);
                intentMultipleAnswer.putExtra("question", aForm.getQuestionText(0));
                intentMultipleAnswer.putExtra("answers", aForm.getAnswers(0));
                intentMultipleAnswer.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                intentMultipleAnswer.putExtra("mode", MapAnswerActivity.REVIEW_MODE);
                startActivity(intentMultipleAnswer);
                break;
            case 3:
                Intent intentPhoto = new Intent(getApplicationContext(), PhotoAnswerActivity.class);
                intentPhoto.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentPhoto.putExtra("form", aForm);
                intentPhoto.putExtra("question", aForm.getQuestionText(0));
                intentPhoto.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                intentPhoto.putExtra("mode", MapAnswerActivity.REVIEW_MODE);
                startActivity(intentPhoto);
                break;
            case 4:
                Intent intentLocation = new Intent(getApplicationContext(), MapAnswerActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentLocation.putExtra("form", aForm);
                intentLocation.putExtra("question", aForm.getQuestionText(0));
                intentLocation.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                intentLocation.putExtra("mode", MapAnswerActivity.REVIEW_MODE);
                startActivity(intentLocation);
                break;
            case 5:
                Intent intentText = new Intent(getApplicationContext(), TextAnswerActivity.class);
                intentText.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentText.putExtra("form", aForm);
                intentText.putExtra("question", aForm.getQuestionText(0));
                intentText.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(0).isMandatoryAnswer());
                intentText.putExtra("mode", MapAnswerActivity.REVIEW_MODE);
                startActivity(intentText);
                break;
            default:
                throw new RuntimeException(getResources().getString(R.string.question_type_error));
        }
    }

    /**
     * Método que genera un diálogo para alertar al usuario de que se va a eliminar un formulario.
     * En caso de desear continuar, se elimina al formulario.
     * @param f Id del formulario a eliminar
     */
    protected void createDeleteDialog(final int f){
        final Context c = this;
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_confirmation)
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int result = DbHelper.deleteUserFormFromMenu(c, f);
                        // Se actualiza el listado de formularios tras la eliminación
                        switch (result) {
                            case -1:
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.delete_form_error, Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case 0:
                                // Se actualiza la vista
                                getFormsTypes();
                                selectedForm = false;
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog confirmDialog = builder.create();
        confirmDialog.show();
    }

}
