package com.wdd.new2048;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    private MyBinder mBinder = new MyBinder();

    class MyBinder extends Binder{
        public void startMe() {
            Log.d("Myservice", "startMe executed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate Service");

        //Open the front desk service
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("The 2048 Game")
                .setContentText("开启前台服务咯")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.flower)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.flower))
                .setContentIntent(pendingIntent);
        //solve the Bug of "Bad notification for startForefround"
        /*Because Android 8.0 has subdivided notifications and introduced channels, this mainly affects two aspects:
        * A Ordinary notification of Notification
        * B Front desk service notification*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
