package com.studio.android.utaappointmentscheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Deathlord on 12/4/2017.
 */

public class notifyer {
    public notifyer(){}

    public void mNotify(Context lContext, Class lClass,String lMessage){
        String channelID="";
        NotificationCompat.Builder b =new  NotificationCompat.Builder(lContext,  channelID);

        Intent resultIntent = new Intent(lContext,lClass);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        lContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.uta_img)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("UTA Appointment Scheduler")
                .setContentText(lMessage);




        b.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) lContext.getSystemService(lContext.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, b.build());
    }
}
