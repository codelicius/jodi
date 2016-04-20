package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLDecoder;

/**
 * Created by abay on 11/03/16.
 */
public class webView extends AppCompatActivity {
    private WebView wv1;
    String provider,imsi;
    Integer durasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Bundle bundle=getIntent().getExtras();
        wv1=(WebView)findViewById(R.id.webview);


        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.setWebViewClient(new MyBrowser());
        wv1.getSettings().setLoadsImagesAutomatically(true);
        if(bundle.getString("activity").equals("charge")){
            provider = bundle.getString("provider");
            imsi = bundle.getString("imsi");
            int durasi=bundle.getInt("durasi");
            String userid = bundle.getString("userid");
            if(provider.equals("xl")){
                //String url = "http://www.gudangapp.com";
                String url = "http://www.gudangapp.com/xlp/?kc=REG JODOH"+durasi+" "+imsi+" "+userid+"&sdc=93827&cb=jodoh://ideal/subscribe&desc=Berlangganan Layanan Aplikasi Jodoh Ideal&img=http://103.253.112.121/2/scjodoh.jpg&eid=b8f16";
                wv1.loadUrl(url);
            }
            else{
                wv1.loadUrl("http://google.com");
            }
        }
        else{
            wv1.loadUrl("http://google.com");
        }
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.equals("http://www.gudangapp.com/store/wap/portalone")){
                Intent i = new Intent(webView.this, successSubscribe.class);
                i.putExtra("success","false");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("s", "s" + url);
                startActivity(i);
            }
            if(url.startsWith("jodoh:")) {
                Intent i = new Intent(webView.this, successSubscribe.class);
                i.putExtra("success","true");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("s", "s" + url);
                startActivity(i);
            }
            else {
                Log.d("s", "s" + url);
                view.loadUrl(url);
            }
            return true;
        }
    }
}