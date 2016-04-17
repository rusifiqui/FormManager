package com.novotec.formmanager.forms_designer;

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
import android.widget.ListView;
import android.widget.Toast;

import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Vector;

public class SelectQuestionTypeActivity extends AppCompatActivity {

    public static final String MODE_FIRST_QUESTION = "MODE_FIRST_QUESTION";
    public static final String MODE_ADD_QUESTION = "MODE_ADD_QUESTION";

    private Form form;
    Vector<Integer> questionIds;
    ListView questionTypes;
    String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_question_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fabFinish = (FloatingActionButton) findViewById(R.id.fabFinishQuestionSelect);
        questionTypes = (ListView) findViewById(R.id.listViewQuestionType);

        setData();
        getQuestionTypes();

        if(mode.equals(MODE_FIRST_QUESTION)){
            if (fabFinish != null) {
                fabFinish.hide();
            }
        }

        // Listener para los elementos de la lista
        questionTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = parent.getAdapter().getItem(position).toString();
                Intent intent;
                // TODO cambiar los valores del case
                switch(selectedValue){
                    case "Respuesta única":
                    case "Respuesta múltiple":
                        intent = new Intent(getApplicationContext(), QuestionTextAndAnswersActivity.class);
                        intent.putExtra("form",form);
                        intent.putExtra("question_type", questionIds.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    break;
                    case "Realizar fotografía":
                    case "Seleccionar ubicación":
                    case "Texto libre":
                        intent = new Intent(getApplicationContext(), OnlyQuestionTextActivity.class);
                        intent.putExtra("form",form);
                        intent.putExtra("question_type", questionIds.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    break;
                    default:
                        throw new RuntimeException(getResources().getString(R.string.question_select_error));
                }
            }
        });

        if (fabFinish != null) {
            fabFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            });
        }
    }

    private void getQuestionTypes() {

        String[] questions;
        questionIds = new Vector<>();
        Vector<String> auxQuestion = new Vector<>();

        DbHelper expensesHelper =
                new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID_QUESTION_TYPE,QUESTION_TYPE FROM QUESTION_TYPE", null);
        if(cursor != null){
            while(cursor.moveToNext()){
                questionIds.add(cursor.getInt(0));
                auxQuestion.add(cursor.getString(1));
            }
            questions = new String[auxQuestion.size()];
            for(int i = 0; i < auxQuestion.size(); i++){
                questions[i] = auxQuestion.get(i);
            }
            setAdapter(questions);
            cursor.close();
        }
    }

    private void setAdapter(String[] values){
        // ArrayAdapter para la lista de elementos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        questionTypes.setAdapter(adapter);
    }

    private void setData() {
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            if (parameters.containsKey("form")) {
                form = (Form) parameters.get("form");
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if (parameters.containsKey("mode")) {
                mode = parameters.get("mode").toString();
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
        }
    }

}
