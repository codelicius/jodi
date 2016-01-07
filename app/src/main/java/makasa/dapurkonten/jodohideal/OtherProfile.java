package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;

public class OtherProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;
    sessionmanager session;
    private SQLiteController db;
    private static String INI = CariPasangan.class.getSimpleName();
    private String pID = "";
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        pID = bundle.getString("pID");
        userID = bundle.getString("userID");
        pDialog = new ProgressDialog(OtherProfile.this);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Please Wait...");
        pDialog.show();



        db = new SQLiteController(getApplicationContext());
        session = new sessionmanager(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lihatDetailPasangan();
    }

    private void lihatDetailPasangan(){
        final String userIDs = userID;
        final String pIDs=pID;
        final TextView fullName =(TextView) findViewById(R.id.viewProfileNama),
                umur =(TextView) findViewById(R.id.viewProfileUmur),
                gender=(TextView) findViewById(R.id.viewProfileGender),
                suku=(TextView) findViewById(R.id.viewProfileSuku),
                tb=(TextView) findViewById(R.id.viewProfileTinggi),
                lokasi=(TextView) findViewById(R.id.viewProfileLokasi),
                agama=(TextView) findViewById(R.id.viewProfileAgama),
                horoskop=(TextView) findViewById(R.id.viewProfileHoroskop),
                pekerjaan=(TextView) findViewById(R.id.viewProfilePekerjaan),
                merokok=(TextView) findViewById(R.id.viewProfileMerokok),
                alkohol=(TextView) findViewById(R.id.viewProfileAlkohol);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  subscribe_status = jsonResponse.getString("subscribe_status");


                            if(subscribe_status.equals("true")) {
                                JSONObject pd = jsonResponse.getJSONObject("partner_detail");
                                String apiFullName = pd.getString("fname") + " " +pd.getString("lname");
                                String apiId = pd.getString("id_pasangan");
                                String apiUmur = pd.getString("umur");
                                String apiFoto = pd.getString("foto");
                                String apiGender = pd.getString("jenis_kelamin");
                                String apiSuku = pd.getString("suku");
                                String apiAgama = pd.getString("agama");
                                String apiTinggi = pd.getString("tinggi");
                                String apiLokasi = pd.getString("lokasi");
                                String apiHoroskop = pd.getString("horoskop");
                                String apiPekerjaan = pd.getString("pekerjaan");
                                String apiRokok = pd.getString("rokok");
                                String apiAlkohol = pd.getString("alkohol");
                                String apiDetail = pd.getString("detail");
                                String apiMatch = pd.getString("match");
                                String apiNotMatch = pd.getString("not_match");
                                fullName.setText(apiFullName);
                                umur.setText(apiUmur);
                                suku.setText(apiSuku);
                                tb.setText(apiTinggi);
                                lokasi.setText(apiLokasi);
                                gender.setText(apiGender);
                                agama.setText(apiAgama);
                                horoskop.setText(apiHoroskop);
                                pekerjaan.setText(apiPekerjaan);
                                merokok.setText(apiRokok);
                                alkohol.setText(apiAlkohol);
                            }
                            else{
                                Intent i = new Intent(getApplicationContext(), Subscribe.class);
                                startActivity(i);
                                Log.d(INI, "user tidak berbayar dan diarahkan ke halaman subscribe");
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtherProfile.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("jodiPartnerDetail", "");
                params.put("userid",userIDs);
                params.put("partner_userid",pIDs);
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        hidepDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.other_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
