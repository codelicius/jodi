package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class questionsActivity extends AppCompatActivity{
    private static String INI=questionsActivity.class.getSimpleName();
    private SQLiteController db;
    TextView pertanyaans,idquestion,questionid;
    ImageView imageQue;
    Button goto_next,goto_prev,goto_next_event,goto_prev_event;
    RadioGroup groupQuestion;
    RadioButton question1,question2;
    sessionmanager sessions;
    int idpertanyaan,user_question,jwb_flag;
    long totalQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idpertanyaan = 1;
        setContentView(R.layout.activity_question);
        pertanyaans = (TextView)findViewById(R.id.questiont);
        idquestion = (TextView)findViewById(R.id.idQuestiont);
        questionid= (TextView)findViewById(R.id.questionid);
        goto_next = (Button)findViewById(R.id.goto_next);
        goto_prev = (Button)findViewById(R.id.goto_previous);
        goto_next_event = (Button)findViewById(R.id.goto_next_event);
        goto_prev_event = (Button)findViewById(R.id.goto_previous_event);
        groupQuestion = (RadioGroup)findViewById(R.id.groupQuestion);
        question1 = (RadioButton)findViewById(R.id.question1);
        question2 = (RadioButton)findViewById(R.id.question2);
        sessions = new sessionmanager(getApplicationContext());
        imageQue = (ImageView)findViewById(R.id.imgQuestion);
        goto_prev.setVisibility(View.GONE);
        goto_prev_event.setVisibility(View.GONE);
        db = new SQLiteController(getApplicationContext());
        totalQuestion = db.getTotalQuestion();
        HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
        String id=tenQuestion.get("id"),
                question_id=tenQuestion.get("question_id"),
                question=tenQuestion.get("question"),
                answer_ops1=tenQuestion.get("answer_ops1"),
                answer_ops2=tenQuestion.get("answer_ops2");
        int id_pertanyaan = Integer.valueOf(question_id);
        if(id_pertanyaan >= 29){
            goto_next.setVisibility(View.GONE);
            goto_next_event.setVisibility(View.VISIBLE);
            idquestion.setText("Question " + id + " of "+totalQuestion);
            if (idpertanyaan == totalQuestion) {
                goto_next_event.setText("Finish");
            }
        }
        else {
            goto_next_event.setVisibility(View.GONE);
            goto_next.setVisibility(View.VISIBLE);
            idquestion.setText("Question " + id + " of 10");
        }
        pertanyaans.setText(question);
        questionid.setText(question_id);
        question1.setText(answer_ops1);
        question2.setText(answer_ops2);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


    }
    public void next_event (View view){
        user_question = groupQuestion.getCheckedRadioButtonId();
        View radioButton = groupQuestion.findViewById(user_question);
        int idx = groupQuestion.indexOfChild(radioButton);
        String questionId = questionid.getText().toString().trim();
        int indexJawabPertanyaan = idx +1 ;// di + 1 karna dari database nya di mulai dari angka 1 sedangkan index di mulai dari 0
        if(user_question != -1) {
            if(goto_next_event.getText().toString().equals("Finish")){
                jawabPertanyaanEvent(questionId, indexJawabPertanyaan);
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
            if(idpertanyaan<totalQuestion) {
                RadioButton t=(RadioButton)findViewById(user_question);
                db.updateQuestion(idpertanyaan,user_question);
                jawabPertanyaanEvent(questionId, indexJawabPertanyaan);
                Log.d(INI, "jawab pertanyaan " + questionId+indexJawabPertanyaan);

                idpertanyaan++;
                groupQuestion.clearCheck();
                if (idpertanyaan == totalQuestion) {
                    goto_next_event.setText("Finish");
                }
                HashMap<String, String> tenQuestion = db.getIdQuestion(idpertanyaan);
                String id = tenQuestion.get("id"),
                        question_id = tenQuestion.get("question_id"),
                        question = tenQuestion.get("question"),
                        answer_ops1 = tenQuestion.get("answer_ops1"),
                        answer_ops2 = tenQuestion.get("answer_ops2");
                pertanyaans.setText(question);
                Log.d(INI,"question id "+ question_id);
                idquestion.setText("Question " + id + " of "+totalQuestion);
                //error.setText(t.getText());
                //Toast.makeText(this, "jawab pertanyaan "+jawabPertanyaan, Toast.LENGTH_LONG).show();
                question1.setText(answer_ops1);
                question2.setText(answer_ops2);
                questionid.setText(question_id);

                try {
                    URL thumb_u = new URL("http://103.253.112.121/jodohidealxl/upload/question_" + question_id+".jpg");
                    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                    imageQue.setImageDrawable(thumb_d);
                }
                catch (Exception e) {
                    imageQue.setImageResource(R.drawable.question_default);
                    Log.d("imageview","error "+e);
                }
                goto_prev_event.setVisibility(View.VISIBLE);
            }
        }
        else{
            Toast.makeText(this, "Silahkan pilih salah satu jawaban terlebih dahulu", Toast.LENGTH_LONG).show();
        }
    }
    public void next (View view){
        user_question = groupQuestion.getCheckedRadioButtonId();
        View radioButton = groupQuestion.findViewById(user_question);
        int idx = groupQuestion.indexOfChild(radioButton);
        String questionId = questionid.getText().toString().trim();
        int indexJawabPertanyaan = idx +1 ;// di + 1 karna dari database nya di mulai dari angka 1 sedangkan index di mulai dari 0
        if(user_question != -1) {
            if(goto_next.getText().toString().equals("Finish")){
                jawabPertanyaan(questionId, indexJawabPertanyaan);
                sessions.changeValueRegister("question", 1);
                sessions.registerDone();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
            if(idpertanyaan<10) {
                RadioButton t=(RadioButton)findViewById(user_question);
                db.updateQuestion(idpertanyaan,user_question);
                jawabPertanyaan(questionId, indexJawabPertanyaan);
                Log.d(INI, "jawab pertanyaan " + questionId+indexJawabPertanyaan);

                idpertanyaan++;
                groupQuestion.clearCheck();
                if (idpertanyaan == 10) {
                    goto_next.setText("Finish");
                    getDetail();
                }
                HashMap<String, String> tenQuestion = db.getIdQuestion(idpertanyaan);
                String id = tenQuestion.get("id"),
                        question_id = tenQuestion.get("question_id"),
                        question = tenQuestion.get("question"),
                        answer_ops1 = tenQuestion.get("answer_ops1"),
                        answer_ops2 = tenQuestion.get("answer_ops2");
                pertanyaans.setText(question);
                Log.d(INI,"question id "+ question_id);
                idquestion.setText("Question " + id + " of 10");
                //error.setText(t.getText());
                //Toast.makeText(this, "jawab pertanyaan "+jawabPertanyaan, Toast.LENGTH_LONG).show();
                question1.setText(answer_ops1);
                question2.setText(answer_ops2);
                questionid.setText(question_id);

                try {
                    URL thumb_u = new URL("http://103.253.112.121/jodohidealxl/upload/question_" + question_id+".jpg");
                    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                    imageQue.setImageDrawable(thumb_d);
                }
                catch (Exception e) {
                    imageQue.setImageResource(R.drawable.question_default);
                    Log.d("imageview","error "+e);
                }
                goto_prev.setVisibility(View.VISIBLE);
            }
        }
            else{
                Toast.makeText(this, "Silahkan pilih salah satu jawaban terlebih dahulu", Toast.LENGTH_LONG).show();
            }
    }
    public void previous_event (View view){
        if(idpertanyaan>1) {
            idpertanyaan--;
            if(idpertanyaan==1){
                goto_prev_event.setVisibility(View.INVISIBLE);
            }
            groupQuestion.clearCheck();
            HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
            String id=tenQuestion.get("id"),
                    question_id=tenQuestion.get("question_id"),
                    question=tenQuestion.get("question"),
                    answer_ops1=tenQuestion.get("answer_ops1"),
                    answer_ops2=tenQuestion.get("answer_ops2");
            pertanyaans.setText(question);
            Log.d(INI, "question id " + question_id);
            Log.d(INI,"total "+db.getTotalQuestion());

            idquestion.setText("Question " + id + " of "+totalQuestion);
            question1.setText(answer_ops1);
            question2.setText(answer_ops2);
            questionid.setText(question_id);
            try {
                URL thumb_u = new URL("http://103.253.112.121/jodohidealxl/upload/question_" + question_id+".jpg");
                Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                imageQue.setImageDrawable(thumb_d);
            }
            catch (Exception e) {
                imageQue.setImageResource(R.drawable.question_default);
                Log.d("imageview","error "+e);
            }
            goto_next_event.setText("Next");

        }
    }
    public void previous (View view){
        if(idpertanyaan>1) {
            idpertanyaan--;
            if(idpertanyaan==1){
                goto_prev.setVisibility(View.INVISIBLE);
            }
            groupQuestion.clearCheck();
            HashMap<String,String> tenQuestion = db.getIdQuestion(idpertanyaan);
            String id=tenQuestion.get("id"),
                    question_id=tenQuestion.get("question_id"),
                    question=tenQuestion.get("question"),
                    answer_ops1=tenQuestion.get("answer_ops1"),
                    answer_ops2=tenQuestion.get("answer_ops2");
            pertanyaans.setText(question);
            Log.d(INI, "question id " + question_id);
            Log.d(INI,"total "+db.getTotalQuestion());

            idquestion.setText("Question " + id + " of 10");
            question1.setText(answer_ops1);
            question2.setText(answer_ops2);
            questionid.setText(question_id);
            try {
                URL thumb_u = new URL("http://103.253.112.121/jodohidealxl/upload/question_" + question_id+".jpg");
                Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                imageQue.setImageDrawable(thumb_d);
            }
            catch (Exception e) {
                imageQue.setImageResource(R.drawable.question_default);
                Log.d("imageview","error "+e);
            }
            goto_next.setText("Next");

        }
    }
    public void jawabPertanyaanEvent(final String questionId,Integer answerId){
        final String answerIds = answerId.toString();
        HashMap<String, String> user = sessions.getUserDetails();
        final String userid = user.get(sessionmanager.SES_USER_ID);
        Log.d(INI,"jawabpertanyaan "+userid+" userid "+questionId+" questionId "+answerId);
        StringRequest requestDaftar = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("saveevent","event "+response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  jodiStatus = jsonResponse.getString("status");
                            Log.d(INI,"success update to server");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(questionsActivity.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //parameter, nilai
                params.put("userid",userid);
                params.put("questionid", questionId);
                params.put("answerid", answerIds);
                params.put("jodiSaveQuestionsEvent", "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestDaftar);
    }
    public void jawabPertanyaan(final String questionId,Integer answerId){
        final String answerIds = answerId.toString();
        HashMap<String, String> user = sessions.getUserDetails();
        final String userid = user.get(sessionmanager.SES_USER_ID);
        Log.d(INI,"jawabpertanyaan "+userid+" userid "+questionId+" questionId "+answerId);
        StringRequest requestDaftar = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  jodiStatus = jsonResponse.getString("status");
                            Log.d(INI,"success update to server");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(questionsActivity.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //parameter, nilai
                params.put("userid",userid);
                params.put("questionid", questionId);
                params.put("answerid", answerIds);
                params.put("jodiSaveQuestions", "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestDaftar);
    }
    public void getDetail(){
        HashMap<String, String> user = sessions.getUserDetails();
        final String jodiUserID = user.get(sessionmanager.SES_USER_ID);
        final String jodiFirstName = user.get(sessionmanager.SES_FIRST_NAME);
        final String jodiLastName = user.get(sessionmanager.SES_LAST_NAME);
        final String jodiEmail = user.get(sessionmanager.SES_EMAIL);
        StringRequest requestDaftar = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  jodiStatus = jsonResponse.getString("status");
                            if(jodiStatus.equals("success")) {
                                JSONObject profileUser = jsonResponse.getJSONObject("profile");
                                String profileAge = profileUser.getString("age"),
                                        profileGender = profileUser.getString("gender"),
                                        profileRace = profileUser.getString("race_name"),
                                        profileReligion = profileUser.getString("religion"),
                                        profileHeight = profileUser.getString("height"),
                                        profileLocation = profileUser.getString("loc_name"),
                                        profileHoroscope = profileUser.getString("horoscope_name"),
                                        profileJob = profileUser.getString("job_name"),
                                        profileDetail = profileUser.getString("user_detail"),
                                        profileFoto = profileUser.getString("foto_url"),
                                        profileMerokok = profileUser.getString("smoking"),
                                        profileAlkohol = profileUser.getString("alcohol"),
                                        profileTipePasangan = profileUser.getString("partner_desc"),
                                        profileKegiatan = profileUser.getString("activity"),
                                        profileInterest = profileUser.getString("hobby"),
                                        profileSatNite = profileUser.getString("sat_night");

                                db.addUser(jodiUserID, jodiFirstName, jodiLastName, jodiEmail, profileGender,
                                        profileAge, profileRace, profileReligion, profileHeight, profileLocation,
                                        profileHoroscope, profileJob, profileDetail, profileFoto, profileMerokok, profileAlkohol,
                                        profileTipePasangan, profileKegiatan, profileInterest, profileSatNite);
                                Log.d("add db", jodiUserID+jodiFirstName+jodiLastName+jodiEmail+profileGender+
                                        profileAge+profileRace+profileReligion+profileHeight+profileLocation+
                                        profileHoroscope+profileJob+profileDetail+profileFoto+profileMerokok+profileAlkohol+profileTipePasangan+profileKegiatan+profileInterest+profileSatNite);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(questionsActivity.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //parameter, nilai
                params.put("email",jodiEmail);
                params.put("jodiGetDetailUser", "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestDaftar);
    }
}
