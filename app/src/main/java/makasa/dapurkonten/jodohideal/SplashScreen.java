package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME = 3000;
    sessionmanager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                session = new sessionmanager(getApplicationContext());
                session.checkDaftar();
                //session.checkLogin();
                finish();
            }
        }, SPLASH_TIME);
    }

}
