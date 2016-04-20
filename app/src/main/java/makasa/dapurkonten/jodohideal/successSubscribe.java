package makasa.dapurkonten.jodohideal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by abay on 11/03/16.
 */
public class successSubscribe extends AppCompatActivity {
    protected TextView status;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_subscribe);
        status = (TextView)findViewById(R.id.suksesStatus);
        Intent i = getIntent();
        String sukses = i.getStringExtra("success");
        if(sukses.equals("false"))
            status.setText("permintaan anda gagal,silakan coba lagi");

    }
    public void home(View v){
        Intent i=new Intent(successSubscribe.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
