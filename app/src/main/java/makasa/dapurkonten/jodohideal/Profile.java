package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import org.w3c.dom.Text;

import java.util.HashMap;

import makasa.dapurkonten.jodohideal.app.SQLiteController;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    sessionmanager session;
    private SQLiteController db;
    TextView nama,umur,tb,agama,lokasi,horoskop,jk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nama=(TextView)findViewById(R.id.viewProfileNama);
        umur=(TextView)findViewById(R.id.viewProfileUmur);
        tb=(TextView)findViewById(R.id.viewProfileTinggi);
        agama=(TextView)findViewById(R.id.viewProfileAgama);
        lokasi=(TextView)findViewById(R.id.viewProfileLokasi);
        horoskop=(TextView)findViewById(R.id.viewProfileHoroskop);
        jk=(TextView)findViewById(R.id.viewProfileGender);

        db = new SQLiteController(getApplicationContext());
        HashMap<String,String> user=db.getUserDetails();
        String age=user.get("age"),
                gender=user.get("gender"),
                fname=user.get("first_name"),
                lname=user.get("last_name"),
                height=user.get("height"),
                location=user.get("location"),
                horoscope=user.get("horoscope"),
                religion=user.get("religion");
        nama.setText(fname + ' ' + lname);
        umur.setText(age);
        tb.setText(height);
        agama.setText(religion);
        lokasi.setText(location);
        horoskop.setText(horoscope);
        jk.setText(gender);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEditProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.profile, menu);
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
            Intent hm = new Intent(this, MainActivity.class);
            startActivity(hm);
        }
        else if (id == R.id.nav_profile) {
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
}
