package by.digitalshop.quests.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import by.digitalshop.quests.DsqApplication;
import by.digitalshop.quests.R;
import by.digitalshop.quests.model.MapQuestMarker;
import by.digitalshop.quests.model.MapQuestMarkerDao;
import by.digitalshop.quests.utils.Notifications;
import by.digitalshop.quests.utils.Utils;

import static by.digitalshop.quests.MapActivity.ID_PROXIMITY_NOTIFICATION;
import static by.digitalshop.quests.MapActivity.NOTIFICATION_THRESHOLD;
import static by.digitalshop.quests.MapActivity.PROXIMITY_THRESHOLD;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int ONE_MINUTES = 1000 * 60 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    private Set<Long>notificationIds = new HashSet<>();

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return START_STICKY;
        }
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 300, listener);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 300, listener);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("TAG","location change in background service !");
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                MapQuestMarkerDao mapQuestMarkerDao = DsqApplication.sDaoSession.getMapQuestMarkerDao();
                List<MapQuestMarker> markers = mapQuestMarkerDao.loadAll();
                boolean isNearToMarker = false;
                StringBuilder notificationMessage = new StringBuilder();
                double lat = 0.0,lon = 0.0;
                long id = -1;

                final List<MapQuestMarker> res = new ArrayList<MapQuestMarker>(markers.size());
                for (MapQuestMarker marker : markers) {
                    double distance = Utils.calculateDistance(marker.getLatitude(), marker.getLongitude(), loc.getLatitude(), loc.getLongitude());
                    if (distance < PROXIMITY_THRESHOLD) {
                        isNearToMarker = true;
                    }

                    if (distance < NOTIFICATION_THRESHOLD && !notificationIds.contains(id) && !marker.isNotified()) {
                        Log.i("TAG","get notification in background service !");
                        lat = marker.getLatitude();
                        lon = marker.getLongitude();
                        id = marker.getId();

                        marker.setNotified(true);
                        notificationMessage.append(getString(R.string.distance_to));
                        notificationMessage.append(" ");
                        notificationMessage.append(marker.getTitle());
                        notificationMessage.append(" ");
                        DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
                        notificationMessage.append(mDecimalFormat.format(distance));
                        notificationMessage.append(" ");
                        notificationMessage.append("m");
                        notificationMessage.append("\n");
                        notificationIds.add(id);
                        res.add(marker);
                    }else if(distance > NOTIFICATION_THRESHOLD && notificationIds.contains(id) && marker.isNotified()){
                        marker.setNotified(false);
                        notificationIds.remove(id);
                        if(res.contains(marker)){
                            res.remove(marker);
                        }
                    }else if(distance >NOTIFICATION_THRESHOLD && marker.isNotified()){
                        marker.setNotified(false);
                        if(res.contains(marker)){
                            res.remove(marker);
                        }
                    }
                }
                DsqApplication.sDaoSession.getMapQuestMarkerDao().updateInTx(markers);

                if (notificationMessage.length() > 1) {
                    notificationMessage.setLength(notificationMessage.length() - 1);
                    Notification notification = Notifications.createNotification(LocationService.this, getString(R.string.notification_title), notificationMessage.toString(),id,lat,lon);
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(ID_PROXIMITY_NOTIFICATION, notification);
                }
            }
        }

        public void onProviderDisabled(String provider)
        {
        }


        public void onProviderEnabled(String provider)
        {
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }
}
