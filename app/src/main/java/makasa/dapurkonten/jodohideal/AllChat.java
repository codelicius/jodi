package makasa.dapurkonten.jodohideal;

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

import makasa.dapurkonten.jodohideal.adapter.AllChatAdapter;
import makasa.dapurkonten.jodohideal.adapter.RecentChatAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.AllChats;
import makasa.dapurkonten.jodohideal.object.RecentChat;

public class AllChat extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private sessionmanager session;
    private SQLiteController db;
    TextView txtDrawerNama, txtDrawerEmail;
    NetworkImageView imageView;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    private List<RecentChat> rcArray = new ArrayList<RecentChat>();
    private RecentChatAdapter adapter;
    private List<AllChats> acArray = new ArrayList<AllChats>();
    private AllChatAdapter adapterAllChat;
    ListView recentChatList, allChatList;
    ImageButton btnTglChat;
    private static String INI = AllChat.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new sessionmanager(getApplicationContext());
        session.checkLoginMain();
        db = new SQLiteController(getApplicationContext());

        // tarik data user dari session
        HashMap<String, String> user = session.getUserDetails();
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);
        final String userID = user.get(sessionmanager.SES_USER_ID);

        db = new SQLiteController(getApplicationContext());
        HashMap<String,String> profile=db.getUserDetails();
        String age=profile.get("age"),
                gender=profile.get("gender"),
                fname=profile.get("first_name"),
                lname=profile.get("last_name"),
                height=profile.get("height"),
                location=profile.get("location"),
                horoscope=profile.get("horoscope"),
                religion=profile.get("religion"),
                foto=profile.get("foto");

        //drawer
        txtDrawerNama = (TextView)findViewById(R.id.txtDrawerNama);
        txtDrawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        imageView = (NetworkImageView)findViewById(R.id.imageView);
        txtDrawerNama.setText(firstName + " " + lastname);
        txtDrawerEmail.setText(email);
        imageView.setImageUrl("http://103.253.112.121/jodohidealxl/upload/" + foto, mImageLoader);

        adapter = new RecentChatAdapter(this, rcArray);
        recentChatList=(ListView)findViewById(R.id.right_nav);
        recentChatList.setAdapter(adapter);
        recentChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String pID = ((TextView) view.findViewById(R.id.txtPartnerID)).getText().toString();
                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("pID", pID);
                startActivity(i);
            }
        });

        adapterAllChat = new AllChatAdapter(this, acArray);
        allChatList=(ListView)findViewById(R.id.listAllChat);
        allChatList.setAdapter(adapterAllChat);
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

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //tgl right nav
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent hm = new Intent(getApplicationContext(), MainActivity.class);
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
                                    AllChats ac = new AllChats();

                                    JSONObject ppl = (JSONObject) lsCht.get(i);
                                    recentPpl.setPartnerID(ppl.getInt("partner_id"));
                                    ac.setChatID(ppl.getInt("partner_id"));
                                    recentPpl.setFirstName(ppl.getString("first_name"));
                                    recentPpl.setLastName(ppl.getString("last_name"));
                                    ac.setPartnerName(ppl.getString("first_name") + " " + ppl.getString("last_name"));
                                    recentPpl.setPic("http://103.253.112.121/jodohidealxl/upload/" + ppl.getString("foto"));
                                    ac.setPartnerPic("http://103.253.112.121/jodohidealxl/upload/" + ppl.getString("foto"));

                                    rcArray.add(recentPpl);
                                    acArray.add(ac);
                                    //Toast.makeText(MainActivity.this, recentPpl.getFirstName(), Toast.LENGTH_LONG).show();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        adapterAllChat.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AllChat.this, error.toString(), Toast.LENGTH_LONG).show();
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