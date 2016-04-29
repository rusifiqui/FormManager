package com.novotec.formmanager.Synchronization;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.helpers.BaseVolleyActivity;
import com.novotec.formmanager.helpers.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class SelectSyncDesignFormActivity extends BaseVolleyActivity {

    Vector<Integer> formIds;
    ListView formsList;
    boolean selectedForm = false;
    int selectedFormId;
    Vector<String> formNames;
    String selectedFormName;
    boolean upload = false;

    ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sync_design_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        progress.setIndeterminate(true);
        progress.setTitle(R.string.connect_title);
        progress.setMessage(getResources().getString(R.string.connect_message));

        formsList = (ListView) findViewById(R.id.listViewSelectFormSync);
        getParameters();

        // Se cargan los formularios. El método de  carga depende de si se va a subir un formulario
        // al servidor o si se pretende descargar desde el servidor.
        if(upload) {
            getFormsTypes();
        }else{
            progress.show();
            makeRequestGetForms();
        }

        // Se guarda el formulario seleccionado y se indica que se ha realizado una selección.
        formsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedForm = true;
                if(upload) {
                    selectedFormId = formIds.get(position);
                    selectedFormName = formNames.get(position);
                }
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(upload){
                        progress.show();
                        makeRequestExistsForm(selectedFormName);
                    }else{
                        // TODO Descarga de un formulario. Hay que hacer el servicio web y llamar al método.
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
        formNames = new Vector<>();
        Vector<String> auxQuestion = new Vector<>();

        DbHelper expensesHelper =
                new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(getResources().getString(R.string.select_forms), null);


        if(cursor != null){
            int id = 0;
            while(cursor.moveToNext()){
                formIds.add(cursor.getInt(0));
                formNames.add(cursor.getString(1));
                auxQuestion.add(id+1 + ". " + cursor.getString(1));
                id++;
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
     * Método que recupera los parámetros que son pasados a la actividad.
     */
    private void getParameters(){
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            if (parameters.containsKey("upload")) {
                upload = parameters.getBoolean("upload");
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
        }
    }

    /**
     * Método que comprueba si un formulario ya existe en el servidor.
     * Nos pueden existir dos formularios con el mismo nombre.
     * @param formName  El nombre del formulario.
     */
    private void makeRequestExistsForm(final String formName){
        String url = "http://kikevila.noip.me:8080/formsApp/existsFormName.php";
        JSONObject params = new JSONObject();

        try {
            params.put("formName", formName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Se crea la petición
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject a = response.getJSONObject(0);
                            String as = a.getString("COUNT");
                            if(Integer.valueOf(as) != 0){
                                Toast toast = Toast.makeText(getApplicationContext(), "ERROR: Existen resultados", Toast.LENGTH_SHORT);
                                toast.show();
                            }else{
                                uploadFormDesign(selectedFormId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.print("skfksdjf");
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onConnectionFailed(error.toString());
                progress.dismiss();
            }
        });

        // Se añade la petición a la cola
        addToQueue(jsonObjReq);
    }

    /**
     * Método que sube al servidor un diseño de formulario seleccionado previamente por el usuario
     * @param formId    El identificador del diseño de formulario
     */
    private void uploadFormDesign(int formId){
        String url = "http://kikevila.noip.me:8080/formsApp/uploadFormDesign.php";
        JSONObject params = new JSONObject();

        try {
            Form f = DbHelper.getFormDesign(getApplicationContext(), formId);
            final Gson gson = new Gson();
            String json = gson.toJson(f);
            params.put("form", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Se crea la petición
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject a = response.getJSONObject(0);
                            String as = a.getString("result");
                            Toast toast;
                            if(as.equals("OK")){
                                toast = Toast.makeText(getApplicationContext(), "Formulario cargado correctamente", Toast.LENGTH_SHORT);

                            }else{
                                toast = Toast.makeText(getApplicationContext(), "ERROR: No se podido cargar el formulario", Toast.LENGTH_SHORT);
                            }
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onConnectionFailed(error.toString());
                progress.dismiss();
            }
        });

        // Se añade la petición a la cola
        addToQueue(jsonObjReq);
    }

    /**
     * Método que recupera los formularios disponibles en el servidor
     */
    private void makeRequestGetForms() {
        String url = "http://kikevila.noip.me:8080/formsApp/getFormDesigns.php";

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        formIds = new Vector<>();
                        formNames = new Vector<>();
                        Vector<String> forms = new Vector<>();
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject a = response.getJSONObject(i);
                                forms.add(a.getInt("ID") + ". " + a.getString("NAME") + "\nAutor: " + a.getString("AUTHOR") + "\nDescripción: " + a.getString("DESCRIPTION"));
                                formIds.add(a.getInt("ID"));
                                formNames.add(a.getString("NAME"));
                            }
                            String[] formsAdpat = new String[forms.size()];
                            for(int i = 0; i < forms.size(); i++){
                                formsAdpat[i] = forms.get(i);
                            }

                            // ArrayAdapter para la lista de elementos
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectSyncDesignFormActivity.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, formsAdpat);
                            formsList.setAdapter(adapter);
                            progress.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.print("skfksdjf");
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onConnectionFailed(error.toString());
                progress.dismiss();
            }
        });
        addToQueue(jsonObjReq);
    }
}
