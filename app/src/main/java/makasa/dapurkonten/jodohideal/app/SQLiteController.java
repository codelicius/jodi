package makasa.dapurkonten.jodohideal.app;

/**
 * Created by pr1de on 26/11/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteController extends SQLiteOpenHelper {

    private static final String INI = SQLiteController.class.getSimpleName();

    // Seluruh variabel statis di definisikan
    // versi db yang dipake
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "jodi";

    // tabel untuk login dan value akan disimpan pada tabel ini
    private static final String TABLE_USER = "user";
    private static final String TABLE_QUESTION = "question";
    private static final String TABLE_PARTNER = "partner";

    // nama kolom
    private static final String COL_ID = "id";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_GENDER = "gender";
    private static final String COL_AGE = "age";
    private static final String COL_RACE = "race";
    private static final String COL_RELIGION = "religion";
    private static final String COL_HEIGHT = "height";
    private static final String COL_LOCATION = "location";
    private static final String COL_HOROSCOPE = "horoscope";
    private static final String COL_JOB = "job";
    private static final String COL_USER_DETAIL = "user_detail";
    private static final String COL_FOTO = "foto";
    private static final String COL_MEROKOK = "merokok";
    private static final String COL_ALKOHOL = "alkohol";
    private static final String COL_TIPE_PASANGAN = "tipe_pasangan";
    private static final String COL_KEGIATAN = "kegiatan";
    private static final String COL_INTEREST = "interest";
    private static final String COL_SATNITE = "satnine";



    public SQLiteController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COL_ID + " INTEGER PRIMARY KEY,"
                + COL_FIRST_NAME + " TEXT,"
                + COL_LAST_NAME + " TEXT,"
                + COL_EMAIL + " TEXT UNIQUE,"
                + COL_GENDER + " TEXT,"
                + COL_AGE + " TEXT,"
                + COL_RACE + " TEXT,"
                + COL_RELIGION + " TEXT,"
                + COL_HEIGHT+ " TEXT,"
                + COL_LOCATION + " TEXT,"
                + COL_HOROSCOPE + " TEXT,"
                + COL_JOB + " TEXT,"
                + COL_USER_DETAIL + " TEXT,"
                + COL_FOTO + " TEXT,"
                + COL_MEROKOK + " TEXT,"
                + COL_ALKOHOL + " TEXT,"
                + COL_TIPE_PASANGAN + " TEXT,"
                + COL_KEGIATAN + " TEXT,"
                + COL_INTEREST + " TEXT,"
                + COL_SATNITE + " TEXT"+
                ")";

        String CREATE_PARTNER_TABLE = "CREATE TABLE " + TABLE_PARTNER + "(" +
                "id INTEGER PRIMARY KEY, " +
                "partner_fname TEXT, " +
                "partner_lname TEXT, " +
                "partner_match INTEGER, " +
                "partner_notmatch INTEGER, " +
                "partner_image TEXT, " +
                "partner_age INTEGER, " +
                "partner_gender TEXT, " +
                "partner_race TEXT, " +
                "partner_religion TEXT" +
                ")";

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTION +"(" +
                "id INTEGER PRIMARY KEY," +
                "question_id INTEGER," +
                "question TEXT," +
                "answer_ops1 TEXT," +
                "answer_ops2 TEXT," +
                "user_answer INTEGER)";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_PARTNER_TABLE);

        Log.d(INI, "tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table yg lama
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TAble IF EXISTS " + TABLE_PARTNER);
        db.execSQL("DROP TAble IF EXISTS " + TABLE_QUESTION);

        // buat kembali tabel
        onCreate(db);
    }

    /**
     * Simpen detail user ke tabel
     * cek pada sqlite editor ok
     * */

    public void addUser(String id, String fname, String lname, String email,
                        String gender, String age, String race, String religion, String height,
                        String location, String horoscope, String job, String userDetail, String foto,String merokok, String alkohol,
                        String tipePasangan,String kegiatan,String interest, String satNite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // key, value
        values.put(COL_ID, id);
        values.put(COL_FIRST_NAME, fname);
        values.put(COL_LAST_NAME, lname);
        values.put(COL_EMAIL, email);
        values.put(COL_GENDER, gender);
        values.put(COL_AGE, age);
        values.put(COL_RACE, race);
        values.put(COL_RELIGION, religion);
        values.put(COL_HEIGHT, height);
        values.put(COL_LOCATION, location);
        values.put(COL_HOROSCOPE, horoscope);
        values.put(COL_JOB, job);
        values.put(COL_USER_DETAIL, userDetail);
        values.put(COL_FOTO, foto);
        values.put(COL_MEROKOK, merokok);
        values.put(COL_ALKOHOL, alkohol);
        values.put(COL_TIPE_PASANGAN, tipePasangan);
        values.put(COL_KEGIATAN, kegiatan);
        values.put(COL_INTEREST, interest);
        values.put(COL_SATNITE, satNite);




        // Insert
        long uid = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(INI, "New user inserted into sqlite: " + uid);
    }


    public void addPartner(String id, String fname, String lname, int match, int not_match, String imageUrl,
                           int age, String gender, String race, String religion){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // key, value
        values.put("id", id);
        values.put("partner_fname", fname);
        values.put("partner_lname", lname);
        values.put("partner_match", match);
        values.put("partner_notmatch", not_match);
        values.put("partner_image", imageUrl);
        values.put("partner_age", age);
        values.put("partner_gender", gender);
        values.put("partner_race", race);
        values.put("partner_religion", religion);

        // Insert
        long uid = db.insert(TABLE_PARTNER, null, values);
        db.close();

        Log.d(INI, "New partner inserted into sqlite: " + uid);
    }

    //ini table question
    public void addQuestion(String question_id,String question,String answer_ops1,String answer_ops2){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues questions=new ContentValues();
        questions.put("question_id",question_id);
        questions.put("question", question);
        questions.put("answer_ops1", answer_ops1);
        questions.put("answer_ops2", answer_ops2);
        long uidQuestion=db.insert(TABLE_QUESTION,null,questions);
        db.close();
        Log.d(INI,"new question inserted into sqlite: "+uidQuestion);
    }

    /**
     * fix problem get user data
     * */

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // arahkan kursor si sqlite ke baris pertama table
        // ibarat fetch array mysql
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("first_name", cursor.getString(1));
            user.put("last_name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("gender", cursor.getString(4));
            user.put("age", cursor.getString(5));
            user.put("race", cursor.getString(6));
            user.put("religion", cursor.getString(7));
            user.put("height", cursor.getString(8));
            user.put("location", cursor.getString(9));
            user.put("horoscope", cursor.getString(10));
            user.put("job", cursor.getString(11));
            user.put("user_detail", cursor.getString(12));
            user.put("foto",cursor.getString(13));
            user.put("merokok", cursor.getString(14));
            user.put("alkohol", cursor.getString(15));
            user.put("tipe_pasangan", cursor.getString(16));
            user.put("kegiatan", cursor.getString(17));
            user.put("interest", cursor.getString(18));
            user.put("satnite", cursor.getString(19));


        }
        cursor.close();
        db.close();
        // return user
        Log.d(INI, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getPartnerDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_PARTNER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // arahkan kursor si sqlite ke baris pertama table
        // ibarat fetch array mysql
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("partner_id", cursor.getString(0));
            user.put("partner_fname", cursor.getString(1));
            user.put("partner_lname", cursor.getString(2));
            user.put("partner_match", cursor.getString(3));
            user.put("partner_notmatch", cursor.getString(4));
            user.put("partner_image", cursor.getString(5));
            user.put("partner_age", cursor.getString(6));
            user.put("partner_gender", cursor.getString(7));
            user.put("partner_race", cursor.getString(8));
            user.put("partner_religion", cursor.getString(9));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(INI, "Fetching partner from Sqlite: " + user.toString());

        return user;
    }
    public long getTotalQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, TABLE_QUESTION);
        db.close();
        return cnt;
    }
    public HashMap<String,String> getIdQuestion(int id){
        HashMap<String, String> questions = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION +" where id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            questions.put("id", cursor.getString(0));
            questions.put("question_id", cursor.getString(1));
            questions.put("question", cursor.getString(2));
            questions.put("answer_ops1", cursor.getString(3));
            questions.put("answer_ops2", cursor.getString(4));
        }
        cursor.close();
        db.close();
        return questions;
    }
    public void updateQuestion(int id,int user_answer){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues c=new ContentValues();
        c.put("user_answer", user_answer);
        db.update(TABLE_QUESTION, c, "id=" + id, null);
        db.close();
    }


    /**
     * buat ulang db
     * apus semua table and dan buat ulang table lagi
     * */

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(INI, "Deleted all user info from sqlite");
    }
    public void deleteQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_QUESTION, null, null);
        db.close();

        Log.d(INI, "Deleted all questions info from sqlite");
    }

    public ArrayList<HashMap<String, String>> getAllPartner() {
        SQLiteDatabase database = this.getWritableDatabase();

        // deklarasikan sebuah arraylist yang bisa menampung hashmap
        ArrayList<HashMap<String, String>> arrayListPartner = new ArrayList<HashMap<String, String>>();

        Cursor cursor = database.rawQuery("SELECT * FROM partner", null);

        // kursor langsung diarkan ke posisi paling awal data pada tabel partner
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMapPartner = new HashMap<String, String>();

                hashMapPartner.put("partner_id", cursor.getString(0));
                hashMapPartner.put("partner_fname", cursor.getString(1));
                hashMapPartner.put("partner_lname", cursor.getString(2));
                hashMapPartner.put("partner_match", cursor.getString(3));
                hashMapPartner.put("partner_notmatch", cursor.getString(4));
                hashMapPartner.put("partner_image", cursor.getString(5));
                hashMapPartner.put("partner_age", cursor.getString(6));
                hashMapPartner.put("partner_gender", cursor.getString(7));
                hashMapPartner.put("partner_race", cursor.getString(8));
                hashMapPartner.put("partner_religion", cursor.getString(9));

                arrayListPartner.add(hashMapPartner);

            } while (cursor.moveToNext());
        }

        return arrayListPartner;
    }

    public void updateUser(String id, String race, String religion, String height,
                           String location, String horoscope, String job, String userDetail, String merokok,
                           String alkohol, String tipe_pasangan, String kegiatan, String interest, String satnite){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cp = new ContentValues();
        cp.put(COL_ID, id);
        cp.put(COL_RACE, race);
        cp.put(COL_RELIGION, religion);
        cp.put(COL_HEIGHT, height);
        cp.put(COL_LOCATION, location);
        cp.put(COL_HOROSCOPE, horoscope);
        cp.put(COL_JOB, job);
        cp.put(COL_USER_DETAIL, userDetail);
        cp.put(COL_MEROKOK, merokok);
        cp.put(COL_ALKOHOL, alkohol);
        cp.put(COL_TIPE_PASANGAN, tipe_pasangan);
        cp.put(COL_KEGIATAN, kegiatan);
        cp.put(COL_INTEREST, interest);
        cp.put(COL_SATNITE, satnite);

        db.update(TABLE_USER,cp,"id="+id,null);
        db.close();
    }

    public void fotoUpdate(String uid){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cp = new ContentValues();
        cp.put(COL_FOTO, uid+".jpg");

        db.update(TABLE_USER, cp, "id="+uid, null);
        db.close();

    }

}