package com.novotec.formmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PrefsActivity extends AppCompatActivity {

    // Ajustes del servidor
    EditText server;
    EditText user;
    EditText password;

    // Ajustes del tracking
    EditText actTime;
    EditText actDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        server = (EditText) findViewById(R.id.editTextServer);
        user = (EditText) findViewById(R.id.editTextUser);
        password = (EditText) findViewById(R.id.editTextPassword);
        actTime = (EditText) findViewById(R.id.editTextActTime);
        actDistance = (EditText) findViewById(R.id.editTextActDistance);

        Button save = (Button) findViewById(R.id.buttonSave);

        getPreferences();

        assert save != null;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                Toast toast = Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void savePreferences(){
        SharedPreferences prefs = getSharedPreferences("dbPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("dbserver", server.getText().toString());
        editor.putString("dbuser", user.getText().toString());
        editor.putString("dbpass", password.getText().toString());
        editor.putString("actTime", (actTime.getText().toString()));
        editor.putString("actDistance", actDistance.getText().toString());
        editor.commit();
    }

    private void getPreferences(){
        SharedPreferences prefs = getSharedPreferences("dbPreferences",Context.MODE_PRIVATE);
        server.setText(prefs.getString("dbserver", ""));
        user.setText(prefs.getString("dbuser", ""));
        password.setText(prefs.getString("dbpass", ""));
        actTime.setText(prefs.getString("actTime", ""));
        actDistance.setText(prefs.getString("actDistance", ""));
    }
}
