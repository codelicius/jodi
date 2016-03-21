package makasa.dapurkonten.jodohideal;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;


public class Login extends AppCompatActivity {
    private static final String REGISTER_URL = AppConfig.urlAPI;

    private TextView info;
    private EditText editTextUsername, editTextPassword;
    private static String INI = Login.class.getSimpleName();
    TelephonyManager tel;

    //facebook punya
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    sessionmanager session;

    private SQLiteController db;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //manggil sesion manager
        session = new sessionmanager(getApplicationContext());
        //session.checkLogin();

        db = new SQLiteController(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        info = (TextView) findViewById(R.id.message);
        //loginButton = (LoginButton) findViewById(R.id.login_button);
        editTextUsername = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        //info.setText(tel.getSubscriberId().toString()); //IMSI
        //tel.getLine1Number()//phonenumber

        // login dengan facebook
/**        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final Profile profile = Profile.getCurrentProfile();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(INI, response.toString());

                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    //ambil nilai dari JSON respon API
                                    String jodiStatus = jsonResponse.getString("status");

                                    if (jodiStatus.equals("success")) {
                                        JSONObject dataUser = jsonResponse.getJSONObject("data");
                                        String jodiUserID = dataUser.getString("user_id"),
                                                jodiEmail = dataUser.getString("email"),
                                                jodiFirstName = dataUser.getString("first_name"),
                                                jodiLastName = dataUser.getString("last_name"),
                                                jodiGender = dataUser.getString("gender"),
                                                jodiBirthday = dataUser.getString("birth_date");
                                        session.buatSesiLogin(jodiUserID, jodiEmail, jodiFirstName, jodiLastName, jodiGender, jodiBirthday);
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        Log.d(INI, response.toString());
                                        //shownotification();
                                        startActivity(i);
                                        finish();
                                    } else {
                                        String jodiMessage = jsonResponse.getString("message");
                                        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(Login.this).create();
                                        alert.setTitle("Jodoh Ideal");
                                        alert.setMessage(jodiMessage);
                                        alert.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alert.show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    //proses kirim parameter ke
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("jodiIDSocmed", profile.getId());
                        params.put("jodiFName", profile.getFirstName());
                        params.put("jodiLName", profile.getLastName());
                        params.put("jodiLoginFB", "");
                        return params;
                    }


                };
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });**/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // link menuju register
    public void register(View view) {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }

    //link menuju main activity
    public void main(View view) {
        Intent i = new Intent(Login.this, webView.class);
        startActivity(i);
    }

    //proses login
    private void loginUser() {
        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final String email = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(INI, response.toString());

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //ambil nilai dari JSON respon API
                            String jodiStatus = jsonResponse.getString("status");

                            if (jodiStatus.equals("success")) {
                                JSONObject dataUser = jsonResponse.getJSONObject("data");
                                JSONObject profileUser = jsonResponse.getJSONObject("profile");

                                String jodiUserID = dataUser.getString("user_id"),
                                        jodiEmail = dataUser.getString("email"),
                                        jodiFirstName = dataUser.getString("first_name"),
                                        jodiLastName = dataUser.getString("last_name"),
                                        jodiGender = dataUser.getString("gender"),
                                        jodiBirthday = dataUser.getString("birth_date"),
                                        jodiIsFillProfile = dataUser.getString("is_fillprofile");

                                JSONArray jodiPartner = jsonResponse.getJSONArray("partner");

                                for (int i = 0; i < jodiPartner.length(); i++) {
                                    JSONObject partner = (JSONObject) jodiPartner.get(i);
                                    String partner_id = partner.getString("partner_id"),
                                            partner_fname = partner.getString("fname"),
                                            partner_lname = partner.getString("lname"),
                                            partner_image = partner.getString("image"),
                                            partner_gender = partner.getString("gender"),
                                            partner_race = partner.getString("race"),
                                            partner_religion = partner.getString("religion");
                                    int partner_match = partner.getInt("match"),
                                            partner_notmatch = partner.getInt("not_match"),
                                            partner_age = partner.getInt("age");

                                    db.addPartner(partner_id, partner_fname, partner_lname, partner_match,
                                            partner_notmatch, partner_image, partner_age, partner_gender, partner_race, partner_religion);


                                }

                                session.buatSesiLogin(jodiUserID, jodiEmail, jodiFirstName,
                                        jodiLastName, jodiGender, jodiBirthday);
                                if (jodiIsFillProfile.equals("0")) {
                                    Intent i = new Intent(getApplicationContext(), EditProfile.class);
                                    i.putExtra("fromActivity","profile");
                                    startActivity(i);
                                    finish();
                                } else {
                                    String profileAge = profileUser.getString("age"),
                                            profileGender = profileUser.getString("gender"),
                                            profileRace = profileUser.getString("race_name"),
                                            profileReligion = profileUser.getString("religion"),
                                            profileHeight = profileUser.getString("height"),
                                            profileLocation = profileUser.getString("loc_name"),
                                            profileHoroscope = profileUser.getString("horoscope_name"),
                                            profileJob = profileUser.getString("job_name"),
                                            profileDetail = profileUser.getString("self_desc"),
                                            profileFoto = profileUser.getString("foto_url"),
                                            profileMerokok = profileUser.getString("smoking"),
                                            profileAlkohol = profileUser.getString("alcohol"),
                                            profileTipePasangan = profileUser.getString("partner_desc"),
                                            profileKegiatan = profileUser.getString("activity"),
                                            profileInterest = profileUser.getString("hobby"),
                                            profileSatNite = profileUser.getString("sat_night");

                                    db.addUser(jodiUserID, jodiFirstName, jodiLastName, jodiEmail, profileGender,
                                            profileAge, profileRace, profileReligion, profileHeight, profileLocation,
                                            profileHoroscope, profileJob, profileDetail, profileFoto, profileMerokok, profileAlkohol,
                                            profileTipePasangan, profileKegiatan, profileInterest, profileSatNite);

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    //shownotification();
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                String jodiMessage = jsonResponse.getString("message");
                                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(Login.this).create();
                                alert.setTitle("Jodoh Ideal");
                                alert.setMessage(jodiMessage);
                                alert.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alert.show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            //proses kirim parameter ke
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("jodiEmail", email);
                params.put("jodiPassword", password);
                params.put("jodiLogin", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void shownotification() {
        Intent intent = new Intent(Login.this, Login.class);
        PendingIntent pIntent = PendingIntent.getActivity(Login.this, 0, intent, 0);
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("Belajar Notifikasi")
                .setContentText("Silahkan tap untuk melihat notifikasi!")
                .setSmallIcon(R.drawable.avatar)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotification);
    }

    public void login(View view) {
        loginUser();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://makasa.dapurkonten.jodohideal/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://makasa.dapurkonten.jodohideal/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
