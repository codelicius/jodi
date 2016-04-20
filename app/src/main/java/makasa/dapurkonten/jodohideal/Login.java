package makasa.dapurkonten.jodohideal;

import com.android.datetimepicker.Utils;
import com.android.volley.DefaultRetryPolicy;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;
import makasa.dapurkonten.jodohideal.app.SQLiteController;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {
    private static final String REGISTER_URL = AppConfig.urlAPI;

    private TextView info;
    private EditText editTextUsername, editTextPassword;
    private static String INI = Login.class.getSimpleName();
    TelephonyManager tel;

    //facebook punya
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    sessionmanager session;

    private SQLiteController db;
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApiClient;

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
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        custimizeSignBtn();
        setBtnClickListeners();
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;
            String idfb,fname,lname;
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            idfb = profile2.getId();
                            fname = profile2.getFirstName();
                            lname = profile2.getLastName();
                            loginFB(idfb,fname,lname);
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.d("baru","lama "+profile.getId());
                    idfb = profile.getId();
                    fname = profile.getFirstName();
                    lname = profile.getLastName();
                    loginFB(idfb,fname,lname);

                }
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
        editTextUsername = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        //info.setText(tel.getSubscriberId().toString()); //IMSI
        //tel.getLine1Number()//phonenumber

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        buidNewGoogleApiClient();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }
    private void buidNewGoogleApiClient(){

        google_api_client =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }
    private void custimizeSignBtn(){

        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});

    }



    // link menuju register
    protected void loginFB(final String id,final String fname,final String lname){
        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(INI,"respons "+response);

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
                                Intent i = new Intent(getApplicationContext(),Register.class);
                                i.putExtra("fname",fname);
                                i.putExtra("lname",lname);
                                i.putExtra("source","fb");
                                i.putExtra("option",id);
                                startActivity(i);
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
                        LoginManager.getInstance().logOut();
                        Log.d("error ", "error " + error.toString());
                        //Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            //proses kirim parameter ke
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("jodiLoginFB", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    public void register(View view) {
        Intent i = new Intent(Login.this, Register.class);
        i.putExtra("source","");
        i.putExtra("option","");
        startActivity(i);
    }
    protected void loginGplus(final String id,final String fname,final String lname,final String email){
        final ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(INI,"respons "+response);

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
                                    gPlusSignOut();
                                    finish();
                                }
                            } else {
                                Intent i = new Intent(getApplicationContext(),Register.class);
                                i.putExtra("fname",fname);
                                i.putExtra("lname",lname);
                                i.putExtra("email",email);
                                i.putExtra("source","gplus");
                                i.putExtra("option",id);
                                gPlusSignOut();
                                startActivity(i);
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
                        LoginManager.getInstance().logOut();
                        Log.d("error ", "error " + error.toString());
                        //Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            //proses kirim parameter ke
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("jodiLoginFB", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

                                String jodiUserID = dataUser.getString("user_id"),
                                        jodiEmail = dataUser.getString("email"),
                                        jodiFirstName = dataUser.getString("first_name"),
                                        jodiLastName = dataUser.getString("last_name"),
                                        jodiGender = dataUser.getString("gender"),
                                        jodiBirthday = dataUser.getString("birth_date"),
                                        jodiIsFillProfile = dataUser.getString("is_fillprofile");



                                session.buatSesiLogin(jodiUserID, jodiEmail, jodiFirstName,
                                        jodiLastName, jodiGender, jodiBirthday);
                                if (jodiIsFillProfile.equals("0")) {
                                    Intent i = new Intent(getApplicationContext(), EditProfile.class);
                                    i.putExtra("fromActivity","profile");
                                    startActivity(i);
                                    finish();
                                } else {
                                    JSONObject profileUser = jsonResponse.getJSONObject("profile");

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
                        progressDialog.dismiss();
                        android.app.AlertDialog infoPass = new android.app.AlertDialog.Builder(Login.this).create();
                        infoPass.setTitle("Perhatian");
                        infoPass.setMessage("Gagal terhubung dengan server, silakan cek koneksi internet anda");
                        infoPass.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        loginUser();
                                    }
                                });
                        infoPass.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        infoPass.show();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private void setBtnClickListeners(){
        // Button listeners
        signIn_btn.setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume(){
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }

    /*
      Will receive the activity result and check which request we are responding to

     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        // Update the UI after signin
        //changeUI(true);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
        //changeUI(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //Toast.makeText(this, "start sign process", Toast.LENGTH_SHORT).show();
                gPlusSignIn();
                break;
            /**case R.id.sign_out_button:
                Toast.makeText(this, "Sign Out from G+", Toast.LENGTH_LONG).show();
                gPlusSignOut();

                break;
            case R.id.disconnect_button:
                Toast.makeText(this, "Revoke Access from G+", Toast.LENGTH_LONG).show();
                gPlusRevokeAccess();

                break;**/
        }
    }

    /*
      Sign-in into the Google + account
     */

    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected","connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        }
    }

    /*
      Method to resolve any signin errors
     */

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    /*
      Sign-out from Google+ account
     */

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
          //  changeUI(false);
        }
    }

    /*
     Revoking access from Google+ account
     */

    private void gPlusRevokeAccess() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            Plus.AccountApi.revokeAccessAndDisconnect(google_api_client)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d("MainActivity", "User access revoked!");
                            buidNewGoogleApiClient();
                            google_api_client.connect();
            //                changeUI(false);
                        }

                    });
        }
    }

    /*
     get user's information name, email, profile pic,Date of birth,tag line and about me
     */

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);

            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     set the User information into the views defined in the layout
     */

    private void setPersonalInfo(Person currentPerson){

        String fName = currentPerson.getName().getGivenName();
        String lName = currentPerson.getName().getFamilyName();
        String personPhotoUrl = currentPerson.getImage().getUrl();
        String email = Plus.AccountApi.getAccountName(google_api_client);
        String id = currentPerson.getId();
        loginGplus(id,fName,lName,email);
        /**Intent i = new Intent(getApplicationContext(),Register.class);
        i.putExtra("fname",fName);
        i.putExtra("lname",lName);
        i.putExtra("source","gplus");
        i.putExtra("email",email);
        i.putExtra("option",id);
        startActivity(i);**/
        //setProfilePic(personPhotoUrl);
        Log.d("namaGplus","nama "+fName+lName+" email "+email+" id "+id);
        progress_dialog.dismiss();
        //Toast.makeText(this, "Person information is shown!", Toast.LENGTH_LONG).show();
    }

    /*
     By default the profile pic url gives 50x50 px image.
     If you need a bigger image we have to change the query parameter value from 50 to the size you want
    */

    private void setProfilePic(String profile_pic){
        profile_pic = profile_pic.substring(0,
                profile_pic.length() - 2)
                + PROFILE_PIC_SIZE;
        //ImageView    user_picture = (ImageView)findViewById(R.id.profile_pic);
        //new LoadProfilePic(user_picture).execute(profile_pic);
    }

    /*
     Show and hide of the Views according to the user login status
     */

    private void changeUI(boolean signedIn) {
        /**if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }**/
    }

   /*
    Perform background operation asynchronously, to load user profile picture with new dimensions from the modified url
    */

}