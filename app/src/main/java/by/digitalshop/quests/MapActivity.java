package by.digitalshop.quests;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import by.digitalshop.quests.event.MapMotionEvent;
import by.digitalshop.quests.model.MapQuestMarker;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

/**
 * Created by CoolerBy on 18.12.2016.
 */

public class MapActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener, OnMapListener, View.OnClickListener {
    private static final String TAG = "MapActivity";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 124;
    private static final int BUTTON_DELAY = 5000;
    private static final int DEFAULT_ZOOM = 16;

    private View buttonCreate;
    private View buttonAdd;
    private View buttonSearch;
    private View buttonZoomIn;
    private View buttonZoomOut;
    private View buttonGpsFix;
    private View buttonMenu;

    private MapView mMapView;
    private MapController mController;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Handler mHandler;
    private Drawable mMarkerDrawable;

    private int mZoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mHandler = new Handler(Looper.getMainLooper());
        mMapView = (MapView) findViewById(R.id.map);
        buttonCreate = findViewById(R.id.btn_create);
        buttonAdd = findViewById(R.id.btn_add);
        buttonSearch = findViewById(R.id.btn_search);
        buttonZoomIn = findViewById(R.id.btn_zoom_in);
        buttonZoomOut = findViewById(R.id.btn_zoom_out);
        buttonGpsFix = findViewById(R.id.btn_gps_fix);
        buttonMenu = findViewById(R.id.btn_menu);
        buttonCreate.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonZoomIn.setOnClickListener(this);
        buttonZoomOut.setOnClickListener(this);
        buttonGpsFix.setOnClickListener(this);
        buttonMenu.setOnClickListener(this);
        mMarkerDrawable = ContextCompat.getDrawable(this, R.drawable.ic_marker);
        if (!checkPermission(this)) {
            showPermissionDialog();
        }
        mMapView.setVisibility(View.GONE);
        mController = mMapView.getMapController();
        setMapZoom(DEFAULT_ZOOM);
        mController.addMapListener(this);
        handleMarkers();
//        mMapView.setOnTouchListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (checkPermission(this)) {
            resumeAfterPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMapMotion(MapMotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setControlsTrack();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mHandler.postDelayed(mRunnable, BUTTON_DELAY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gps_fix:
                centerPosition();
                break;
            case R.id.btn_zoom_in:
                setMapZoom(mZoom++);
                break;
            case R.id.btn_zoom_out:
                setMapZoom(mZoom--);
                break;
            case R.id.btn_menu:
                showDeleteAllDialog();
                break;
            case R.id.btn_add:
            case R.id.btn_create:
                showCreateMarkerDialog();
                break;
            default:

        }
    }

    private void showCreateMarkerDialog() {
        setControlsTrack();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_marker);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.dialog_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setControlsUnTrack();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setControlsUnTrack();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.ic_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMarker(
                        ((EditText) dialog.findViewById(R.id.edit_title)).getText().toString(),
                        ((EditText) dialog.findViewById(R.id.edit_text)).getText().toString()
                );
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createMarker(String title, String text) {
        GeoPoint point = mController.getMapCenter();
        MapQuestMarker marker = new MapQuestMarker(point.getLat(), point.getLon(), title, text);
        DsqApplication.sDaoSession.getMapQuestMarkerDao().save(marker);
        List<MapQuestMarker> markers = new ArrayList<>(1);
        markers.add(marker);
        addMarkers(markers);
    }

    private void handleMarkers() {
        List<MapQuestMarker> markers = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadAll();
        addMarkers(markers);
    }

    private void addMarkers(List<MapQuestMarker> markers) {
        Overlay overlay = new Overlay(mController);
        for (MapQuestMarker marker : markers) {
            GeoPoint point = new GeoPoint(marker.getLatitude(), marker.getLongitude());
            OverlayItem item = new OverlayItem(point, mMarkerDrawable);
            BalloonItem balloon = new BalloonItem(this, point);
            balloon.setText(marker.getTitle() + "\n" + marker.getText());
            item.setBalloonItem(balloon);
            overlay.addOverlayItem(item);
        }
        mController.getOverlayManager().addOverlay(overlay);
    }

    private void centerPosition() {
        if (checkPermission(this)) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d(TAG, "Last location : " + mLocation.getLatitude() + ", " + mLocation.getLongitude());

            GeoPoint point = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            mController.setPositionNoAnimationTo(point);
        }
    }

    private void setMapZoom(int zoom) {
        mZoom = zoom;
        mController.setZoomCurrent(mZoom);
    }

    private void setControlsTrack() {
        mHandler.removeCallbacks(mRunnable);
        buttonCreate.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.GONE);
        buttonZoomIn.setVisibility(View.GONE);
        buttonZoomOut.setVisibility(View.GONE);
        buttonGpsFix.setVisibility(View.GONE);
        buttonMenu.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.VISIBLE);
    }

    private void setControlsUnTrack() {
        buttonAdd.setVisibility(View.GONE);
        buttonCreate.setVisibility(View.VISIBLE);
        buttonSearch.setVisibility(View.VISIBLE);
        buttonZoomIn.setVisibility(View.VISIBLE);
        buttonZoomOut.setVisibility(View.VISIBLE);
        buttonGpsFix.setVisibility(View.VISIBLE);
        buttonMenu.setVisibility(View.VISIBLE);
    }

    private void resumeAfterPermissions() {
        Log.d(TAG, "Resuming after permissions");
        mMapView.setVisibility(View.VISIBLE);
    }

    private void showDeleteAllDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("удалить все маркеры?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DsqApplication.sDaoSession.getMapQuestMarkerDao().deleteAll();
                dialog.dismiss();
                recreate();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showPermissionDialog() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted");
                } else {
                    Toast.makeText(MapActivity.this, "You need to grant permissions to see map", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static Intent buildIntent(Context context) {
        return new Intent(context, MapActivity.class);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        centerPosition();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "-touch");
        switch (v.getId()) {
            case R.id.map:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "!!!!!!!! UP !!!!!!!!!!");
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "!!!!!!!!!!! DOWN !!!!!!!!");
                    return false;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setControlsUnTrack();
        }
    };

    @Override
    public void onMapActionEvent(MapEvent mapEvent) {
//        Log.d(TAG, "MapEvent : " + mapEvent.getMsg());
    }
}
