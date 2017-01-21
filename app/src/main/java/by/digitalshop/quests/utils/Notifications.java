package by.digitalshop.quests.utils;

import android.app.Notification;
import android.content.Context;

import by.digitalshop.quests.R;

/**
 * Created by CoolerBy on 18.01.2017.
 */

public class Notifications {

    public static Notification createNotification(Context context, String title, String subject) {
        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setStyle(new Notification.BigTextStyle().bigText(subject))
                .setSmallIcon(R.drawable.ic_marker)
//                .setLargeIcon()
                .build();
        return noti;
    }
}
