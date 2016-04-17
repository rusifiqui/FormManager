package com.novotec.formmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.forms.MapAnswerActivity;
import com.novotec.formmanager.forms.MultipleChoiceAnswerActivity;
import com.novotec.formmanager.forms.SelectFormActivity;
import com.novotec.formmanager.forms_designer.NameAndDescriptionDesignActivity;
import com.novotec.formmanager.helpers.DbHelper;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button newFormDesign = (Button) findViewById(R.id.buttonNewFormDesign);
        Button fillForm = (Button) findViewById(R.id.buttonFillForm);

        // Objeto para manejar la base de datos
        DbHelper dbHelper = new DbHelper(getApplicationContext(), DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();

        // Listeners de los botones
        if (newFormDesign != null) {
            newFormDesign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), NameAndDescriptionDesignActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            });
        }
        if (fillForm != null) {
            fillForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SelectFormActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            });
        }
        // Fin Listeners de los botones

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Se está usando este método para probar los diferentes tipos de actividades de preguntas. Esto es PROVISIONAL
                Form f = new Form();
                // Prueba MapAnswerActivity
                Intent intent = new Intent(getApplicationContext(), MapAnswerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("form",f);
                intent.putExtra("question", "Seleccione la ubicación de la instalación");
                intent.putExtra("mandatory", true);
                startActivity(intent);

            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        assert fab2 != null;
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Se está usando este método para probar los diferentes tipos de actividades de preguntas. Esto es PROVISIONAL
                Form f = new Form();

                Intent intent = new Intent(getApplicationContext(), MultipleChoiceAnswerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                String a[] = new String[]{"Hola", "Adiós", "Casa", "Perrito", "Pájaro grande", "Oso pardo", "skldjksjd kdjs ksd", "23 horas"};
                intent.putExtra("answers", a);
                intent.putExtra("form",f);
                intent.putExtra("question", "Realice una fotografía para evidenciar el avistamiento");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
