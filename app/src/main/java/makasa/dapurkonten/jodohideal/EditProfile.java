package makasa.dapurkonten.jodohideal;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.Partner;

public class EditProfile extends AppCompatActivity {
    private SQLiteController db;
    EditText tinggi, deskripsi, tipe_pasangan, kegiatan;
    Spinner spnRace,spnLocation,spnHoroscope,spnJob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new SQLiteController(getApplicationContext());
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
        tinggi = (EditText)findViewById(R.id.tinggi);
        spnRace = (Spinner)findViewById(R.id.suku);
        spnLocation = (Spinner)findViewById(R.id.lokasi);
        spnHoroscope= (Spinner)findViewById(R.id.horoskop);

        tinggi.setText(height);
        deskripsi.setText(userDetail);

    }

    public void spinnerJob() {

        spnJob = (Spinner)findViewById(R.id.pekerjaan);
        String urlAPI = AppConfig.urlAPI + "?jodiSpinner";

        StringRequest req = new StringRequest(urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<String> list = new ArrayList<String>();
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray allJob = jsonResponse.getJSONArray("Pekerjaan");

                            for (int i=0; i<allJob.length(); i++){
                                /**
                                 * peceh data dari array
                                 * lalu add ke list.add("list 1");
                                 * **/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditProfile.this,
                                android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnJob.setAdapter(dataAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

}
