package com.novotec.formmanager.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Answer;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.entities.Question;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by jvilam on 01/04/2016.
 * Clase para la gestión de la base de datos.
 * @author jvilam
 * @version 1
 * @since 01/04/2016
 */
public class DbHelper extends SQLiteOpenHelper {

    Context c;
    public static final String DATABASE_NAME = "FormsDesign.db";
    public static final int DATABASE_VERSION = 1;

    // Tablas
    // Diseñador de formularios
    public static final String DATABASE_TABLE_FORM_DESIGN = "FORM_DESIGN";
    public static final String DATABASE_TABLE_QUESTION_DESIGN = "QUESTION_DESIGN";
    public static final String DATABASE_TABLE_ANSWER_DESIGN = "ANSWER_DESIGN";
    public static final String DATABASE_TABLE_QUESTION_TYPE = "QUESTION_TYPE";
    public static final String DATABASE_TABLE_ANSWER_TYPE = "ANSWER_TYPE";

    // Formularios completador por usuario
    public static final String DATABASE_USER_FORMS = "USER_FORMS";
    public static final String DATABASE_USER_ANSWERS = "USER_ANSWERS";

    // FIN Tablas

    // Atributos
    // Atributos comunes
    public static final String KEY_CREATE_DATE = "CREATE_DATE";
    public static final String KEY_AUTHOR = "AUTHOR";

    // Atributos de la tabla FORM_DESIGN
    public static final String KEY_ID_FORM_DESIGN = "ID_FORM_DESIGN";
    public static final String KEY_FORM_NAME = "FORM_NAME";
    public static final String KEY_FORM_DESCRIPTION = "FORM_DESCRIPTION";
    public static final String KEY_FORM_TRACKEABLE = "FORM_TRACKEABLE";

    // Atributos de la tabla QUESTION_DESIGN
    public static final String KEY_ID_QUESTION_DESIGN = "ID_QUESTION_DESIGN";
    public static final String KEY_QUESTION = "QUESTION";
    public static final String KEY_MANDATORY_ASNWER = "MANDATORY";

    // Atributos de la tabla ANSWER_DESIGN
    public static final String KEY_ID_ANSWER_DESIGN = "ID_ANSWER_DESIGN";
    public static final String KEY_ANSWER = "ANSWER";

    // Atributos de la tabla QUESTION_TYPE
    public static final String KEY_ID_QUESTION_TYPE = "ID_QUESTION_TYPE";
    public static final String KEY_QUESTION_TYPE = "QUESTION_TYPE";

    // Atributos de la tabla ANSWER_TYPE
    public static final String KEY_ID_ANSWER_TYPE = "ID_ANSWER_TYPE";
    public static final String KEY_ANSWER_TYPE = "ANSWER_TYPE";

    // Atributos de la tabla USER_FORMS
    public static final String KEY_ID_USER_FORM = "ID_USER_FORM";
    public static final String KEY_ID_FORM = "ID_FORM";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_DESCRIPTION = "FORM_DESCRIPTION";

    // Atributos de la tabla USER_ANSWERS
    // Id de la respuesta. PK.
    public static final String KEY_ID_USER_ANSWER = "ID_USER_ANSWER";
    // Id de la pregunta del formulario
    public static final String KEY_ID_USER_QUESTION = "ID_USER_QUESTION";
    // Id de la respuesta seleccionada del formulario. Solo para respuesta única y múltiple
    public static final String KEY_USER_ANSWER_ID = "USER_ANSWER_ID";
    // Texto de la respuesta
    public static final String KEY_USER_ANSWER_TEXT = "USER_ANSWER_TEXT";
    // Latitud
    public static final String KEY_LATITUDE = "LATITUDE";
    // Longitud
    public static final String KEY_LONGITUDE = "LONGITUDE";
    // Dirección de la respuesta
    public static final String KEY_ADDRESS = "ADDRESS";

    //FIN Atributos

    // Sentencia SQL para crear la tabla FormDesign
    private static final String DATABASE_CREATE_FORM_DESIGN = "create table " +
            DATABASE_TABLE_FORM_DESIGN + " (" + KEY_ID_FORM_DESIGN +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_AUTHOR + " text not null, " +
            KEY_FORM_NAME + " text not null, " +
            KEY_FORM_DESCRIPTION + " text not null, " +
            KEY_FORM_TRACKEABLE + " boolean not null);";

