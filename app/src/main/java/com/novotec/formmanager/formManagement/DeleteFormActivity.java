package com.novotec.formmanager.formManagement;

import android.content.Context;
import android.content.DialogInterface;
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
import com.novotec.formmanager.helpers.DbHelper;

import java.util.Vector;

public class DeleteFormActivity extends AppCompatActivity {

    private Vector<Integer> formIds;
    ListView formsList;
    private boolean selectForm = false;
    private int selectedFormId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        formsList = (ListView) findViewById(R.id.listViewDeleteForm);
        getFormsTypes();

        // Se guarda el formulario seleccionado y se indica que se ha realizado una selección
        formsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectForm = true;
                selectedFormId = formIds.get(position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabDeleteForm);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectForm) {
                        // Se llama al método que crea un diálogo de confirmación
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

    /**
     * Método que genera un diálogo para alertar al usuario de que se va a eliminar un formulario.
     * En caso de desear continuar, se elimina al formulario.
     * @param formId Id del formulario a eliminar
     */
    protected void createDeleteDialog(final int formId){
        final Context c = this;
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_confirmation)
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int result = DbHelper.deleteFormDesign(getApplicationContext(), formId, false);
                        // Se actualiza el listado de formularios tras la eliminación
                        switch (result) {
                            case -2:
                                AlertDialog.Builder builder2 =
                                        new AlertDialog.Builder(c);
                                builder2.setMessage(R.string.delete_user_forms_confirmation)
                                        .setTitle(R.string.attention)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                int result = DbHelper.deleteFormDesign(getApplicationContext(), formId, true);
                                                // Se actualiza el listado de formularios tras la eliminación
                                                switch (result) {
                                                    case -1:
                                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.delete_form_error, Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        break;
                                                    case 0:
                                                        // Se actualiza la vista
                                                        getFormsTypes();
                                                        break;
                                                }
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog confirmDialog2 = builder2.create();
                                confirmDialog2.show();
                                break;
                            case -1:
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.delete_form_error, Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case 0:
                                // Se actualiza la vista
                                getFormsTypes();
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
