package makasa.dapurkonten.jodohideal.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import makasa.dapurkonten.jodohideal.MainActivity;
import makasa.dapurkonten.jodohideal.R;
import makasa.dapurkonten.jodohideal.SplashScreen;

/**
 * Created by pr1de on 04/02/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Jodoh Ideal")
                        .setContentText("Hi! Seseorang mengirimkan kamu pesan. Cek sekarang juga!")
                        .setContentIntent(pi)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
