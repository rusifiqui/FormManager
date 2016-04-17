package com.novotec.formmanager.forms;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Form;

import java.util.Vector;

/**
 * Actividad que sirve para representar una pregunta con una única respuesta.
 * Las posibles respuestas se representan mediante una lista.
 * En la parte superior de la pantalla se encuentra la pregunta.
 * Se ha implementado un buscador para que sea más sencillo seleccionar la respuesta adecuada.
 * @author jvilam
 * @version 1
 * @since 04/04/2016
 */
public class SingleAnswerActivity extends AppCompatActivity {

    private Form form;

    ListView elements;
    EditText searchCriteria;
    TextView questionTextView;
    String[] values;
    String selectedValue;
    boolean mandatory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        elements = (ListView) findViewById(R.id.listViewSingleAnswer);
        searchCriteria = (EditText) findViewById(R.id.editTextSingleAnswer);
        questionTextView = (TextView) findViewById(R.id.textViewQuestionSingleQuestion);

        // Recuperamos los parámetros que se envían a la actividad a través del intent de creación.
        getParameters();

        selectedValue = "";
        setAdapter(values);

        searchCriteria.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Vector<String> vec = new Vector<>();
                for (String value : values) {
                    if (value.toUpperCase().contains(searchCriteria.getText().toString().toUpperCase())) {
                        vec.add(value);
                    }
                }
                String[] a = new String[vec.size()];
                for (int i = 0; i < vec.size(); i++) {
                    a[i] = vec.get(i);
                }
                setAdapter(a);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}


        });

        // Listener para los elementos de la lista
        elements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = parent.getAdapter().getItem(position).toString();
                searchCriteria.setText(selectedValue);

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Acción del botón
            }
        });
    }

    /**
     * Función que establece el adapatador de la ListView y establece los valores a mostrar
     * @param values Los valores a mostrar
     */
    private void setAdapter(String[] values){
        // ArrayAdapter para la lista de elementos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        elements.setAdapter(adapter);
    }

    /**
     * Función que establece los parámetros de la actividad.
     * Los parámetros se reciben al crear la vista a través de un Intent
     */
    private void getParameters(){
        Bundle parameters = getIntent().getExtras();
        if(parameters != null){
            if(parameters.containsKey("form")){
                form = (Form) parameters.get("form");
            }else{
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if(parameters.containsKey("question")) {
                questionTextView.setText(parameters.getString("question"));
            }else{
                questionTextView.setText(R.string.question_error);
            }
            if(parameters.containsKey("answers")) {
                values = parameters.getStringArray("answers");
            }else{
                values = new String[]{getResources().getString(R.string.answers_error)};
            }
            if(parameters.containsKey("mandatory"))
                mandatory = parameters.getBoolean("mandatory");
        }
    }

}
