package makasa.dapurkonten.jodohideal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import makasa.dapurkonten.jodohideal.adapter.RecentChatAdapter;
import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.ChatHistory;
import makasa.dapurkonten.jodohideal.object.Partner;
import makasa.dapurkonten.jodohideal.object.RecentChat;
import makasa.dapurkonten.jodohideal.object.circleImage;
import makasa.dapurkonten.jodohideal.receiver.parsePush;

public class Chat extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    sessionmanager session;
    private SQLiteController db;
    Button btnSendMessage;
    EditText txtComposeMessage;
    TextView chtSelf, chtOther;
    private boolean side = false;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    String urlAPI,partnerID, userChat="user";
    private static String INI = Chat.class.getSimpleName();
    TextView txtDrawerNama, txtDrawerEmail,chatName;
    NetworkImageView imageView;
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    private List<RecentChat> rcArray = new ArrayList<RecentChat>();
    private RecentChatAdapter adapter;
    private circleImage ci;
    ListView recentChatList;
    ImageButton btnTglChat;
    parsePush p = new parsePush();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle=getIntent().getExtras();
        partnerID = bundle.getString("pID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        session = new sessionmanager(getApplicationContext());
        //session.checkLogin();
        session.checkLoginMain();
        db = new SQLiteController(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);
        final String userID = user.get(sessionmanager.SES_USER_ID);

        HashMap<String,String> profile=db.getUserDetails();
        String foto=profile.get("foto");

        //drawer
        txtDrawerNama = (TextView)findViewById(R.id.txtDrawerNama);
        txtDrawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        ci = (circleImage) findViewById(R.id.chatImg);
        chatName = (TextView) findViewById(R.id.chatName);
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

        listView = (ListView) findViewById(R.id.chtView);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_msg);
        listView.setAdapter(chatArrayAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        txtComposeMessage = (EditText)findViewById(R.id.txtComposeMessage);

        btnSendMessage = (Button)findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            CharSequence message;

            @Override
            public void onClick(View v) {

                message = txtComposeMessage.getText();
                if (message.length() > 0) {
                    HashMap<String, String> user = session.getUserDetails();
                    final String userID = user.get(sessionmanager.SES_USER_ID);
                    final String pID = partnerID;
                    final String pesan = txtComposeMessage.getText().toString();
                    sendChatMessage(userID, pID, pesan);
                    p.sendPush(userID, pID);

                }

            }
        });
        getRecentPartner(userID);
        //set partner id here
        getChatHistory(partnerID);

        Log.d("userchat", "user " + userChat);
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
        getMenuInflater().inflate(R.menu.chat, menu);
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

    private void sendChatMessage(final String userID,final String partnerID,final String message) {
        chatArrayAdapter.add(new ChatMessage("right", txtComposeMessage.getText().toString()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(INI, response.toString());

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String  jodiStatus = jsonResponse.getString("history");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Chat.this,"Koneksi Anda bermasalah, silahkan cek koneksi Anda",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",userID);
                params.put("partner_id",partnerID);
                params.put("txt_message",message);
                params.put("jodiWriteChat","");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        txtComposeMessage.setText("");
    }

    private void getChatHistory(final String partnerID){
        HashMap<String, String> user = session.getUserDetails();
        final String userID = user.get(sessionmanager.SES_USER_ID);

        final ProgressDialog progressDialog = new ProgressDialog(Chat.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(INI, response.toString());

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            String  jodiStatus = jsonResponse.getString("history");
                            JSONObject detail = jsonResponse.getJSONObject("detail");
                            userChat = detail.getString("first_name") + " "+ detail.getString("last_name");
                            getSupportActionBar().setTitle(userChat);
                            Log.d("userchat ","users "+userChat+"from db"+detail.getString("first_name") + " "+ detail.getString("last_name"));
                            if(jodiStatus.equals("1")) {
                                JSONArray allChat = jsonResponse.getJSONArray("message");

                                for (int i=0; i<allChat.length(); i++){
                                    JSONObject cht = (JSONObject) allChat.get(i);
                                    ChatHistory chats = new ChatHistory();
                                    int senderID = cht.getInt("sent_id");
                                    String message = cht.getString("message");
                                    if(senderID == Integer.parseInt(userID))
                                        chatArrayAdapter.add(new ChatMessage("right", message));
                                    else
                                        chatArrayAdapter.add(new ChatMessage("left", message));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog infoPass = new AlertDialog.Builder(Chat.this).create();
                        infoPass.setTitle("Alert");
                        infoPass.setMessage("Gagal terhubung dengan server, silakan cek koneksi internet anda");
                        infoPass.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getChatHistory(partnerID);
                                    }
                                });
                        infoPass.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        infoPass.show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",userID);
                params.put("partner_id",partnerID);
                params.put("jodiHistoryChat","");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}