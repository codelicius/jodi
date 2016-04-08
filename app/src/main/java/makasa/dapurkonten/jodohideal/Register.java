package makasa.dapurkonten.jodohideal;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;

public class Register extends AppCompatActivity {
    TelephonyManager tel;
    final String userIMSI = "user";
    final String passIMSI = "pass";
    final String APIIMSI = "http://103.253.112.121/quiz_api/imsi_api.php";
    private EditText inputFirstName, inputLastName, inputEmail, inputPhoneNumber, inputFpassword, inputLpassword;
    private CheckBox agreement;
    private RadioGroup rgSex;
    private RadioButton rbGender;
    private Button btnRegister,inputBirthDay;
    private String urlApi = AppConfig.urlAPI;
    private SQLiteController db;
    private static String INI = Register.class.getSimpleName();
    private TextView src,opt;
    sessionmanager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i= getIntent();



        db = new SQLiteController(getApplicationContext());
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        checkNumber();
        session = new sessionmanager(getApplicationContext());
        inputFirstName = (EditText)findViewById(R.id.firstName);
        inputLastName = (EditText)findViewById(R.id.lastName);
        inputEmail = (EditText)findViewById(R.id.email);
        inputPhoneNumber = (EditText)findViewById(R.id.phoneNumber);
        inputFpassword = (EditText)findViewById(R.id.fPassword);
        inputLpassword = (EditText)findViewById(R.id.lPassword);
        agreement = (CheckBox)findViewById(R.id.agreement);
        rgSex = (RadioGroup)findViewById(R.id.sex);
        btnRegister = (Button)findViewById(R.id.register);
        inputBirthDay = (Button)findViewById(R.id.birthday);
        src = (TextView)findViewById(R.id.src);
        opt = (TextView)findViewById(R.id.opt);
        String source = i.getStringExtra("source");
        if(!source.isEmpty()){
            String option = i.getStringExtra("option");
            src.setText(source);
            opt.setText(option);
            Log.d("src", "source " + option);
            Log.d("src","source "+source);

        }
        agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnRegister.setEnabled(true);
                } else {
                    btnRegister.setEnabled(false);
                }
            }
        });


    }


    private void registerUser(final String firstName, final String lastName, final String email,
                              final String phoneNumber, final String firstPassword,final String birthDay, final String gender,final String option,final String source){
        final ProgressDialog progressDialog = new ProgressDialog(Register.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest requestDaftar = new StringRequest(Request.Method.POST, urlApi,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();

                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    //ambil nilai dari JSON respon API
                                    String  jodiStatus = jsonResponse.getString("status");
                                    if(jodiStatus.equals("success")) {
                                        db.deleteQuestions();
                                        String userid = jsonResponse.getString("userid");
                                        JSONArray multiQuestions = jsonResponse.getJSONArray("pertanyaan");
                                        for(int i=0;i<multiQuestions.length();i++){
                                            JSONObject jodiQuestions = multiQuestions.getJSONObject(i);
                                            String jodiQuestionId= jodiQuestions.getString("question_id"),
                                                    jodiQuestion= jodiQuestions.getString("question"),
                                                    jodiOps1= jodiQuestions.getString("answer_ops1"),
                                                    jodiOps2= jodiQuestions.getString("answer_ops2");
                                            db.addQuestion(jodiQuestionId,jodiQuestion,jodiOps1,jodiOps2);
                                        }
                                        //Toast.makeText(Register.this,userid,Toast.LENGTH_LONG).show();
                                        Log.d(INI, "register" + firstName + lastName + email + phoneNumber + firstPassword + birthDay + gender);
                                        session.buatSesiLogin(userid, email, firstName, lastName, gender, birthDay);
                                        Intent i = new Intent(getApplicationContext(),EditProfile.class);
                                        i.putExtra("fromActivity","Register");

                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        String jodiMessage = jsonResponse.getString("message");
                                        Toast.makeText(Register.this,jodiMessage,Toast.LENGTH_LONG).show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Register.this,"Please check your connection",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //parameter, nilai
                params.put("jodiFName", firstName);
                params.put("jodiLName", lastName);
                params.put("jodiEmail", email);
                params.put("jodiPhone", phoneNumber);
                params.put("jodiPassword", firstPassword);
                params.put("jodiDOB", birthDay);
                params.put("jodiGender", gender);
                params.put("option", option);
                params.put("src", source);
                params.put("jodiRegister","");

                return params;
            }
        };

        //add ke queue request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestDaftar);
        requestDaftar.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void daftar(View view){
        //dapatkan value terakhir pada saat button di klik
        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String firstPassword= inputFpassword.getText().toString();
        String lastPassword = inputLpassword.getText().toString();
        String option = opt.getText().toString();
        String source = src.getText().toString();
        String birthDay = inputBirthDay.getText().toString().trim();

        int selectedID = rgSex.getCheckedRadioButtonId();
        rbGender = (RadioButton)findViewById(selectedID);
        String gender = rbGender.getText().toString().trim();
        if(gender.equals("Male"))
            gender = "0";
        else
            gender = "1";
//        String gender = rbGender.getText().toString().trim();

        //cek untuk pastikan user mengisi seluruh form
        if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty()
                && !firstPassword.isEmpty() && !lastPassword.isEmpty() && !birthDay.equals("Date of Birth") ){
            if (firstPassword.equals(lastPassword)){
                registerUser(firstName, lastName, email, phoneNumber, firstPassword, birthDay, gender,option,source);
                session.buatSesiDaftar();
            }
            else {
                AlertDialog infoPass = new AlertDialog.Builder(Register.this).create();
                infoPass.setTitle("Jodoh Ideal");
                infoPass.setMessage("Maaf password yang Anda masukan tidak cocok. Silahkan cek kembali.");
                infoPass.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                infoPass.show();
            }
        }
        else {
            AlertDialog info = new AlertDialog.Builder(Register.this).create();
            info.setTitle("Jodoh Ideal");
            info.setMessage("Silahkan isi seluruh form yang tersedia sebelum Anda melanjutkan pendaftaran");
            info.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            info.show();
        }
    }
    public void dob (View view){
        DialogFragment dob = new datepicker();
        dob.show(getFragmentManager(), "Date Picker");
    }
    public void checkNumber(){
        final String IMSI = tel.getSubscriberId().toString();
        final String url = APIIMSI+"?imsi="+IMSI+"&user="+userIMSI+"&pass="+passIMSI;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            String mdn = response.getString("mdn");
                            if(!mdn.equals("null")) {
                                inputPhoneNumber.setText(mdn);
                                inputPhoneNumber.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Adding request to request queue
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void tos(View view){
        Intent i = new Intent(Register.this, Tos.class);
        startActivity(i);
    }
}
