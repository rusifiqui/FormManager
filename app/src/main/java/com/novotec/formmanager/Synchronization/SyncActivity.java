package com.novotec.formmanager.Synchronization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.novotec.formmanager.R;
import com.novotec.formmanager.helpers.BaseVolleyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncActivity extends BaseVolleyActivity {

    Button formDesignUpload;
    Button formDesignDownload;

    TextView result;
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

        // Listeners para los botones

        result = (TextView) findViewById(R.id.textViewResult);
        makeRequest();
    }

    private void makeRequest(){
        String url = "http://kikevila.noip.me:8080/formsApp/prueba.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                result.setText(jsonArray.toString());
                try {
                    JSONObject a = jsonArray.getJSONObject(0);

                    String as = a.getString("QUESTION_TYPE");
                    System.out.print("skfksdjf");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onConnectionFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onConnectionFailed(volleyError.toString());
                result.setText(volleyError.toString());
            }
        }){ @Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("formName","hola");
            return params;
        }};

        addToQueue(request);
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
