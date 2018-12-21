package sugtao4423.batterynotifier;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class BatteryWatchService extends Service{

    private BroadcastReceiver changeBatteryStateReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        setServicePingReceiver();

        changeBatteryStateReceiver = new ChangeBatteryStateReceiver();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(changeBatteryStateReceiver, iFilter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(26)
    private void startNotification(){
        String channelId = "default";
        String title = "Watching battery level";

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 810, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(114514, notification);
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy(){
        if(changeBatteryStateReceiver != null){
            unregisterReceiver(changeBatteryStateReceiver);
        }
        super.onDestroy();
    }

    private void setServicePingReceiver(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new ServicePingReceiver(), new IntentFilter("ping"));
    }

    private class ServicePingReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcastSync(new Intent("pong"));
        }
    }

    private class ChangeBatteryStateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
            new ChangeBatteryState(context, intent);
        }

    }

}
