package com.novotec.formmanager.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.novotec.formmanager.R;
import com.novotec.formmanager.entities.Answer;
import com.novotec.formmanager.entities.AnsweredForm;
import com.novotec.formmanager.entities.AnsweredQuestion;
import com.novotec.formmanager.entities.Form;
import com.novotec.formmanager.entities.Question;

import java.text.SimpleDateFormat;
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
    public static final String DATABASE_TABLE_USER_FORMS = "USER_FORMS";
    public static final String DATABASE_TABLE_USER_ANSWERS = "USER_ANSWERS";
    public static final String DATABASE_TABLE_USER_MULTIPLE_CHOICE_ANSWERS = "USER_MULTIPLE_ANSWERS";

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

    // Atributos de la tabla USER_MULTIPLE_ANSWERS
    public static final String KEY_ID_USER_MULTIPLE_ANSWERS = "ID_USER_MULTIPLE_ANSWERS";
    public static final String KEY_ANSWER_ID = "ANSWER_ID";
    public static final String KEY_MULTIPLE_ANSWER_TEXT = "MULTIPLE_ANSWER_TEXT";

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
            DATABASE_TABLE_USER_FORMS + " (" + KEY_ID_USER_FORM +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_ID_FORM + " integer not null, " +
            KEY_USER_NAME + " text not null, " +
            KEY_DESCRIPTION + " text not null);";

    // Sentencia SQL para crear la tabla USER_ANSERS
    private static final String DATABASE_CREATE_USER_ANSWERS = "create table " +
            DATABASE_TABLE_USER_ANSWERS + " (" + KEY_ID_USER_ANSWER +
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

    // Sentencia SQL para crear la tabla USER_MULTIPLE_ANSWERS.
    // Se utiliza para almacenar las respuestas de las preguntas con respuesta múltiple.
    private static final String DATABASE_CREATE_USER_MULTIPLE_ANSWERS = "create table " +
            DATABASE_TABLE_USER_MULTIPLE_CHOICE_ANSWERS + " (" + KEY_ID_USER_MULTIPLE_ANSWERS +
            " integer primary key autoincrement, " +
            KEY_CREATE_DATE + " date, " +
            KEY_AUTHOR + " text not null, " +
            KEY_ID_USER_ANSWER + " integer not null, " +
            KEY_ANSWER_ID + " integer not null, " +
            KEY_MULTIPLE_ANSWER_TEXT + " text not null);";

    public DbHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        c = context;
    }

    /**
     * Método empleado para crear la base de datos cuando esta no existe en el sistema.
     * @param db    La base de datos.
     */
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
        db.execSQL(DATABASE_CREATE_USER_MULTIPLE_ANSWERS);

        populateDataBase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion){
        upgrade(db, oldVersion, newVersion, c);
    }

    /**
     * Método llamado cuando se realiza una actualización de la versión de la base de datos
     * @param db            La base de datos
     * @param oldVersion    Id de la versión anterior
     * @param newVersion    Id de la nueva versión
     * @param c             Contexto de la aplicación
     */
    public void upgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion, Context c) {

        if(newVersion > oldVersion) {

            // Tablas de los modelos de formularios
            db.execSQL("DROP TABLE " + DATABASE_TABLE_FORM_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_QUESTION_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_ANSWER_DESIGN);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_QUESTION_TYPE);

            // Tablas de los formularios completador por el usuario
            db.execSQL("DROP TABLE " + DATABASE_TABLE_USER_FORMS);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_USER_ANSWERS);
            db.execSQL("DROP TABLE " + DATABASE_TABLE_USER_MULTIPLE_CHOICE_ANSWERS);

            Toast toast = Toast.makeText(c, R.string.dbActualized, Toast.LENGTH_SHORT);
            toast.show();
            // Se crea la base de datos
            onCreate(db);
        }else{
            Toast toast = Toast.makeText(c, R.string.dbNotActualized, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /**
     * Método que se encarga de insertar elementos en la Base de Datos
     * @param db    La base de datos
     */
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
        newValues.remove(KEY_QUESTION_TYPE);
        newValues.put(KEY_QUESTION_TYPE, "Código de barras");
        db.insert(DATABASE_TABLE_QUESTION_TYPE, null, newValues);
    }

    /**
     * Método que se encarga de guardar el diseño de un formulario.
     * @param f         Formulario a insertar en la base de datos
     * @param context   Contexto de la aplicación
     * @return          true si se ha insertado correctamente
     *                  false en caso contrario
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
     * @param context   Contexto de la aplicación.
     * @param formId    Id del tipo de formulario a recuperar.
     * @return          Estructura del formulario.
     */
    public static Form getFormDesign(Context context, int formId){
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
                            if(c.getString(i).equals("1")){
                                q.setMandatoryAnswer(true);
                            }else{
                                q.setMandatoryAnswer(false);
                            }
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
     * Método que se encarga de eliminar un tipo de formulario del sistema. Valida si el usuario ha creado
     * algún formulario del tipo a eliminar.
     * @param context       Contexto de la aplicación
     * @param formId        Id del formulario a eliminar
     * @param forceDelete   Parámetro que indica que se elimine el formulario con todos sus formularios asociados
     * @return              0 si se elimina correctamente
     *                      -1 si se produce algún error
     *                      -2 si no se eliminar por existir información del usuario correspondiente al formulario seleccionado.
     */

    public static int deleteFormDesign(Context context,int formId, boolean forceDelete){

        boolean existingUserForms = false;

        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USER_FORMS WHERE ID_FORM = " + formId, null);
        if(cursor!=null){
            // Existen formularios
            if(cursor.getCount() > 0){
                if(forceDelete){
                    existingUserForms = true;
                }else {
                    return -2;
                }
            }
        }
        if (cursor != null) cursor.close();

        db.beginTransaction();
        try{
            if(existingUserForms){
                if(deleteUserFormsType(db, formId) == -1) return -1;
            }

            // Eliminamos el formulario. Si no se elimina ninguno, se devuelve error
            if(db.delete(DATABASE_TABLE_FORM_DESIGN, KEY_ID_FORM_DESIGN + " = " + formId, null) < 1) return -1;

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }finally{
            db.endTransaction();
            db.close();
        }
        return 0;
    }

    /**
     * Método que elimina todos los formularios de un tipo concreto generados por el usuario
     * @param db        La base de datos
     * @param formType  Tipo de formulario
     * @return          0 si se elimina correctamente
     *                  -1 si se produce algún error
     */
    public static int deleteUserFormsType(SQLiteDatabase db, int formType){

        /*DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();*/
        Cursor cursor = db.rawQuery("SELECT ID_USER_FORM FROM USER_FORMS WHERE ID_FORM = " + formType, null);

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (deleteUserForm(db, cursor.getInt(0)) == -1) return -1;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }finally{
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    /**
     * Método que elimina un formulario cumplimentado por el usuario
     * @param db            La base da datos
     * @param formId        Id del formulario
     * @return              0 Si se elimina correctamente
     *                      -1 Si se produce algún error
     */
    public static int deleteUserForm(SQLiteDatabase db, int formId){

        try{
            // Eliminamos el formulario. Si no se elimina ninguno, se devuelve error
            if(db.delete(DATABASE_TABLE_USER_FORMS, KEY_ID_USER_FORM + " = " + formId, null) < 1) return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Método para eliminar un formulario cumplimentado por el usuario desde el menú.
     * @param context   Contexto de la aplicación
     * @param formId    Id del formulario a eliminar
     * @return          0 si se elimina correctamente
     *                  -1 si se produce algún error
     */
    public static int deleteUserFormFromMenu(Context context,int formId){

        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            // Eliminamos el formulario. Si no se elimina ninguno, se devuelve error
            if(db.delete(DATABASE_TABLE_USER_FORMS, KEY_ID_USER_FORM + " = " + formId, null) < 1) return -1;

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }finally{
            db.close();
        }
        return 0;
    }

    /**
     * Método que comprueba si ya existe un formulario con el mismo nombre en la
     * base de datos del dispositivo
     * @param context   Context
     * @param formName  Nombre del formulario
     * @return          true si existe el formulario
     *                  false si no existe
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

    /**
     * * Método que se encarga de guardar las respuestas del usuario
     * @param f         Respuestas del usuario
     * @param context   Contexto de la aplicación
     * @return          true si la inserción se realiza correctamente
     *                  false si ocurre algún error
     */
    public static boolean saveForm(AnsweredForm f, Context context) {

        long idForm;
        Vector<AnsweredQuestion> answers = f.getAnsweredQuestions();
        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            // Iniciamos una transacción para que solamente se haga "commit" si se han realizado correctamente todas las inserciones.
            db.beginTransaction();
            // Insertamos el nuevo formularioa
            ContentValues formulario = new ContentValues();
            formulario.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
            formulario.put(KEY_USER_NAME, f.getUserName());
            formulario.put(KEY_ID_FORM, f.getFormStructure().getIdForm());
            formulario.put(KEY_DESCRIPTION, f.getDescription());

            idForm = db.insert(DATABASE_TABLE_USER_FORMS, null, formulario);
            if (idForm == -1) return false;

            for(int i = 0; i < answers.size(); i++){
                long idAnswer;
                AnsweredQuestion a = answers.get(i);
                ContentValues answserValues = new ContentValues();
                answserValues.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
                answserValues.put(KEY_AUTHOR, f.getUserName());
                answserValues.put(KEY_ID_USER_FORM, idForm);
                answserValues.put(KEY_ID_USER_QUESTION, i);
                answserValues.put(KEY_USER_ANSWER_ID, a.getUserAnswerId());
                answserValues.put(KEY_USER_ANSWER_TEXT, a.getAnswer());
                answserValues.put(KEY_LATITUDE, a.getLat());
                answserValues.put(KEY_LONGITUDE, a.getLon());
                answserValues.put(KEY_ADDRESS, a.getAddress());

                idAnswer = db.insert(DATABASE_TABLE_USER_ANSWERS, null, answserValues);
                if (idAnswer == -1) return false;

                // Si es una pregunta de respuesta múltiple, hay que añadir las opciones seleccionadas.
                if(a.getAnswers().size() > 0 && a.getAnswersIds().size() > 0 && a.getAnswersIds().size() == a.getAnswers().size()){
                    for(int j = 0; j < a.getAnswers().size(); j++){
                        ContentValues multipleAnswers = new ContentValues();
                        multipleAnswers.put(KEY_CREATE_DATE, dateFormatter.format(f.getCreateDate().getTime()));
                        multipleAnswers.put(KEY_AUTHOR, f.getUserName());
                        multipleAnswers.put(KEY_ID_USER_ANSWER, idAnswer);
                        multipleAnswers.put(KEY_ANSWER_ID, a.getAnswersIds().get(j));
                        multipleAnswers.put(KEY_MULTIPLE_ANSWER_TEXT, a.getAnswers().get(j));
                        if(db.insert(DATABASE_TABLE_USER_MULTIPLE_CHOICE_ANSWERS, null, multipleAnswers) == -1) return false;
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

    public static AnsweredForm getForm(Context context, int formId){
        AnsweredForm userForm = null;

        DbHelper dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Se recupera el formulario
        Cursor c = db.rawQuery("SELECT * FROM USER_FORMS WHERE ID_USER_FORM = " + formId, null);
        if(c != null){
            userForm = new AnsweredForm();
            while (c.moveToNext()) {
                for(int i = 0; i < c.getColumnCount(); i++){
                    switch (c.getColumnName(i)){
                        case KEY_ID_USER_FORM:
                            userForm.setId(c.getInt(i));
                            break;
                        case KEY_CREATE_DATE:
                            userForm.setCreateDate(DateHelper.getDate(c.getString(i)));
                            break;
                        case KEY_ID_FORM:
                            userForm.setIdForm(c.getInt(i));
                            break;
                        case KEY_USER_NAME:
                            userForm.setUserName(c.getString(i));
                            break;
                        case KEY_DESCRIPTION:
                            userForm.setDescription(c.getString(i));
                            break;
                    }
                }
            }
        }

        if (userForm != null) c = db.rawQuery("SELECT * FROM USER_ANSWERS WHERE ID_USER_FORM = " + userForm.getId(), null);

        if(c != null) {
            while (c.moveToNext()) {
                // Recuperamos una pregunta
                AnsweredQuestion q = new AnsweredQuestion();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    switch (c.getColumnName(i)) {
                        case KEY_ID_USER_ANSWER:
                            q.setId(c.getInt(i));
                            break;
                        case KEY_CREATE_DATE:
                            q.setCreateDate(DateHelper.getDate(c.getString(i)));
                            break;
                        case KEY_AUTHOR:
                            q.setAuthor(c.getString(i));
                            break;
                        case KEY_ID_USER_FORM:
                            q.setIdUserForm(userForm.getId());
                            break;
                        case KEY_ID_USER_QUESTION:
                            q.setIdUserQuestion(c.getInt(i));
                            break;
                        case KEY_USER_ANSWER_ID:
                            q.setUserAnswerId(c.getInt(i));
                            break;
                        case KEY_USER_ANSWER_TEXT:
                            q.setAnswer(c.getString(i));
                            break;
                        case KEY_LATITUDE:
                            q.setLat(c.getString(i));
                            break;
                        case KEY_LONGITUDE:
                            q.setLon(c.getString(i));
                            break;
                        case KEY_ADDRESS:
                            q.setAddress(c.getString(i));
                            break;
                    }
                }
                // Se recuperan las respuestas múltiples
                // Recuperamos las respuestas de la pregunta, en el caso de existir.
                Cursor cA = db.rawQuery("SELECT * FROM USER_MULTIPLE_ANSWERS WHERE ID_USER_ANSWER = " + q.getId(), null);
                Vector<Integer> answerIds = null;
                Vector<String> answersText = null;
                if (cA != null) {
                    // Recuperamos las respuestas
                    answerIds = new Vector<>();
                    answersText = new Vector<>();
                    while (cA.moveToNext()) {
                        for (int j = 0; j < cA.getColumnCount(); j++) {
                            switch (cA.getColumnName(j)) {
                                case KEY_ANSWER_ID:
                                    answerIds.add(cA.getInt(j));
                                    break;
                                case KEY_MULTIPLE_ANSWER_TEXT:
                                    answersText.add(cA.getString(j));
                                    break;
                            }
                        }
                    }
                    // Se añaden las respuestas
                    q.setAnswersIds(answerIds);
                    q.setAnswers(answersText);
                }
                // Se añaden las respuestas
                q.setAnswersIds(answerIds);
                q.setAnswers(answersText);
                userForm.addAnswer(q);
                if (cA != null) cA.close();
            }
        }
        // Añadimos la estructura del formulario
        if (userForm != null) userForm.setFormStructure(getFormDesign(context, userForm.getIdForm()));

        if (c != null) c.close();

        db.close();

        return userForm;
    }
}
