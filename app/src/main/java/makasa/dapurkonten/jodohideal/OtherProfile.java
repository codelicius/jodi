package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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

import makasa.dapurkonten.jodohideal.adapter.ListPencocokanJawaban;
import makasa.dapurkonten.jodohideal.adapter.RecentChatAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.AppHelper;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.PencocokanJawaban;
import makasa.dapurkonten.jodohideal.object.RecentChat;

public class OtherProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;
    sessionmanager session;
    private SQLiteController db;
    private static String INI = OtherProfile.class.getSimpleName();
    private String pID = "";
    private String userID = "";
    private RequestQueue mRequestQueue;
    private ListView listView;
    private List<PencocokanJawaban> PencocokanJawaban = new ArrayList<PencocokanJawaban>();
    private ListPencocokanJawaban adapter;
    final Context context = this;
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
        setContentView(R.layout.activity_other_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        pID = bundle.getString("pID");
        userID = bundle.getString("userID");

        pDialog = new ProgressDialog(OtherProfile.this);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        db = new SQLiteController(getApplicationContext());
        session = new sessionmanager(getApplicationContext());

        HashMap<String, String> profile = db.getUserDetails();
        final String myFoto = profile.get("foto");

        HashMap<String, String> user = session.getUserDetails();
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);

        int time = (int) (System.currentTimeMillis());

        drawerPic = (NetworkImageView) findViewById(R.id.imageView);
        drawerPic.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + myFoto  +"?time=" + time, mImageLoader);
        drawerName = (TextView)findViewById(R.id.txtDrawerNama);
        drawerName.setText(firstName + " " + lastname);
        drawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        drawerEmail.setText(email);

        listView = (ListView) findViewById(R.id.listPencocokanJawaban);
        adapter = new ListPencocokanJawaban(this, PencocokanJawaban);
        listView.setAdapter(adapter);


        //right-nav
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

        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
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

        lihatDetailPasangan();
        getRecentPartner(userID);

    }
    public void kirimPesan(View view){
        Intent i = new Intent(getBaseContext(), Chat.class);
        i.putExtra("pID", pID);
        startActivity(i);
        finish();
    }

    private void lihatDetailPasangan(){
        final String userIDs = userID;
        final String pIDs=pID;
        final TextView fullName =(TextView) findViewById(R.id.viewProfileNama),
                shortdesc =(TextView) findViewById(R.id.viewProfileShortdec),
                tb=(TextView) findViewById(R.id.viewProfileTinggi),
                lokasi=(TextView) findViewById(R.id.viewProfileLokasi),
                agama=(TextView) findViewById(R.id.viewProfileAgama),
                horoskop=(TextView) findViewById(R.id.viewProfileHoroskop),
                pekerjaan=(TextView) findViewById(R.id.viewProfilePekerjaan),
                merokok=(TextView) findViewById(R.id.viewProfileMerokok),
                alkohol=(TextView) findViewById(R.id.viewProfileAlkohol);
        final NetworkImageView foto = (NetworkImageView)findViewById(R.id.thumbnailFoto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  subscribe_status = jsonResponse.getString("subscribe_status");


                            if(subscribe_status.equals("true")) {
                                HashMap<String, String> profile = db.getUserDetails();
                                String myFoto = profile.get("foto");
                                Log.d("Other profile", "Proses berhasil masuk ke tahap parsing");
                                Log.d("tes","res "+response);
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

                                JSONArray semuaPertanyaan = pd.getJSONArray("pertanyaan");

                                for (int i=0; i<semuaPertanyaan.length(); i++){
                                    Log.d("Other profile", "Proses berhasil masuk ke tahap parsing array pertanyaan");
                                    JSONObject sp = (JSONObject) semuaPertanyaan.get(i);
                                    PencocokanJawaban pj = new PencocokanJawaban();

                                    pj.setPertanyaan(sp.getString("pertanyaan"));
                                    pj.setJawabanKamu(sp.getString("jawaban_kamu"));
                                    pj.setJawabanDia(sp.getString("jawaban_dia"));
                                    pj.setNamaDia(apiFullName);
                                    pj.setFotoDia(apiFoto);
                                    pj.setFotoKamu(myFoto);
                                    PencocokanJawaban.add(pj);
                                    Log.d("Other profile", sp.getString("pertanyaan"));
                                }

                                fullName.setText(apiFullName);
                                shortdesc.setText(apiUmur + " tahun, " + apiGender + ", " + apiSuku);
                                tb.setText(apiTinggi);
                                lokasi.setText(apiLokasi);
                                agama.setText(apiAgama);
                                horoskop.setText(apiHoroskop);
                                pekerjaan.setText(apiPekerjaan);
                                merokok.setText(apiRokok);
                                alkohol.setText(apiAlkohol);
                                foto.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + apiFoto, mImageLoader);
                                Log.d("Other profile", "Selesai set text");
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
                        adapter.notifyDataSetChanged();
                        AppHelper.listViewDynamicHeight(listView);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtherProfile.this,"silakan cek koneksi anda",Toast.LENGTH_LONG).show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent hm = new Intent(getApplicationContext(), MainActivity.class);
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
            AppConfig logoutfb = new AppConfig();
            logoutfb.fbLogout(getApplicationContext());
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
                        Toast.makeText(OtherProfile.this, "silakan cek koneksi anda", Toast.LENGTH_LONG).show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


}
