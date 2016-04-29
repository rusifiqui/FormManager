package com.novotec.formmanager.forms;

import android.content.Intent;
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
import android.widget.Toast;

import com.novotec.formmanager.MainActivity;
import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.entities.AnsweredQuestion;
import com.novotec.formmanager.helpers.DbHelper;

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

    public static final int ANSWER_MODE = 1;
    public static final int REVIEW_MODE = 2;

    private AnsweredForm form;

    ListView elements;
    EditText searchCriteria;
    TextView questionTextView;
    String[] values;
    String selectedValue;
    int selectedItemId;
    boolean mandatory = false;
    boolean selectAnswer = false;

    private int mode = 1;
    private int currentQuestion;

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

        if (elements != null) elements.requestFocus();

        selectedValue = "";
        setAdapter(values);

        if(mode == ANSWER_MODE) {
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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Listener para los elementos de la lista
            elements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectAnswer = true;
                    selectedItemId = position;
                    selectedValue = parent.getAdapter().getItem(position).toString();
                    searchCriteria.setText(selectedValue);

                }
            });
        }else{
            searchCriteria.setFocusable(false);
            searchCriteria.setEnabled(false);
            String[] selectedValue = {form.getAnsweredQuestions().get(currentQuestion).getAnswer()};
            setAdapter(selectedValue);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContinueSinglesQuestion);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == ANSWER_MODE){
                    // Se comprueba si la respuesta es obligatoria y si el usuario ha introducido texto
                    if (mandatory & !selectAnswer) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.mandatory_answer, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        int currentQuestion = form.getCurrentQuestion();
                        AnsweredQuestion userAnswer = new AnsweredQuestion();
                        if (!selectAnswer) {
                            userAnswer.setAnswer("");
                            userAnswer.setUserAnswerId(-1);
                        } else {
                            userAnswer.setAnswer(selectedValue);
                            userAnswer.setUserAnswerId(selectedItemId);
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
                form = (AnsweredForm) parameters.get("form");
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