    // Sentencia SQL para crear la tabla QuestionDesign
    private static final String DATABASE_CREATE_QUESTION_DESIGN = "create table " +
            DATABASE_TABLE_QUESTION_DESIGN + " (" + KEY_ID_QUESTION_DESIGN +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_AUTHOR + " text not null, " +
            KEY_ID_FORM_DESIGN + " integer not null, " +
            KEY_ID_QUESTION_TYPE + " integer not null, " +
            KEY_QUESTION + " text not null, " +
            KEY_MANDATORY_ASNWER + " boolean not null default false);";

    // Sentencia SQL para crear la tabla AnswerDesign
    private static final String DATABASE_CREATE_ANSWER_DESIGN = "create table " +
            DATABASE_TABLE_ANSWER_DESIGN + " (" + KEY_ID_ANSWER_DESIGN +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_AUTHOR + " text not null, " +
            KEY_ID_QUESTION_DESIGN + " integer not null, " +
            KEY_ID_ANSWER_TYPE + " integer not null, " +
            KEY_ANSWER + " text not null);";

    // Sentencia SQL para crear la tabla QuestionType
    private static final String DATABASE_CREATE_QUESTION_TYPE = "create table " +
            DATABASE_TABLE_QUESTION_TYPE + " (" + KEY_ID_QUESTION_TYPE +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_QUESTION_TYPE + " text not null);";

    // Sentencia SQL para crear la tabla AnswerType
    private static final String DATABASE_CREATE_ANSWER_TYPE = "create table " +
            DATABASE_TABLE_ANSWER_TYPE + " (" + KEY_ID_ANSWER_TYPE +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_ANSWER_TYPE + " text not null);";

    // Sentencia SQL para crear la tabla USER_FORMS
    private static final String DATABASE_CREATE_FORM_USER = "create table " +
            DATABASE_USER_FORMS + " (" + KEY_ID_USER_FORM +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_ID_FORM + " integer not null, " +
            KEY_USER_NAME + " text not null, " +
            KEY_DESCRIPTION + " boolean not null);";

    // Sentencia SQL para crear la tabla USER_ANSERS
    private static final String DATABASE_CREATE_USER_ANSWERS = "create table " +
            DATABASE_USER_ANSWERS + " (" + KEY_ID_USER_ANSWER +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_AUTHOR + " text not null, " +
            KEY_ID_USER_FORM + " integer not null, " +
            KEY_ID_USER_QUESTION + " integer not null, " +
            KEY_USER_ANSWER_ID + " integer, " +
            KEY_USER_ANSWER_TEXT + " text, " +
            KEY_LATITUDE + " text, " +
            KEY_LONGITUDE + " text, " +
            KEY_ADDRESS + " text);";



    public DbHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        c = context;
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tablas de los modelos de formularios
        db.execSQL(DATABASE_CREATE_FORM_DESIGN);
        db.execSQL(DATABASE_CREATE_QUESTION_TYPE);
        db.execSQL(DATABASE_CREATE_ANSWER_TYPE);
        db.execSQL(DATABASE_CREATE_QUESTION_DESIGN);
        db.execSQL(DATABASE_CREATE_ANSWER_DESIGN);

        // Tablas de los formularios completador por el usuario
        db.execSQL(DATABASE_CREATE_FORM_USER);
        db.execSQL(DATABASE_CREATE_USER_ANSWERS);

