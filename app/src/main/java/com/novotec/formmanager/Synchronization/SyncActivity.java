package com.novotec.formmanager.synchronization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.novotec.formmanager.R;
import com.novotec.formmanager.helpers.BaseVolleyActivity;

public class SyncActivity extends BaseVolleyActivity {

    Button formDesignUpload;
    Button formDesignDownload;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        formDesignUpload = (Button) findViewById(R.id.buttonFormDesignUpload);
        formDesignDownload = (Button) findViewById(R.id.buttonFormDesignDownload);


        // Listeners para los botones
        formDesignUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForm(true);
            }
        });
        formDesignDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForm(false);
            }
        });
        // FIN Listeners para los botones
    }


    private void selectForm(boolean upload){
        if(upload){
            Intent intent = new Intent(getApplicationContext(), SelectSyncDesignFormActivity.class);
            intent.putExtra("upload", true);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(), SelectSyncDesignFormActivity.class);
            intent.putExtra("upload", false);
            startActivity(intent);
        }
    }
}
