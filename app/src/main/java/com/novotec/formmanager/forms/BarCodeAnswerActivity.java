package com.novotec.formmanager.forms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.entities.AnsweredQuestion;
import com.novotec.formmanager.helpers.DbHelper;

import java.io.IOException;

/**
 * Actividad para realizar la lectura de un código de barras desde la aplicación.
 * @author jvilam
 * @version 1
 * @since 21/04/2016
 */
public class BarCodeAnswerActivity extends AppCompatActivity {

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;

    public static final int ANSWER_MODE = 1;

    AnsweredForm form;
    boolean mandatory = false;

    TextView question;
    EditText answer;
    private int mode = 1;
    private int currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = (TextView) findViewById(R.id.textViewBarCode);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeDetector = new BarcodeDetector.Builder(this).build();
        answer = (EditText) findViewById(R.id.editTextContent);

        getParameters();

        if(mode == ANSWER_MODE) {

            cameraSource = new CameraSource
                    .Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(800, 600)
                    .setAutoFocusEnabled(true)
                    .build();

            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                    if (barcodes.size() != 0) {
                        answer.post(new Runnable() {    // Use the post method of the EditText
                            public void run() {
                                answer.setText(barcodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }
            });

            if (cameraView != null) {
                cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        try {
                            // Permiso para la cámara
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(BarCodeAnswerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                            }

                            cameraSource.start(cameraView.getHolder());
                        } catch (IOException ie) {
                            Log.e("CAMERA SOURCE", ie.getMessage());
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        cameraSource.stop();
                    }
                });
            }
        }else{
            // Se crea el listener para que no se pueda pegar en el campo
            answer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            answer.setText(form.getAnsweredQuestions().get(currentQuestion).getAnswer());
            answer.setFocusable(false); answer.setClickable(false);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mode == ANSWER_MODE){
                        // Se comprueba si la respuesta es obligatoria y si el usuario ha introducido texto
                        if (mandatory && answer.getText().length() < 1) {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.mandatory_answer, Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            AnsweredQuestion userAnswer = new AnsweredQuestion();
                            userAnswer.setAnswer(answer.getText().toString());
                            userAnswer.setIdUserQuestion(form.getCurrentQuestion());
                            form.addAnswer(userAnswer);
                            form.setCurrentQuestion(currentQuestion + 1);
                            if (form.getCurrentQuestion() <= form.getQuestionCount()) {
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
                        if(form.getCurrentQuestion() < form.getQuestionCount() - 1){
                            form.setCurrentQuestion(currentQuestion + 1);
                            generateFormStep(form.getQuestionType(form.getCurrentQuestion()), form, false);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.form_end, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });
        }
    }

    /**
     * Función que establece los parámetros de la actividad.
     * Los parámetros se reciben al crear la vista a través de un Intent
     */
    private void getParameters(){
        Bundle parameters = getIntent().getExtras();
        if(parameters != null){
            if(parameters.containsKey("form")){
                form = (AnsweredForm) parameters.get("form");
            }else{
                throw new RuntimeException(getResources().getString(R.string.no_form_found));
            }
            if(parameters.containsKey("question")) {
                question.setText(parameters.getString("question"));
            }else{
                question.setText(R.string.question_error);
            }
            if(parameters.containsKey("mandatory"))
                mandatory = parameters.getBoolean("mandatory");
            if(parameters.containsKey("mode"))
                mode = parameters.getInt("mode");
            currentQuestion = form.getCurrentQuestion();
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
