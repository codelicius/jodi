

package makasa.dapurkonten.jodohideal;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.ParseInstallation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makasa.dapurkonten.jodohideal.adapter.ListPartnerAdapter;
import makasa.dapurkonten.jodohideal.adapter.RecentChatAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.AppHelper;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.Partner;
import makasa.dapurkonten.jodohideal.object.RecentChat;
import makasa.dapurkonten.jodohideal.receiver.AlarmReceiver;
import makasa.dapurkonten.jodohideal.receiver.parsePush;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    roundimage round;
    sessionmanager session;
    imageUpload changeImage;
    private SQLiteController db;
    TextView txtNama, txtTinggi, txtLokasi,txtHoroskop, txtPekerjaan, txtAgama,
            txtTentang, txtDrawerNama,txtDrawerEmail, txtShortDescription, lblAbout;
    NetworkImageView imgFoto,imageView;
    private static String INI = MainActivity.class.getSimpleName();
    private ListView listView;
    private List<Partner> pasanganArray = new ArrayList<Partner>();
    private List<RecentChat> rcItem;
    private List<RecentChat> rcArray = new ArrayList<RecentChat>();
    private RecentChatAdapter adapter;
    private ListPartnerAdapter adapterListPasangan;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    ImageButton btnTglChat;
    ListView customListView_chat;
    private String urlAPI = AppConfig.urlAPI;
    parsePush p=new parsePush();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new RecentChatAdapter(this, rcArray);
        customListView_chat=(ListView)findViewById(R.id.right_nav);
        customListView_chat.setAdapter(adapter);
        customListView_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String pID = ((TextView) view.findViewById(R.id.txtPartnerID)).getText().toString();
                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("pID", pID);
                startActivity(i);
            }
        });

        db = new SQLiteController(getApplicationContext());


        session = new sessionmanager(getApplicationContext());
        //session.checkLogin();
        session.checkLoginMain();


        // tarik data user dari sqlite
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
        String foto = profile.get("foto");

        // tarik data dari tabel

        // tarik data user dari session
        HashMap<String, String> user = session.getUserDetails();
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);
        final String userID = user.get(sessionmanager.SES_USER_ID);

        p.insertPush(email,userID);

        //set dalam textview
        txtNama = (TextView)findViewById(R.id.txtProfilNama);
        txtDrawerNama = (TextView)findViewById(R.id.txtDrawerNama);
        txtDrawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        txtLokasi = (TextView)findViewById(R.id.txtProfilLokasi);
        txtTentang = (TextView)findViewById(R.id.txtProfilTentang);
        txtShortDescription = (TextView)findViewById(R.id.txtShortDescription) ;
        lblAbout = (TextView)findViewById(R.id.lblAbout);
        imgFoto = (NetworkImageView)findViewById(R.id.fotoProfile);
        imageView = (NetworkImageView) findViewById(R.id.imageView);

        int time = (int) (System.currentTimeMillis());

        txtNama.setText(firstName + " " + lastname + ", " + age);
        txtDrawerNama.setText(firstName + " " + lastname);
        txtDrawerEmail.setText(email);
        txtLokasi.setText(location);
        txtTentang.setText(userDetail);
        txtShortDescription.setText("Tinggi " + height + " cm, " + horoscope + ", " + job
                + ", " + religion);
        lblAbout.setText("Tentang " + firstName + " " + lastname);
        imgFoto.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + foto +"?time=" + time, mImageLoader);
        imageView.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + foto +"?time=" + time, mImageLoader);



        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        //round = new roundimage(bm);
        //imageView.setImageDrawable(round);

        //list pasangan yang cocok
        adapterListPasangan = new ListPartnerAdapter(this, pasanganArray);
        listView = (ListView) findViewById(R.id.listKecocokan);
        listView.setAdapter(adapterListPasangan);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pID = ((TextView) view.findViewById(R.id.txtPID)).getText().toString();
                //Toast.makeText(getApplicationContext(), pID, Toast.LENGTH_LONG).show();
                //lihatDetailPasangan(pID, userID);

                cekSubscribe(userID, pID);
            }
        });

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnTglChat = (ImageButton)findViewById(R.id.tglChat);

        btnTglChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(customListView_chat)){
                    drawer.closeDrawer(customListView_chat);
                }
                drawer.openDrawer(customListView_chat);
            }
        });

        /**
         * coba nampilin data dari sqlite
         *
         * ArrayList<HashMap<String, String>> arrayListPartner = db.getAllPartner();
         if (arrayListPartner.size() > 0) {

         for (int i = 0; i < arrayListPartner.size(); i++) {

         // ambil masing-masing hasmap dari arrayListPartner
         HashMap<String, String> hashMapRecordPartner = arrayListPartner
         .get(i);

         // JSONObject jsonChildNode = arrayBiodata.getJSONObject(i);
         String partner_id = hashMapRecordPartner.get("partner_id"),
         partner_fname = hashMapRecordPartner.get("partner_fname"),
         partner_lname = hashMapRecordPartner.get("partner_lname"),
         partner_match = hashMapRecordPartner.get("partner_match"),
         partner_notmatch = hashMapRecordPartner.get("partner_notmatch");
         TextView cocokNama = (TextView)findViewById(R.id.txtCocokNama);
         cocokNama.setText(partner_fname + " " + lastname);

         }
         } **/

        //IsiChat();
        //RefreshListChat();
        getRecentPartner(userID);
        listPasangan();
        callUserToBack(60);
    }

    /** public void RefreshListChat() {



     objectArray_right.clear();
     for (int i = 0; i < dataArray_right.size(); i++) {
     Object obj = new Object();
     objectArray_right.add(obj);
     }
     Log.d("object array", "" + objectArray_right.size());
     adapterChat = new ChatItemAdapter(objectArray_right, 1);
     customListView_chat.setAdapter(adapterChat);

     }

     public void IsiChat()
     {

     dataArray_right.clear();


     dataArray_right.add("Option 1");
     dataArray_right.add("Option 2");
     dataArray_right.add("Option 3");
     dataArray_right.add("Option 4");
     dataArray_right.add("Option 5");


     }

     @Override
     public void onBackPressed() {
     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
     if (drawer.isDrawerOpen(GravityCompat.START)) {
     drawer.closeDrawer(GravityCompat.START);
     } else {
     super.onBackPressed();
     }
     }**/
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
    public void imgProfile(View v){
        Intent i = new Intent(getApplicationContext(),imageUpload.class);
        i.putExtra("fromActivity","Main");
        startActivity(i);
    }
    public void morePartner(View v){
        Intent i=new Intent(getApplicationContext(),CariPasangan.class);
        startActivity(i);
    }
    private void listPasangan(){
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(sessionmanager.SES_USER_ID);
        String genderid=user.get(sessionmanager.SES_GENDER);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        urlAPI = urlAPI + "?userid="+userid+"&genderid="+genderid+"&page=1&jodiPasangan";
        Log.d("url",urlAPI + "?userid="+userid+"&genderid="+genderid+"&page=1&jodiPasangan");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,urlAPI,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(INI, response.toString());
                        progressDialog.dismiss();

                        try {
                           JSONArray pasangan = response.getJSONArray("pasangan");
                            for (int i = 0; i < pasangan.length(); i++) {

                                JSONObject respon = (JSONObject) pasangan.get(i);

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
                                pasanganArray.add(partner);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterListPasangan.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d(INI, "Error: " + error.getMessage());
                AlertDialog infoPass = new AlertDialog.Builder(MainActivity.this).create();
                infoPass.setTitle("Alert");
                infoPass.setMessage("Gagal terhubung dengan server, silakan cek koneksi internet anda");
                infoPass.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                listPasangan();
                            }
                        });
                infoPass.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                infoPass.show();
                //Toast.makeText(getApplicationContext(),
                //        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        req.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            p.deletePush();
            ParseInstallation.getCurrentInstallation().deleteInBackground();
            AppConfig logoutfb = new AppConfig();
            logoutfb.fbLogout(getApplicationContext());
            db.deleteUsers();
            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Item Adapter untuk nampilkan daftar orang
     * yang online
     */


    private class ViewHolder {
        TextView text,textcounter;

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
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"silakan cek koneksi anda",Toast.LENGTH_LONG).show();
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
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void callUserToBack (int minutes){

        //set waktu
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minutes);

        //set id proses, bebas
        int rID = 1;
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), rID, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() * 24 * 7, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void cekSubscribe(String ui, final String pi){

        final String userIDs = ui;
        final String pIDs = pi;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String  subscribe_status = jsonResponse.getString("subscribe_status");

                            if(subscribe_status.equals("true")) {
                                Intent i = new Intent(getBaseContext(), OtherProfile.class);
                                i.putExtra("pID", pIDs);
                                i.putExtra("userID", userIDs);
                                startActivity(i);
                            }
                            else{
                                Intent i = new Intent(getApplicationContext(), Subscribe.class);
                                startActivity(i);
                                Log.d(INI, "user tidak berbayar dan diarahkan ke halaman subscribe");
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
                        Toast.makeText(MainActivity.this,"silakan cek koneksi anda",Toast.LENGTH_LONG).show();
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


    }
}
