package by.digitalshop.quests.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import by.digitalshop.quests.MapActivity;
import by.digitalshop.quests.R;

import static ru.yandex.core.CoreApplication.getApplicationContext;

/**
 * Created by CoolerBy on 18.01.2017.
 */

public class Notifications {

    public static Notification createNotification(Context context, String title, String subject, long id, double lat, double lon) {
        if (context == null) {
            context = getApplicationContext();
        }
        Intent notificationIntent = new Intent(context, MapActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.setAction(MapActivity.ACTION_NOTIFICATION);
        notificationIntent.putExtra(MapActivity.EXTRA_MARKER_ID, id);
        notificationIntent.putExtra(MapActivity.EXTRA_LAT, lat);
        notificationIntent.putExtra(MapActivity.EXTRA_LON, lon);

        PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setStyle(new Notification.BigTextStyle().bigText(subject))
                .setSmallIcon(R.drawable.ic_marker)
                .setContentIntent(activity)
                .build();
        return noti;
    }
}
