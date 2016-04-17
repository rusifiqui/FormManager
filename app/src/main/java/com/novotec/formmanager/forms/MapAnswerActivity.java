package com.novotec.formmanager.forms;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;

/**
 * Actividad que sirve para representar una pregunta la cual es respondida mediante la representación
 * de una ubicación.
 * @author jvilam
 * @version 1
 * @since 04/04/2016
 */
public class MapAnswerActivity extends FragmentActivity implements OnMapReadyCallback {

    private AnsweredForm form;
    private GoogleMap answerMap;
    private TextView questionTextView;
    private boolean mandatory = false;
    private boolean gotLocation = false;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_answer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        questionTextView = (TextView) findViewById(R.id.textViewQuestionMapQuestion);
        // Recuperamos los parámetros que se envían a la actividad a través del intent de creación.
        getParameters();

        // Botones flotantes para modificar la vista del mapa
        FloatingActionButton fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        assert fabMap != null;
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)
                    answerMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        FloatingActionButton fabSatellite = (FloatingActionButton) findViewById(R.id.fabSatellite);
        assert fabSatellite != null;
        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
                    answerMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        // Fin Botones flotantes para modificar la vista del mapa

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if(!gotLocation) {
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                    setLastLocation(pos);
                    gotLocation = true;
                    answerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(pos.latitude, pos.longitude), 16));
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}

        };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        answerMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        answerMap.setMyLocationEnabled(true);
        LatLng pos = getLastLocation();
        if (pos != null) {
            answerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(pos.latitude, pos.longitude), 10));
        } else {
            answerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(-18.142, 178.431), 2));
        }

        // Si se ha podido crear el mapa, se establece el listener para poder establecer el marcador de posición,
        // el cual será selecionado por el usuario.
        answerMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                selectedLocation = point;
                answerMap.clear();
                answerMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                selectedLocation = new LatLng(point.latitude, point.longitude);
            }
        });
    }


    /**
     * Función que establece los parámetros de la actividad.
     * Los parámetros se reciben al crear la vista a través de un Intent
     */
    private void getParameters() {
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            if (parameters.containsKey("form")) {
                form = (AnsweredForm) parameters.get("form");
            } else {
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if (parameters.containsKey("question")) {
                questionTextView.setText(parameters.getString("question"));
            } else {
                questionTextView.setText(R.string.question_error);
            }
            if (parameters.containsKey("mandatory"))
                mandatory = parameters.getBoolean("mandatory");
        }
    }

    private void setLastLocation(LatLng loc) {
        SharedPreferences prefs = getSharedPreferences("locationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lat", String.valueOf(loc.latitude));
        editor.putString("long", String.valueOf(loc.longitude));
        editor.commit();
    }

    private LatLng getLastLocation() {

        SharedPreferences prefs = getSharedPreferences("locationPrefs", Context.MODE_PRIVATE);
        LatLng pos = null;
        if (prefs != null) {
            pos = new LatLng(Double.valueOf(prefs.getString("lat", "0")), Double.valueOf(prefs.getString("long", "0")));
        }
        return pos;
    }

}
