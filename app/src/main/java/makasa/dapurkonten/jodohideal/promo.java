package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import makasa.dapurkonten.jodohideal.app.AppController;

/**
 * Created by akbar on 28/06/2017.
 */

public class promo extends AppCompatActivity {
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);
        NetworkImageView niv = (NetworkImageView) findViewById(R.id.gambar);
        niv.setScaleType(NetworkImageView.ScaleType.CENTER_CROP);
        niv.setImageUrl("http://103.253.112.121/jodohideal/api/promo.png",mImageLoader);
        niv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(promo.this,Subscribe.class);
                startActivity(i);
            }
        });
        TextView close = (TextView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(promo.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }
}