        populateDataBase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion){
        upgrade(db, oldVersion, newVersion, c);
    }

    public void upgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion, Context c) {

        if(newVersion > oldVersion) {

            // Tablas de los modelos de formularios
            db.execSQL("DROP TABLE " + DATABASE_TABLE_FORM_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_QUESTION_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_ANSWER_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_QUESTION_TYPE);

            // Tablas de los formularios completador por el usuario
            db.execSQL("DROP TABLE " + DATABASE_CREATE_FORM_USER);
            db.execSQL("DROP TABLE " + DATABASE_CREATE_USER_ANSWERS);

            Toast toast = Toast.makeText(c, R.string.dbActualized, Toast.LENGTH_SHORT);
            toast.show();
            // Se crea la base de datos
            onCreate(db);
        }else{
            Toast toast = Toast.makeText(c, R.string.dbNotActualized, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void populateDataBase(SQLiteDatabase db){

        // TODO Modificar la forma en la que se introducen los valores en la tabla
        // Se generan los tipos de preguntas
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_CREATE_DATE, DateHelper.APP_CREATION_DATE);
        newValues.put(KEY_QUESTION_TYPE, "Respuesta única");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
        newValues.remove(KEY_QUESTION_TYPE);
        newValues.put(KEY_QUESTION_TYPE, "Respuesta múltiple");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
        newValues.remove(KEY_QUESTION_TYPE);
        newValues.put(KEY_QUESTION_TYPE, "Realizar fotografía");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
        newValues.remove(KEY_QUESTION_TYPE);
        newValues.put(KEY_QUESTION_TYPE, "Seleccionar ubicación");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
        newValues.remove(KEY_QUESTION_TYPE);
        newValues.put(KEY_QUESTION_TYPE, "Texto libre");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
    }

    /**
     * Método que se encarga de guardar el diseño de un formulario.
     * @param f Formulario a insertar en la base de datos
     * @param context Contexto de la aplicación
     * @return true si se ha insertado correctamente, false en caso contrario
     */
    public static boolean saveFormDesign(Form f, Context context){

        long idForm;
        Vector<Question> questions = f.getQuestions();
        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            // Si el formulario existe, no seguimos adelante.
            if(existsForm(context, f.getName())){
                f.setExistingForm(true);
                return false;
            }
            // Iniciamos una transacción para que solamente se haga "commit" si se han realizado correctamente todas las inserciones.
            db.beginTransaction();
            // Insertamos el nuevo formularioa
            ContentValues formulario = new ContentValues();
            formulario.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
            formulario.put(KEY_AUTHOR, f.getAuthor());
            formulario.put(KEY_FORM_NAME, f.getName());
            formulario.put(KEY_FORM_DESCRIPTION, f.getDescription());
            formulario.put(KEY_FORM_TRACKEABLE, f.isTracked());

            idForm = db.insert(DATABASE_TABLE_FORM_DESIGN, null, formulario);
            if (idForm == -1) return false;

            // Para cada una de las preguntas, obtenemos sus posibles respuestas, y almacenamos
            // toda la información.
            for (int i = 0; i < questions.size(); i++) {
                long idQuestion;
                Question q = questions.get(i);
                Vector<Answer> answers = q.getAnswers();
                ContentValues question = new ContentValues();
                question.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
                question.put(KEY_AUTHOR, q.getAuthor());
                question.put(KEY_ID_FORM_DESIGN, idForm);
                question.put(KEY_ID_QUESTION_TYPE, q.getQuestionType());
                question.put(KEY_QUESTION, q.getQuestion());
                question.put(KEY_MANDATORY_ASNWER, q.isMandatoryAnswer());

                // Insertamos la pregunta
                idQuestion = db.insert(DATABASE_TABLE_QUESTION_DESIGN, null, question);
                if (idQuestion == -1) return false;

                // Si hay respuestas (respuesta múltiple o respuesta sencilla) se recuperan e insertan.
                if (answers.size() > 0) {
                    for (int j = 0; j < answers.size(); j++) {
                        Answer a = answers.get(j);
                        ContentValues answer = new ContentValues();
                        answer.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
                        answer.put(KEY_AUTHOR, a.getAuthor());
                        answer.put(KEY_ID_QUESTION_DESIGN, idQuestion);
                        answer.put(KEY_ID_ANSWER_TYPE, a.getAnswerType());
                        answer.put(KEY_ANSWER, a.getAnswer());

                        if (db.insert(DATABASE_TABLE_ANSWER_DESIGN, null, answer) == -1)
                            return false;
                    }
                }
            }
            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
            db.close();
        }

        return true;
    }

    /**
     * Método que recupera la estructura de datos de un formulario, con las preguntas y las respuestas
     * @param context Contexto de la aplicación.
     * @param formId Id del tipo de formulario a recuperar.
     * @return Estructura del formulario.
     */
    public static Form getFormDesign(Context context,int formId){
        Form f = null;

        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Se recupera el formulario
        Cursor c = db.rawQuery("SELECT * FROM FORM_DESIGN WHERE ID_FORM_DESIGN = " + formId, null);
        if(c != null){
            f = new Form();
            while (c.moveToNext()) {
                for(int i = 0; i < c.getColumnCount(); i++){
                    switch (c.getColumnName(i)){
                        case KEY_ID_FORM_DESIGN:
                            f.setIdForm(c.getInt(i));
                        break;
                        case KEY_CREATE_DATE:
                            f.setCreateDate(DateHelper.getDate(c.getString(i)));
                            break;
                        case KEY_AUTHOR:
                            f.setAuthor(c.getString(i));
                            break;
                        case KEY_FORM_NAME:
                            f.setName(c.getString(i));
                            break;
                        case KEY_FORM_DESCRIPTION:
                            f.setDescription(c.getString(i));
                            break;
                        case KEY_FORM_TRACKEABLE:
                            f.setIsTracked(Boolean.valueOf(c.getString(i)));
                            break;
                    }
                }
            }
        }
        if (f != null) c = db.rawQuery("SELECT * FROM QUESTION_DESIGN WHERE ID_FORM_DESIGN = " + f.getIdForm(), null);

        if(c != null){
            while(c.moveToNext()){
                // Recuperamos una pregunta
                Question q = new Question();
                for(int i = 0; i < c.getColumnCount();i++){
                    switch (c.getColumnName(i)){
                        case KEY_ID_QUESTION_DESIGN:
                            q.setIdQuestion(c.getInt(i));
                        break;
                        case KEY_CREATE_DATE:
                            q.setCreateDate(DateHelper.getDate(c.getString(i)));
                            break;
                        case KEY_AUTHOR:
                            q.setAuthor(c.getString(i));
                            break;
                        case KEY_ID_FORM_DESIGN:
                            q.setIdFormDesign(f.getIdForm());
                            break;
                        case KEY_ID_QUESTION_TYPE:
                            q.setQuestionType(c.getInt(i));
                            break;
                        case KEY_QUESTION:
                            q.setQuestion(c.getString(i));
                            break;
                        case KEY_MANDATORY_ASNWER:
                            q.setMandatoryAnswer(Boolean.valueOf(c.getString(i)));
                            break;
                    }
                }
                // Recuperamos las respuestas de la pregunta, en el caso de existir.
                Cursor cA = db.rawQuery("SELECT * FROM ANSWER_DESIGN WHERE ID_QUESTION_DESIGN = " + q.getIdQuestion(), null);
                if (cA != null) {
                    while(cA.moveToNext()){
                        // Recuperamos las respuestas
                        Answer a = new Answer();
                        for(int j = 0; j < cA.getColumnCount(); j++){
                            switch (cA.getColumnName(j)){
                                case KEY_ID_ANSWER_DESIGN:
                                    a.setIdAnser(cA.getInt(j));
                                    break;
                                case KEY_CREATE_DATE:
                                    a.setCreateDate(DateHelper.getDate(cA.getString(j)));
                                    break;
                                case KEY_AUTHOR:
                                    a.setAuthor(cA.getString(j));
                                    break;
                                case KEY_ID_QUESTION_DESIGN:
                                    a.setIdQuesionDesign(q.getIdQuestion());
                                    break;
                                case KEY_ID_ANSWER_TYPE:
                                    a.setAnswerType(cA.getInt(j));
                                    break;
                                case KEY_ANSWER:
                                    a.setAnswer(cA.getString(j));
                                    break;
                            }
                        }
                        // Añadimos la respuesta a la pregunta
                        q.addAnswer(a);
                    }
                }
                if (cA != null) cA.close();
                // Añadimos la pregunta al formulario
                f.addQuestion(q);
            }
        }
        if (c != null) c.close();
        return f;
    }

    /**
     * Método que comprueba si ya existe un formulario con el mismo nombre en la
     * base de datos del dispositivo
     * @param context Context
     * @param formName Nombre del formulario
     * @return true si existe el formulario, false si no existe
     */
    public static boolean existsForm(Context context, String formName){
        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int numForms = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM FORM_DESIGN WHERE UPPER(FORM_NAME) = \"" + formName.toUpperCase() + "\"", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                numForms = cursor.getInt(0);
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

        }
        return numForms > 0;
    }
}
