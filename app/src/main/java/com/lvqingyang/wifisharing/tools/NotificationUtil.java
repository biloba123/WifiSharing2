package com.lvqingyang.wifisharing.tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.lvqingyang.wifisharing.R;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/12/20
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class NotificationUtil {
    public static void showChargeNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

//         PendingIntent pi = PendingIntent.getActivity(context, 0,
//                 new Intent(, .class), 0);
         NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                 .setTicker(context.getString(R.string.connect_succ))
                 .setSmallIcon(R.drawable.ic_traffic)
                 .setContent(new RemoteViews(context.getPackageName(), R.layout.layout_notify))
                 .setChannelId(CHANNEL_ID);
         Notification notification = builder.build();
         notificationManager.notify(0, notification);
    }
}
