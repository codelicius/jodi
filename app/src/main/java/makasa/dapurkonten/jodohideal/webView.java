package makasa.dapurkonten.jodohideal;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.URLDecoder;

/**
 * Created by abay on 11/03/16.
 */
public class webView extends AppCompatActivity {
    private WebView wv1;
    String provider,imsi;
    Integer durasi;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Bundle bundle=getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Loading ...");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        wv1=(WebView)findViewById(R.id.webview);


        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.setWebViewClient(new MyBrowser());
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setProgress(progress);
            }
        });
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
            else if(provider.equals("indosat")){
                //String url = "http://www.gudangapp.com";
                String url = "http://asik.indosatooredoo.com:8014/mcp/?kc=REG JODOH"+durasi+" "+imsi+" "+userid+"&sdc=93827&cb=jodoh://ideal/subscribe&desc=Layanan+Jodoh+Ideal&price=3300&servicename=JODOH"+durasi+"&img=&eid=a6610";
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
            else if(url.equals("http://202.152.162.239/ad/crossmcp/crossx.php")){
                Intent i = new Intent(webView.this, successSubscribe.class);
                i.putExtra("success","true");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("s", "s" + url);
                startActivity(i);
            }
            else if(url.startsWith("jodoh")) {
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
        ProgressDialog pd = new ProgressDialog(webView.this);

        public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
            AlertDialog.Builder adb = new AlertDialog.Builder(webView.this);
            adb.setTitle("Warning");
            adb.setMessage("Tidak dapat terhubung "+view.getUrl());
            adb.setPositiveButton("Coba Lagi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    view.reload();
                }
            });
            adb.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent x = new Intent(webView.this,MainActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(x);
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd.setMessage("Tunggu Sebentar");
            pd.show();
            super.onPageStarted(view, url, favicon);
        }
        public void onPageFinished(WebView view, String url) {
            pd.dismiss();
            getSupportActionBar().setTitle(view.getTitle());
            super.onPageFinished(view, url);
        }

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}