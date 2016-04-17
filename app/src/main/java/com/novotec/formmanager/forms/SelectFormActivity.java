package com.novotec.formmanager.forms;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.novotec.formmanager.R;
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Vector;

public class SelectFormActivity extends AppCompatActivity {

    private Vector formIds;
    ListView formsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tvAvailableForms = (TextView) findViewById(R.id.textViewAvailableForms);

        formsList = (ListView) findViewById(R.id.listViewFormSelect);
        if (formsList != null) formsList.requestFocus();



        getFormsTypes();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DbHelper.getFormDesign(getApplicationContext(), 1);
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
