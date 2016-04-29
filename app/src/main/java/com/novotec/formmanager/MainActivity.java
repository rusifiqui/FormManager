package com.novotec.formmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.novotec.formmanager.formManagement.DeleteFormActivity;
import com.novotec.formmanager.formManagement.ReviewFormActivity;
import com.novotec.formmanager.Synchronization.SyncActivity;
import com.novotec.formmanager.forms.SelectFormActivity;
import com.novotec.formmanager.formsDesigner.NameAndDescriptionDesignActivity;
import com.novotec.formmanager.helpers.DbHelper;

public class MainActivity extends AppCompatActivity {

    private static final boolean NOVOTEC_MODE = true;

    TextView prueba;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);
        ImageView logo = (ImageView) findViewById(R.id.imageViewLogo);

        Button newFormDesign = (Button) findViewById(R.id.buttonNewFormDesign);
        Button fillForm = (Button) findViewById(R.id.buttonFillForm);
        Button deleteForm = (Button) findViewById(R.id.buttonDeleteForm);
        Button reviewForm = (Button) findViewById(R.id.buttonViewForm);
        Button modifyForm = (Button) findViewById(R.id.buttonModifyForm);
        Button sync = (Button) findViewById(R.id.buttonSync);
        prueba = (TextView) findViewById(R.id.textView);

        if(!NOVOTEC_MODE){
            layout.removeView(logo);
        }
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
        if (deleteForm != null) {
            deleteForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DeleteFormActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (reviewForm != null) {
            reviewForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ReviewFormActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            });
        }
        if (modifyForm != null) {
            modifyForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Pendiente
                }
            });
        }
        if(sync != null){
            sync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
                    startActivity(intent);
                }
            });
        }
        // Fin Listeners de los botones
        db.close();
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
