package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;

public class Event extends AppCompatActivity {
    protected TextView title,detail,idEvent;
    protected CheckBox cb;
    protected sessionmanager sesi;
    protected String userid;
    protected ImageView image_event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        title=(TextView)findViewById(R.id.title);
        detail=(TextView)findViewById(R.id.detail);
        cb = (CheckBox)findViewById(R.id.dontShow);
        idEvent = (TextView)findViewById(R.id.idEvent);
        image_event = (ImageView)findViewById(R.id.image_event);
        sesi = new sessionmanager(getApplicationContext());
        userid = sesi.getUserDetails().get("user_id");
        detailEvent();

    }
    public void close (View v){
        if(cb.isChecked())
            dismissEvent();
        else
            Log.d("checked ","false");
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    protected void dismissEvent(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("dismiss", "res "+response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status= jsonResponse.getString("status");
                            Log.d("status","s "+status);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error","sessionmanager "+error);
                        //Toast.makeText(sessionmanager.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("eventid",idEvent.getText().toString());
                params.put("userid",userid);
                params.put("jodiDismissEvent","");
                return params;
            }

        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Event.this);
        requestQueue.add(stringRequest);
    }
    public void joinEvent(View v){
        Intent i = new Intent(getApplicationContext(), questionsActivity.class);

        // Closing all the Activities from stack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(i);
    }
    protected void detailEvent(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.urlAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("t", response.toString());

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String idevent= jsonResponse.getString("id");
                            String details = jsonResponse.getString("detail_event");
                            String titles = jsonResponse.getString("nama_event");
                            String image = jsonResponse.getString("gambar_event");
                            try {
                                URL thumb_u = new URL("http://103.253.112.121/jodohidealxl/upload/" + image);
                                Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                                image_event.setImageDrawable(thumb_d);
                            }
                            catch (Exception e) {
                                image_event.setImageResource(R.drawable.question_default);
                                Log.d("imageview","error "+e);
                            }
                            idEvent.setText(idevent);
                            title.setText(titles);
                            detail.setText(details);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error","sessionmanager "+error);
                        //Toast.makeText(sessionmanager.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            //proses kirim parameter ke
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("jodiEventDetail","");
                return params;
            }

        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Event.this);
        requestQueue.add(stringRequest);
    }

}
