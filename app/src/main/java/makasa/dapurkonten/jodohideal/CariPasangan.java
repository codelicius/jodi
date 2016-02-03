package makasa.dapurkonten.jodohideal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
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
import makasa.dapurkonten.jodohideal.adapter.RecentChatAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.Partner;
import makasa.dapurkonten.jodohideal.object.RecentChat;

public class CariPasangan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    sessionmanager session;
    Button btnLoadMore;
    int page = 1;
    private SQLiteController db;
    private static String INI = CariPasangan.class.getSimpleName();
    private String urlCaPas = AppConfig.urlAPI;
    private List<Partner> pasangan= new ArrayList<Partner>();
    private ListView listView;
    private ListPartnerAdapter adapter;
    final Context context = this;
    private ProgressDialog pDialog;
    private NetworkImageView drawerPic;
    private TextView drawerName, drawerEmail;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    private List<RecentChat> rcArray = new ArrayList<RecentChat>();
    private RecentChatAdapter rcAdapter;
    ListView recentChatList;
    ImageButton btnTglChat;

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
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);

        HashMap<String, String> profile = db.getUserDetails();
        String foto = profile.get("foto");

        drawerPic = (NetworkImageView) findViewById(R.id.imageView);
        drawerPic.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + foto, mImageLoader);
        drawerName = (TextView)findViewById(R.id.txtDrawerNama);
        drawerName.setText(firstName + " " + lastname);
        drawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        drawerEmail.setText(email);

        listView = (ListView) findViewById(R.id.listKecocokan);
        adapter = new ListPartnerAdapter(this, pasangan);
        listView.setAdapter(adapter);
        listView.addFooterView(btnLoadMore);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pID = ((TextView) view.findViewById(R.id.txtPID)).getText().toString();
                //Toast.makeText(getApplicationContext(), pID, Toast.LENGTH_LONG).show();
                //lihatDetailPasangan(pID, userID);
                Intent i = new Intent(getBaseContext(), OtherProfile.class);
                i.putExtra("pID", pID);
                i.putExtra("userID", userID);
                startActivity(i);
            }
        });

        //right nav
        rcAdapter = new RecentChatAdapter(this, rcArray);
        recentChatList=(ListView)findViewById(R.id.right_nav);
        recentChatList.setAdapter(rcAdapter);
        recentChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String pID = ((TextView) view.findViewById(R.id.txtPartnerID)).getText().toString();
                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("pID", pID);
                startActivity(i);
            }
        });

        btnTglChat = (ImageButton)findViewById(R.id.tglChat);

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

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnTglChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(recentChatList)) {
                    drawer.closeDrawer(recentChatList);
                }
                drawer.openDrawer(recentChatList);
            }
        });
        getRecentPartner(userID);

        listPasangan(page);
    }

    private void listPasangan(int pages){
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(sessionmanager.SES_USER_ID);
        String genderid=user.get(sessionmanager.SES_GENDER);
        final ProgressDialog progressDialog = new ProgressDialog(CariPasangan.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent hm = new Intent(getApplicationContext(),  MainActivity.class);
            hm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            Intent cht = new Intent(getApplicationContext(), AllChat.class);
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

    private void getRecentPartner(final String selfID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(INI, response.toString());

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String status = jsonResponse.getString("recent_chat");

                            if(status.equals("1")) {
                                JSONArray lsCht = jsonResponse.getJSONArray("last_chat");

                                for (int i=0; i<lsCht.length(); i++){
                                    RecentChat recentPpl = new RecentChat();

                                    JSONObject ppl = (JSONObject) lsCht.get(i);
                                    recentPpl.setPartnerID(ppl.getInt("partner_id"));
                                    recentPpl.setFirstName(ppl.getString("first_name"));
                                    recentPpl.setLastName(ppl.getString("last_name"));
                                    recentPpl.setPic("http://103.253.112.121/jodohidealxl/upload/" + ppl.getString("foto"));

                                    rcArray.add(recentPpl);
                                    //Toast.makeText(MainActivity.this, recentPpl.getFirstName(), Toast.LENGTH_LONG).show();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        rcAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CariPasangan.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("jodiRecentChat","");
                params.put("userid",selfID);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
