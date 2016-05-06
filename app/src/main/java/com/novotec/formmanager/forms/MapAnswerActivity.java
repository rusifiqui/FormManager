package com.novotec.formmanager.forms;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.entities.AnsweredQuestion;
import com.novotec.formmanager.helpers.DbHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Actividad que sirve para representar una pregunta la cual es respondida mediante la representación
 * de una ubicación.
 * @author jvilam
 * @version 1
 * @since 04/04/2016
 */
public class MapAnswerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final boolean NOVOTEC = false;

    public static final int ANSWER_MODE = 1;
    public static final int REVIEW_MODE = 2;

    private AnsweredForm form;
    private GoogleMap answerMap;
    private TextView questionTextView;
    private boolean mandatory = false;
    private boolean gotLocation = false;
    private LatLng selectedLocation;
    private int mode = 1;
    int currentQuestion;

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
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)
                    answerMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        FloatingActionButton fabSatellite = (FloatingActionButton) findViewById(R.id.fabSatellite);
        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
                    answerMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        FloatingActionButton fabContinue = (FloatingActionButton) findViewById(R.id.fabContinueMap);
        fabContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == ANSWER_MODE){
                    // Se comprueba si la respuesta es obligatoria y si el usuario ha introducido texto
                    if (mandatory && selectedLocation == null) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.mandatory_answer, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        AnsweredQuestion userAnswer = new AnsweredQuestion();
                        if (selectedLocation != null) {
                            userAnswer.setLat(String.valueOf(selectedLocation.latitude));
                            userAnswer.setLon(String.valueOf(selectedLocation.longitude));
                            userAnswer.setAddress(getAddress());
                        } else {
                            userAnswer.setLat("");
                            userAnswer.setLon("");
                            userAnswer.setAddress("");
                        }
                        userAnswer.setIdUserQuestion(form.getCurrentQuestion());
                        form.addAnswer(userAnswer);
                        form.setCurrentQuestion(currentQuestion + 1);
                        if (form.getCurrentQuestion() < form.getQuestionCount() + 1) {
                            generateFormStep(form.getQuestionType(currentQuestion), form, true);
                        } else {
                            Toast toast;
                            if (DbHelper.saveForm(form, getApplicationContext())) {
                                toast = Toast.makeText(getApplicationContext(), R.string.form_saved, Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intentMainActivity);
                            } else {
                                toast = Toast.makeText(getApplicationContext(), R.string.form_not_saved, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }else{
                    // Si estamos en modo de revisión, se comprueba si es la última pregunta del formulario.
                    // En caso contrario, se llama a la siguiente pregunta
                    if(form.getCurrentQuestion() < form.getQuestionCount() - 1 ){
                        form.setCurrentQuestion(currentQuestion + 1);
                        generateFormStep(form.getQuestionType(form.getCurrentQuestion()), form, false);
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.form_end, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
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
                    if(mode == ANSWER_MODE) {
                        answerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(pos.latitude, pos.longitude), 16));
                    }
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

        if(mode == ANSWER_MODE) {
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
                    if(NOVOTEC) {
                        answerMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                    }else{
                        answerMap.addMarker(new MarkerOptions().position(point));
                    }
                    selectedLocation = new LatLng(point.latitude, point.longitude);
                }
            });
        }else{
            LatLng point = new LatLng(Double.valueOf(form.getAnsweredQuestions().get(currentQuestion).getLat()), Double.valueOf(form.getAnsweredQuestions().get(currentQuestion).getLon()));
            // Si estamos en modo revisión se pone un marcador
            // Y se mueve la cámara al marcador
            answerMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(point.latitude, point.longitude), 16));
            if(NOVOTEC) {
                answerMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            }else{
                answerMap.addMarker(new MarkerOptions().position(point));
            }
        }
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
            if (parameters.containsKey("mode"))
                mode = parameters.getInt("mode");

            currentQuestion = form.getCurrentQuestion();
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

    /**
     * Función que devuelve una dirección obtenida a través de una latitud y una longitud
     * @return La dirección correspondiente a la localización
     */
    private String getAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            return address + "/" + city + "/" + postalCode + "/" + country;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /** Método que genera una pregunta del formulario
     * @param questionType Tipo de pregunta
     * @param aForm Información del formulario
     */
    private void generateFormStep(int questionType, AnsweredForm aForm, boolean answeMode){
        switch (questionType) {
            case 1:
                Intent intentSingleQuestion = new Intent(getApplicationContext(), SingleAnswerActivity.class);
                intentSingleQuestion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentSingleQuestion.putExtra("form", aForm);
                intentSingleQuestion.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentSingleQuestion.putExtra("answers", aForm.getAnswers(aForm.getCurrentQuestion() - 1));
                intentSingleQuestion.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode) {
                    intentSingleQuestion.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentSingleQuestion.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentSingleQuestion);
                break;
            case 2:
                Intent intentMultipleAnswer = new Intent(getApplicationContext(), MultipleChoiceAnswerActivity.class);
                intentMultipleAnswer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentMultipleAnswer.putExtra("form", aForm);
                intentMultipleAnswer.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentMultipleAnswer.putExtra("answers", aForm.getAnswers(aForm.getCurrentQuestion() - 1));
                intentMultipleAnswer.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode) {
                    intentMultipleAnswer.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentMultipleAnswer.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentMultipleAnswer);
                break;
            case 3:
                Intent intentPhoto = new Intent(getApplicationContext(), PhotoAnswerActivity.class);
                //intentPhoto.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentPhoto.putExtra("form", aForm);
                intentPhoto.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentPhoto.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode){
                    intentPhoto.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentPhoto.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentPhoto);
                break;
            case 4:
                Intent intentLocation = new Intent(getApplicationContext(), MapAnswerActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentLocation.putExtra("form", aForm);
                intentLocation.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentLocation.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode){
                    intentLocation.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentLocation.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentLocation);
                break;
            case 5:
                Intent intentText = new Intent(getApplicationContext(), TextAnswerActivity.class);
                intentText.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentText.putExtra("form", aForm);
                intentText.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentText.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode) {
                    intentText.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentText.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentText);
                break;
            case 6:
                Intent intentBarCode = new Intent(getApplicationContext(), BarCodeAnswerActivity.class);
                intentBarCode.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentBarCode.putExtra("form", aForm);
                intentBarCode.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion() - 1));
                intentBarCode.putExtra("mandatory", aForm.getFormStructure().getQuestions().get(aForm.getCurrentQuestion() - 1).isMandatoryAnswer());
                if(!answeMode) {
                    intentBarCode.putExtra("question", aForm.getQuestionText(aForm.getCurrentQuestion()));
                    intentBarCode.putExtra("mode", SingleAnswerActivity.REVIEW_MODE);
                }
                startActivity(intentBarCode);
                break;
            default:
                throw new RuntimeException(getResources().getString(R.string.question_type_error));
        }
    }

}
