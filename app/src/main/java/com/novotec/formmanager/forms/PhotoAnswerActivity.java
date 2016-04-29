package com.novotec.formmanager.forms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.entities.AnsweredQuestion;
import com.novotec.formmanager.helpers.DbHelper;
import com.novotec.formmanager.helpers.PhotoHelper;

import java.io.File;

/**
 * Actividad que sirve para representar una pregunta que requiere de la realización de una
 * fotografía.
 *
 * @author jvilam
 * @version 1
 * @since 05/04/2016
 */
public class PhotoAnswerActivity extends AppCompatActivity {

    public static final int ANSWER_MODE = 1;
    public static final int REVIEW_MODE = 2;



    private AnsweredForm form;
    private ImageView mImageView;
    private TextView questionTextView;
    private boolean mandatory = false;

    private Uri mImageUri;
    private static final int ACTIVITY_SELECT_IMAGE = 1020,
            ACTIVITY_SELECT_FROM_CAMERA = 1040;
    private PhotoHelper photoHelper;

    private String mCurrentPhotoPath;

    private int mode = 1;
    private int currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.photoActivity);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        photoHelper = new PhotoHelper(this);
        questionTextView = (TextView) findViewById(R.id.textViewQuestionPhotoQuestion);
        mImageView = (ImageView) findViewById(R.id.imageViewPhoto);

        getParameters();

        // Botones flotantes
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContinuePhoto);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == ANSWER_MODE) {
                    // Se comprueba si la respuesta es obligatoria y si el usuario ha introducido texto
                    if (mandatory && mCurrentPhotoPath == null) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.mandatory_answer, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        int currentQuestion = form.getCurrentQuestion();
                        AnsweredQuestion userAnswer = new AnsweredQuestion();
                        if (mCurrentPhotoPath != null) {
                            userAnswer.setAnswer(mCurrentPhotoPath);
                        } else {
                            userAnswer.setAnswer("");
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

        // Botón para realizar la fotografía
        FloatingActionButton fabPhoto = (FloatingActionButton) findViewById(R.id.fabPhoto);
        assert fabPhoto != null;
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se llama a la cámara para realizar una fotografía
                Intent intent = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                File photo = null;
                try {
                    // place where to store camera taken picture
                    photo = PhotoHelper.createTemporaryFile("picture", ".jpg", PhotoAnswerActivity.this);
                    photo.delete();
                } catch (Exception e) {
                    Log.v(getClass().getSimpleName(),
                            "Can't create file to take picture!");
                }
                mImageUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, ACTIVITY_SELECT_FROM_CAMERA);
            }
        });
        // Botones flotantes

        if(mode != ANSWER_MODE){
            mCurrentPhotoPath = form.getAnsweredQuestions().get(currentQuestion).getAnswer();
            layout.removeView(fabPhoto);
            File f = new File(mCurrentPhotoPath);
            Uri u = Uri.fromFile(f);

            getImage(u);
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
            if(parameters.containsKey("mode"))
                mode = parameters.getInt("mode");
            currentQuestion = form.getCurrentQuestion();
        }
    }


    /**
     * Método que devuelve la fotografía a la aplicación. Este método es llamado al volver de la
     * actividad de la cámara.
     *
     * @param requestCode el código de llamada. Debe coincidir con el código de invocación a la actividad de la cámara.
     * @param resultCode  el código de retorno de la actividad.
     * @param data        los datos.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SELECT_FROM_CAMERA
                && resultCode == RESULT_OK) {
            getImage(mImageUri);
        }
    }

    public void getImage(Uri uri) {
        mCurrentPhotoPath = uri.getPath();
        Bitmap bounds = photoHelper.getImage(uri);
        if (bounds != null) {
            setImage(bounds);
        } else {
            //showErrorToast();
        }
    }


    private void setImage(Bitmap bitmap){
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null)
            outState.putString("Uri", mImageUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("Uri")) {
            mImageUri = Uri.parse(savedInstanceState.getString("Uri"));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
                intentPhoto.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
