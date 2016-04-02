package makasa.dapurkonten.jodohideal;

/**
 * Created by abay on 17/11/15.
 */
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.RecentChat;

public class sessionmanager {

    // Shared Preferences reference
    SharedPreferences pref,prefRegister;

    // Editor reference for Shared preferences
    Editor editor,register;
    SQLiteController db;

    // Context
    private static String INI=sessionmanager.class.getSimpleName();
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "jodi";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // make variable public to access from outside
    public static final String SES_USER_ID = "user_id";
    public static final String SES_EMAIL = "email";
    public static final String SES_FIRST_NAME = "first_name";
    public static final String SES_LAST_NAME = "last_name";
    public static final String SES_GENDER= "gender";
    public static final String SES_BIRTHDAY = "birthday";

    // Constructor
    public sessionmanager(Context context){
        this._context = context;
        db=new SQLiteController(_context);
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        prefRegister = _context.getSharedPreferences("jodiRegister", PRIVATE_MODE);
        register = prefRegister.edit();
    }

    //Create login session
    public void buatSesiLogin(String name, String email, String firstName, String lastName, String gender, String birthday){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Simpen di file prefensi
        editor.putString(SES_USER_ID, name);
        editor.putString(SES_EMAIL, email);
        editor.putString(SES_FIRST_NAME, firstName);
        editor.putString(SES_LAST_NAME, lastName);
        editor.putString(SES_GENDER, gender);
        editor.putString(SES_BIRTHDAY, birthday);
        // simpan nilai atau perubahan
        editor.commit();
    }
    public void changeValueRegister(final String key,final Integer value){
        register.putInt(key,value).commit();
    }
    public void buatSesiDaftar(){
        register.putInt("register", 1);
        register.putInt("edit_profile", 0);
        register.putInt("upload", 0);
        register.putInt("question", 0);
        register.commit();
        Log.d("buat sesi daftar","ok");
    }
    public void checkDaftar(){
        if(this.isUserRegister() == 1){
            if(this.registerEditProfile() == 0){
                Intent i = new Intent(_context, EditProfile.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }
            else if(this.registerUpload() == 0){
                Intent i = new Intent(_context, imageUpload.class);
                i.putExtra("fromActivity","EditProfile");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }
            else if(this.registerQuestion() == 0){
                Intent i = new Intent(_context, questionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }
        }
        else{
            checkLogin();
        }
        Log.d("cek daftar","ok" +isUserRegister());
    }
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(this.isUserLoggedIn()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(INI, "pertanyaan "+response);

                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                String status = jsonResponse.getString("event");

                                if(status.equals("true")) {
                                    db.deleteQuestions();
                                    JSONArray multiQuestions = jsonResponse.getJSONArray("pertanyaan");
                                    for(int i=0;i<multiQuestions.length();i++){
                                        JSONObject jodiQuestions = multiQuestions.getJSONObject(i);
                                        String jodiQuestionId= jodiQuestions.getString("question_id"),
                                                jodiQuestion= jodiQuestions.getString("question"),
                                                jodiOps1= jodiQuestions.getString("answer_ops1"),
                                                jodiOps2= jodiQuestions.getString("answer_ops2");
                                        db.addQuestion(jodiQuestionId,jodiQuestion,jodiOps1,jodiOps2);
                                    }
                                    // user is not logged in redirect him to Login Activity
                                    Intent i = new Intent(_context, Event.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    // Staring Login Activity
                                    _context.startActivity(i);
                                }
                                else{
                                    // user is not logged in redirect him to Login Activity
                                    Intent i = new Intent(_context, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    // Staring Login Activity
                                    _context.startActivity(i);
                                }

                            } catch (JSONException e) {
                                Intent i = new Intent(_context, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


                                // Staring Login Activity
                                _context.startActivity(i);
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Intent i = new Intent(_context, MainActivity.class);

                            // Closing all the Activities from stack
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Add new Flag to start new Activity
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // Staring Login Activity
                            _context.startActivity(i);
                            Log.d("error","sessionmanager "+error);
                            //Toast.makeText(sessionmanager.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                //proses kirim parameter ke
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("userid",getUserDetails().get("user_id"));
                    params.put("jodiEventCheck","");
                    return params;
                }

            };
            stringRequest.setShouldCache(false);
            RequestQueue requestQueue = Volley.newRequestQueue(_context);
            requestQueue.add(stringRequest);

            return false;
        }
        else{
            Intent i = new Intent(_context, Login.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            return false;
        }
    }
    public void checkLoginMain(){
        // Check login status
        if(!this.isUserLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user id
        user.put(SES_USER_ID, pref.getString(SES_USER_ID, null));
        user.put(SES_EMAIL, pref.getString(SES_EMAIL, null));
        user.put(SES_FIRST_NAME, pref.getString(SES_FIRST_NAME, null));
        user.put(SES_LAST_NAME, pref.getString(SES_LAST_NAME, null));
        user.put(SES_GENDER, pref.getString(SES_GENDER, null));
        user.put(SES_BIRTHDAY, pref.getString(SES_BIRTHDAY, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void registerDone(){
        register.clear().commit();
    }
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, Login.class);
        //clear all activities and get new task
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
    public int isUserRegister(){
        return prefRegister.getInt("register", 0);
    }
    public int registerEditProfile(){
        return prefRegister.getInt("edit_profile", 0);
    }
    public int registerUpload(){
        return prefRegister.getInt("upload", 0);
    }
    public int registerQuestion(){
        return prefRegister.getInt("question", 0);
    }


}
