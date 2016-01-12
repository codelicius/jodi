package makasa.dapurkonten.jodohideal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makasa.dapurkonten.jodohideal.adapter.ListPartnerAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.Partner;

public class CariPasangan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    sessionmanager session;
    Button btnLoadMore;
    int page = 1;
    private SQLiteController db;
    private static String INI = CariPasangan.class.getSimpleName();
    private String urlCaPas = "http://jodi.licious.id/api/";
    private List<Partner> pasangan= new ArrayList<Partner>();
    private ListView listView;
    private ListPartnerAdapter adapter;
    final Context context = this;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_pasangan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        db = new SQLiteController(getApplicationContext());
        session = new sessionmanager(getApplicationContext());

        btnLoadMore = new Button(this);
        btnLoadMore.setText("load more");
        btnLoadMore.setTextColor(Color.BLACK);
        btnLoadMore.setBackgroundColor(Color.TRANSPARENT);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPasangan(page);
            }
        });

        HashMap<String, String> user = session.getUserDetails();
        final String userID = user.get(sessionmanager.SES_USER_ID);
        final String genderid=user.get(sessionmanager.SES_GENDER);
        listView = (ListView) findViewById(R.id.listKecocokan);
        adapter = new ListPartnerAdapter(this, pasangan);
        listView.setAdapter(adapter);
        listView.addFooterView(btnLoadMore);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pID = ((TextView)view.findViewById(R.id.txtPID)).getText().toString();
                //Toast.makeText(getApplicationContext(), pID, Toast.LENGTH_LONG).show();
                //lihatDetailPasangan(pID, userID);
                Intent i = new Intent(getBaseContext(), OtherProfile.class);
                i.putExtra("pID", pID);
                i.putExtra("userID",userID);
                startActivity(i);
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCariPasangan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setTitle("Cari Pasangan Ideal");
                dialog.setContentView(R.layout.dialog_cari_pasangan);
                dialog.show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listPasangan(page);
    }

    private void listPasangan(int pages){
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(sessionmanager.SES_USER_ID);
        String genderid=user.get(sessionmanager.SES_GENDER);
        final ProgressDialog progressDialog = new ProgressDialog(CariPasangan.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        urlCaPas = urlCaPas + "?userid="+userid+"&genderid="+genderid+"&page="+pages+"&jodiPasangan";
        JsonArrayRequest req = new JsonArrayRequest(urlCaPas,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(INI, response.toString());
                        progressDialog.dismiss();

                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject respon = (JSONObject) response.get(i);

                                Partner partner = new Partner();

                                partner.setpID(respon.getInt("id_pasangan"));
                                partner.setFullName(respon.getString("fname"), respon.getString("lname"));
                                partner.setUrlFoto("http://103.253.112.121/jodohidealxl/upload/"+respon.getString("foto"));
                                partner.setGender(respon.getString("jenis_kelamin"));
                                partner.setSuku(respon.getString("suku"));
                                partner.setAgama(respon.getString("agama"));

                                partner.setKecocokan(respon.getInt("match"));
                                partner.setKetidakcocokan(respon.getInt("not_match"));
                                partner.setUmur(respon.getInt("umur"));
                                pasangan.add(partner);

                            }
                            page++;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(INI, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void loadMoreEvent(View view){
        listPasangan(page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cari_pasangan, menu);
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

        if (id == R.id.nav_home) {
            Intent hm = new Intent(getApplicationContext(),  MainActivity.class);
            startActivity(hm);

        }
        else if (id == R.id.nav_profile) {
            Intent prfl = new Intent(this, Profile.class);
            startActivity(prfl);
        }
        else if (id == R.id.nav_pasangan) {
            Intent psg = new Intent(getApplicationContext(), CariPasangan.class);
            startActivity(psg);
        }
        else if (id == R.id.nav_chat){
            Intent cht = new Intent(getApplicationContext(), Chat.class);
            startActivity(cht);
        }
        else if (id == R.id.nav_logout) {
            db.deleteUsers();
            session.logoutUser();
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
