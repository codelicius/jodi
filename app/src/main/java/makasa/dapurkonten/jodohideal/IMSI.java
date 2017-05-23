package makasa.dapurkonten.jodohideal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import makasa.dapurkonten.jodohideal.app.AppConfig;

/**
 * Created by abay on 10/03/16.
 */
public class IMSI {
    private static final String REGISTER_URL = AppConfig.urlAPI;
    public final String smartfren1 = "51009";
    public final String smartfren2 = "51028";
    public final String xl = "51011";
    public final String axis = "51008";
    public final String indosat = "51001";
    public final String indosat2 = "51021";
    public final String test = "31026";
    Context _context;
    public IMSI(Context context){
        this._context=context;
    }
    public boolean getProvider(final String imsi,int dur, final String userid){
        String provider = imsi.substring(0, 5);
        Log.d("provider",provider +" imsi "+imsi);
        if(provider.equals(xl) || provider.equals(axis)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("update","ok");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(_context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                //proses kirim parameter ke
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", userid);
                    params.put("mdn", imsi);
                    params.put("jodiMdn", "");
                    Log.d("userid",userid +" imsi "+imsi);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(_context);
            requestQueue.add(stringRequest);
            Intent i=new Intent(_context,webView.class);
            int durasi;
            i.putExtra("activity","charge");
            i.putExtra("provider","xl");
            i.putExtra("imsi",imsi);
            i.putExtra("userid", userid);
            if(dur == 1)
                durasi = 7;
            else
                durasi = 14;
            i.putExtra("durasi",durasi);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

            return false;
        }
        else if(!provider.equals(indosat) || !provider.equals(indosat2)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("update","ok");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(_context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                //proses kirim parameter ke
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", userid);
                    params.put("mdn", imsi);
                    params.put("jodiMdn", "");
                    Log.d("userid",userid +" imsi "+imsi);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(_context);
            requestQueue.add(stringRequest);
            Intent i=new Intent(_context,webView.class);
            int durasi;
            i.putExtra("activity","charge");
            i.putExtra("provider","indosat");
            i.putExtra("imsi",imsi);
            i.putExtra("userid", userid);
            if(dur == 1)
                durasi = 7;
            else
                durasi = 14;
            i.putExtra("durasi",durasi);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

            return false;
        }
        else if(provider.equals(smartfren1) || provider.equals(smartfren2)){
            return true;
        }
        else{
            Toast.makeText(_context, "Maaf layanan kami hanya mendukung provider XL, INDOSAT, SMARTFREN", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
