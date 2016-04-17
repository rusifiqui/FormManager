package com.novotec.formmanager.forms;

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



        getFormsTypes();
        formsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectForm = true;
                selectedFormId = position;
            }
        });
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
                        int firstQuestion = aForm.getFormStructure().getQuestions().get(0).getQuestionType();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.fields_incomplete, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    private void getFormsTypes() {

        String[] forms;
        formIds = new Vector<>();
        Vector<String> auxQuestion = new Vector<>();

        DbHelper expensesHelper =
                new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID_FORM_DESIGN, FORM_NAME FROM FORM_DESIGN", null);
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
        }
    }

}
