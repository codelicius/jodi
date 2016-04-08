package makasa.dapurkonten.jodohideal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.object.Partner;

public class Subscribe extends AppCompatActivity {
    private static String INI = Subscribe.class.getSimpleName();
    sessionmanager session;
    final String userIMSI = "user";
    final String passIMSI = "pass";
    final String APIIMSI = "http://103.253.112.121/quiz_api/imsi_api.php";
    RadioGroup paket;
    Button submitPaket;
    int paketID,idx;
    TextView message;
    IMSI getimsi;
    TelephonyManager tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        session = new sessionmanager(getApplicationContext());
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        paket = (RadioGroup) findViewById(R.id.rgCharge);
        submitPaket = (Button)findViewById(R.id.submitPaket);
        message = (TextView)findViewById(R.id.message);
        getimsi = new IMSI(getApplicationContext());
    }
    public void submit(View v){
        paketID=paket.getCheckedRadioButtonId();
        View radioButton = paket.findViewById(paketID);
        idx = paket.indexOfChild(radioButton);
        if(paketID!=-1){
            final String IMSI = tel.getSubscriberId().toString();
            boolean s = getimsi.getProvider(IMSI,idx,session.getUserDetails().get(sessionmanager.SES_USER_ID));
            if(s) {
                Log.d("lanjut","ke smart");
                final String url = APIIMSI + "?imsi=" + IMSI + "&user=" + userIMSI + "&pass=" + passIMSI;
                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                try {
                                    String mdn = response.getString("mdn");

                                    charge(mdn, idx);
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
        }
    }
    public void charge(String mdn,int opsi){
        int max = 99999;
        int min = 00000;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        int rand=i1+i1;
        HashMap<String,String>user = session.getUserDetails();
        final String userid=user.get(sessionmanager.SES_USER_ID);
        String chargePrice = "3000";
        String timeDuration = "1";
        if(opsi==1){
            chargePrice = "5000";
            timeDuration = "7"; //days
        }
        else if(opsi==2){
            chargePrice = "3000";
            timeDuration = "14"; //days
        }
        final String timeDurations=timeDuration;
        final String APICharge = "http://103.253.112.121/quiz_api/charge_api.php";
        final String url = APICharge+"?mdn="+mdn+"&seqid="+rand+"&charge="+chargePrice+"&user="+userIMSI+"&pass="+passIMSI;
        final String mdns = mdn;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("return_code");
                            Log.d(INI,response.toString());
                            if(status.equals("1")) {
                                sendMsg(mdns);
                                addDuration(userid, timeDurations);
                                Toast.makeText(Subscribe.this,"thanks for subscribe",Toast.LENGTH_LONG).show();
                                Intent i =new Intent(getBaseContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                message.setText("gagal berlangganan");
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
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    public void sendMsg(String mdn){
        final String APISendMsg = "http://103.253.112.121/quiz_api/sms_api.php";
        final String msg = "Selamat anda telah terdaftar di layanan aplikasi Jodoh ideal mingguan. Temukan Jodohmu hari ini.";
        final String url = APISendMsg+"?sender=93827&recipient="+mdn+"&message="+msg+"&user="+userIMSI+"&pass="+passIMSI+"&operator=1";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            String status = response.getString("return_code");
                            message.setText(response.toString());
                            Log.d(INI, url);
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
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    public void addDuration(String userid,String duration){
        final String userids = userid;
        final String durations = duration;

        final String API = AppConfig.urlAPI;
        StringRequest postRequest = new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("jodiDuration", "");
                params.put("userid", userids);
                params.put("duration", durations);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}
