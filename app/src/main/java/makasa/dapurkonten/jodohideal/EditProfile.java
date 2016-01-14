package makasa.dapurkonten.jodohideal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import makasa.dapurkonten.jodohideal.app.SQLiteController;

public class EditProfile extends AppCompatActivity {
    private SQLiteController db;
    EditText tinggi, deskripsi, tipe_pasangan, kegiatan;
    Spinner txtRace,txtLocation,txtHoroscope,txtJob;
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

        deskripsi = (EditText) findViewById(R.id.deskripsi);
        tipe_pasangan = (EditText) findViewById(R.id.tipe_pasangan);
        kegiatan = (EditText) findViewById(R.id.kegiatan);
        tinggi = (EditText) findViewById(R.id.tinggi);
        txtRace = (Spinner) findViewById(R.id.suku);
        txtLocation = (Spinner) findViewById(R.id.lokasi);
        txtHoroscope = (Spinner) findViewById(R.id.horoskop);
        txtJob = (Spinner) findViewById(R.id.pekerjaan);

        tinggi.setText(height);
        deskripsi.setText(userDetail);
    }

}
