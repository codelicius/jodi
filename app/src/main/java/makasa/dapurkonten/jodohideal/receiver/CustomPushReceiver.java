package makasa.dapurkonten.jodohideal.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import makasa.dapurkonten.jodohideal.MainActivity;

public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Log.d("Push", "Push received");

        if (intent == null)
            return ;

        String jsonData = intent.getExtras().getString("com.parse.Data");

        Log.d("Push", "JSON Data ["+jsonData+"]");

        String data = getData(jsonData);


        // Add custom intent
        Intent cIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create custom notification
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context)
                .setContentText(data)
                .setContentTitle("Notification from Parse")
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1410, notification);

    }

    private String getData(String jsonData) {
        // Parse JSON Data
        try {
            System.out.println("JSON Data ["+jsonData+"]")  ;
            JSONObject obj = new JSONObject(jsonData);
            Log.d("pasringdatafromparse",obj.toString());
            return obj.getString("message");
        }
        catch(JSONException jse) {
            jse.printStackTrace();
        }

        return "";
    }
}
