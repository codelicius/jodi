package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;

public class EditProfile extends AppCompatActivity {
    private SQLiteController db;
    sessionmanager sessions;
    EditText tinggi, deskripsi, tipe_pasangan, kegiatan,halsuka,malming;
    Spinner txtRace,txtLocation,txtHoroscope,txtJob,txtReligion,txtRokok,txtAlkohol;
    private static String INI = EditProfile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new SQLiteController(getApplicationContext());
        sessions = new sessionmanager(getApplicationContext());
        HashMap<String, String> profile = db.getUserDetails();
        String id = profile.get("id");
        String gender = profile.get("gender");
        String age = profile.get("age");
        String race = profile.get("race");
        String religion = profile.get("religion");
        String height = profile.get("height");
        String location = profile.get("location");
        String horoscope = profile.get("horoscope");
        String job = profile.get("job");
        String userDetail = profile.get("user_detail");

        deskripsi= (EditText)findViewById(R.id.deskripsi);
        tipe_pasangan = (EditText)findViewById(R.id.tipe_pasangan);
        kegiatan = (EditText)findViewById(R.id.kegiatan);
        halsuka = (EditText)findViewById(R.id.halsuka);
        malming = (EditText)findViewById(R.id.malming);
        tinggi = (EditText)findViewById(R.id.tinggi);
        txtReligion = (Spinner)findViewById(R.id.agama);
        txtRokok = (Spinner)findViewById(R.id.merokok);
        txtAlkohol = (Spinner)findViewById(R.id.alkohol);
        txtRace = (Spinner)findViewById(R.id.suku);
        txtLocation = (Spinner)findViewById(R.id.lokasi);
        txtHoroscope = (Spinner)findViewById(R.id.horoskop);
        txtJob = (Spinner)findViewById(R.id.pekerjaan);

        tinggi.setText(height);
        deskripsi.setText(userDetail);
    }
    public void save(View v){
        HashMap<String, String> user = sessions.getUserDetails();
        final String userid = user.get(sessionmanager.SES_USER_ID);
        String getTinggi = tinggi.getText().toString().trim(),
                gettxtRace= String.valueOf(txtRace.getSelectedItemPosition()),
                gettxtAgama= String.valueOf(txtReligion.getSelectedItemPosition()),
                gettxtRokok= String.valueOf(txtRokok.getSelectedItemPosition()),
                gettxtAlkohol= String.valueOf(txtAlkohol.getSelectedItemPosition()),
                gettxtHoroscope = String.valueOf(txtHoroscope.getSelectedItemPosition()),
                gettxtJob = String.valueOf(txtJob.getSelectedItemPosition()),
                getDeskripsi = deskripsi.getText().toString().trim(),
                getTPasangan= tipe_pasangan.getText().toString().trim(),
                getKegiatan= kegiatan.getText().toString().trim(),
                gettxtLocation = String.valueOf(txtLocation.getSelectedItemPosition()),
                getHalSuka= halsuka.getText().toString().trim(),
                getMalming= malming.getText().toString().trim();
        if(!getDeskripsi.isEmpty() && !getTPasangan.isEmpty() && !getKegiatan.isEmpty() && !getHalSuka.isEmpty() && !getMalming.isEmpty()){
            editUser(userid,getTinggi,gettxtRace,gettxtAgama,gettxtRokok,gettxtAlkohol,gettxtHoroscope,gettxtJob,getDeskripsi,getTPasangan,getKegiatan,gettxtLocation,getHalSuka,getMalming);
        }
       // Toast.makeText(EditProfile.this,gettxtLocation,Toast.LENGTH_LONG).show();

    }
    private void editUser(final String userid,final String height, final String suku, final String agama,
                              final String rokok, final String alkohol,final String horoskop, final String pekerjaan,final String descdiri,final String tipe_psg,final String kegiatan,final String lokasi,final String suka,final String malming) {
        final ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest requestDaftar = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String jodiStatus = jsonResponse.getString("status");

                            if (jodiStatus.equals("success")) {
                                Intent i = new Intent(getApplicationContext(), imageUpload.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(EditProfile.this, jodiStatus, Toast.LENGTH_LONG).show();
                            }
                            Log.d(INI,"edit profile"+userid+height+ suku+ agama+rokok+ alkohol+horoskop+ pekerjaan+descdiri+tipe_psg+kegiatan+lokasi+suka+malming );



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(EditProfile.this, "Please check your connection", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //parameter, nilai
                params.put("userid",userid);
                params.put("height", height);
                params.put("suku", suku);
                params.put("agama", agama);
                params.put("rokok", rokok);
                params.put("alkohol", alkohol);
                params.put("horoskop", horoskop);
                params.put("pekerjaan", pekerjaan);
                params.put("descdiri", descdiri);
                params.put("tipe_psg", tipe_psg);
                params.put("kegiatan", kegiatan);
                params.put("lokasi", lokasi);
                params.put("suka", suka);
                params.put("malming", malming);
                params.put("jodiEditProfile", "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestDaftar);

    }
}
