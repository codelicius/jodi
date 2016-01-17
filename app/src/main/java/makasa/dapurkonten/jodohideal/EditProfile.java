package makasa.dapurkonten.jodohideal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
        ArrayList<String> items=getSpinner("pekerjaan");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
                R.layout.spinner_item,R.id.txt,items);
        txtJob.setAdapter(adapter);
        txtJob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                Log.d("di pilih", Integer.valueOf(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });


        ArrayList<String> lokasi=getSpinner("lokasi");
        ArrayAdapter<String> adapters=new ArrayAdapter<String>(this,
                R.layout.spinner_item,R.id.txt,lokasi);
        txtLocation.setAdapter(adapters);

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
        if(!getTPasangan.isEmpty() && !getKegiatan.isEmpty() && !getHalSuka.isEmpty() && !getMalming.isEmpty()){
            editUser(userid,getTinggi,gettxtRace,gettxtAgama,gettxtRokok,gettxtAlkohol,gettxtHoroscope,gettxtJob,getDeskripsi,getTPasangan,getKegiatan,gettxtLocation,getHalSuka,getMalming);
        }
        else{
            AlertDialog infoPass = new AlertDialog.Builder(EditProfile.this).create();
            infoPass.setTitle("Alert");
            infoPass.setMessage("Silahkan isi seluruh form yang tersedia sebelum Anda melanjutkan");
            infoPass.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            infoPass.show();
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
                                sessions.changeValueRegister("edit_profile",1);
                                Intent i = new Intent(getApplicationContext(), imageUpload.class);
                                i.putExtra("fromActivity","EditProfile");
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
    private ArrayList<String> getSpinner(String fileName){
        final JSONArray jsonArray=null;
        final ArrayList<String> pekerjaan=new ArrayList<String>();
        final ArrayList<String> lokasi=new ArrayList<String>();
        final String API = AppConfig.urlAPI;
        final String url = API+"?jodiSpinner";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("respon",response.toString());
                        try{
                            JSONArray pk = response.getJSONArray("Pekerjaan");
                            JSONArray lk = response.getJSONArray("Lokasi");
                            for(int i=0; i<pk.length(); i++){
                                pekerjaan.add(pk.getString(i));
                            }
                            for(int i=0; i<lk.length(); i++){
                                lokasi.add(lk.getString(i));
                            }
                            Log.d("array pekerjaan",pekerjaan.toString());
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // the response is already constructed as a JSONObject!

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Adding request to request queue
        Volley.newRequestQueue(this).add(jsonRequest);
        if(fileName.equals("pekerjaan")){
            return pekerjaan;
        }
        else{
            return lokasi;
        }
    }
}
