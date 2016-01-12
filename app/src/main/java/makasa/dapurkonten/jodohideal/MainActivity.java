package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import makasa.dapurkonten.jodohideal.adapter.ListPartnerAdapter;
import makasa.dapurkonten.jodohideal.app.AppController;
import makasa.dapurkonten.jodohideal.app.SQLiteController;
import makasa.dapurkonten.jodohideal.object.Partner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    roundimage round;
    ImageView imageView;
    sessionmanager session;
    private SQLiteController db;
    TextView txtNama, txtTinggi, txtLokasi,txtHoroskop, txtPekerjaan, txtAgama,
            txtTentang, txtDrawerNama,txtDrawerEmail, txtShortDescription, lblAbout;
    private static String INI = MainActivity.class.getSimpleName();
    private ListView listView;
    private List<Partner> pasanganArray = new ArrayList<Partner>();
    private ListPartnerAdapter adapterListPasangan;
    ImageButton btnTglChat;
    ArrayList<String> dataArray_right=new ArrayList<String>();
    ArrayList<Object> objectArray_right=new ArrayList<Object>();
    ChatItemAdapter adapterChat;
    ListView customListView_chat;
    private String urlAPI = "http://jodi.licious.id/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customListView_chat=(ListView)findViewById(R.id.right_nav);
        customListView_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), Chat.class);
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

        // tarik data dari tabel

        // tarik data user dari session
        HashMap<String, String> user = session.getUserDetails();
        String firstName = user.get(sessionmanager.SES_FIRST_NAME);
        String lastname = user.get(sessionmanager.SES_LAST_NAME);
        String email = user.get(sessionmanager.SES_EMAIL);
        final String userID = user.get(sessionmanager.SES_USER_ID);


        //set dalam textview
        txtNama = (TextView)findViewById(R.id.txtProfilNama);
        txtDrawerNama = (TextView)findViewById(R.id.txtDrawerNama);
        txtDrawerEmail = (TextView)findViewById(R.id.txtDrawerEmail);
        txtLokasi = (TextView)findViewById(R.id.txtProfilLokasi);
        txtTentang = (TextView)findViewById(R.id.txtProfilTentang);
        txtShortDescription = (TextView)findViewById(R.id.txtShortDescription) ;
        lblAbout = (TextView)findViewById(R.id.lblAbout);

        txtNama.setText(firstName + " " + lastname + ", " + age);
        txtDrawerNama.setText(firstName);
        txtDrawerEmail.setText(email);
        txtLokasi.setText(location);
        txtTentang.setText(userDetail);
        txtShortDescription.setText("Tinggi " + height + " cm, " + horoscope + ", " + job
                            + ", " + religion);
        lblAbout.setText("Tentang " + firstName + " " + lastname);

        imageView = (ImageView) findViewById(R.id.imageView);
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
                Intent i = new Intent(getBaseContext(), OtherProfile.class);
                i.putExtra("pID", pID);
                i.putExtra("userID", userID);
                startActivity(i);
                finish();
            }
        });

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ed = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(ed);
            }
        });

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

        IsiChat();
        RefreshListChat();
        listPasangan();
    }

    public void RefreshListChat() {



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

    /**@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }**/

    private void listPasangan(){
        HashMap<String, String> user = session.getUserDetails();
        String userid = user.get(sessionmanager.SES_USER_ID);
        String genderid=user.get(sessionmanager.SES_GENDER);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        urlAPI = urlAPI + "?userid="+userid+"&genderid="+genderid+"&page=1&jodiPasangan";
        JsonArrayRequest req = new JsonArrayRequest(urlAPI,
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
                                pasanganArray.add(partner);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        adapterListPasangan.notifyDataSetChanged();

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

    /**
     * Item Adapter untuk nampilkan daftar orang
     * yang online
     */
    private class ChatItemAdapter extends ArrayAdapter<Object>
    {
        ViewHolder holder1;

        public ChatItemAdapter(List<Object>items,int x) {
            // TODO Auto-generated constructor stub
            super(MainActivity.this, android.R.layout.simple_list_item_single_choice, items);
        }

        @Override
        public String getItem(int position) {
            // TODO Auto-generated method stub
            return dataArray_right.get(position);
        }

        public int getItemInteger(int pos)
        {
            return pos;

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataArray_right.size();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflator=getLayoutInflater();

            convertView=inflator.inflate(R.layout.list_chat, null);



            holder1=new ViewHolder();

            holder1.text=(TextView)convertView.findViewById(R.id.txtListOnlie);


            convertView.setTag(holder1);

            String text=dataArray_right.get(position);
            holder1.text.setText(dataArray_right.get(position));





            return convertView;
        }

    }

    private class ViewHolder {
        TextView text,textcounter;

    }
}
