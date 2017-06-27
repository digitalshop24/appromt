package by.digitalshop.quests;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import by.digitalshop.quests.adapter.MarkerPagerAdapter;
import by.digitalshop.quests.event.MapMotionEvent;
import by.digitalshop.quests.model.MapQuestMarker;
import by.digitalshop.quests.model.MapQuestMarkerDao;
import by.digitalshop.quests.model.Place;
import by.digitalshop.quests.model.SearchHistoryItem;
import by.digitalshop.quests.service.LocationService;
import by.digitalshop.quests.utils.Notifications;
import by.digitalshop.quests.utils.Utils;
import by.digitalshop.quests.view.CreateMarkerRect;
import by.digitalshop.quests.view.CurrentPlaceView;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

import static by.digitalshop.quests.R.id.btn_rectcreate;

/**
 * Created by CoolerBy on 18.12.2016.
 */

public class MapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnTouchListener,
        LocationListener,
        OnMapListener,
        View.OnClickListener {

    private static final String TAG = "MapActivity";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 124;
    private static final int BUTTON_DELAY = 1500;
    private static final int DEFAULT_ZOOM = 16;

    private static final int SLIDER_HEIGHT_PADDING = 200;

    public static final int PROXIMITY_THRESHOLD = 150;
    public static final int NOTIFICATION_THRESHOLD = 150;
    public static final int ID_PROXIMITY_NOTIFICATION = 123;

    public static final String ACTION_MOVE_FROM_HISTORY = "ACTION_MOVE_FROM_HISTORY";
    public static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";

    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LON = "EXTRA_LON";
    public static final String EXTRA_MARKER_ID = "EXTRA_MARKER_ID";
    public static final String EXTRA_SEARCH_ID = "EXTRA_SEARCH_ID";


    private View buttonCreate;
    private View buttonSearch;
    private View buttonDelete;
    private View buttonBackSlider;
    private View buttonZoomIn;
    private View buttonZoomOut;
    private View buttonGpsFix;
    private View buttonMenu;
    private PagerContainer containerSlider;
    private CreateMarkerRect buttonCreateMarkerRect;
    private FrameLayout sliderBackground;

    private MapView mMapView;
    private CurrentPlaceView mCurrentPlace;
    private MapController mController;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Handler mHandler, mSliderHandler;
    private Drawable mMarkerDrawable;
    private GeoPoint mSavedPoint;
    private LocationRequest mLocationRequestAccurate;
    private LocationRequest mLocationRequestCoarse;

    private int mZoom;
    private long selectedSlideMarkerId = -1;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isHighAccuracyMode;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.#");
    private long _notifyID = -1;


    public static Intent startIntentMap(Context context, long id) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.setAction(ACTION_MOVE_FROM_HISTORY);
        intent.putExtra(EXTRA_SEARCH_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mHandler = new Handler(Looper.getMainLooper());
        mSliderHandler = new Handler(Looper.getMainLooper());

        mMapView = (MapView) findViewById(R.id.map);
        buttonCreate = findViewById(R.id.btn_create);
        buttonDelete = findViewById(R.id.btn_delete);
        buttonBackSlider = findViewById(R.id.btn_back_slider);
        buttonSearch = findViewById(R.id.btn_search);
        buttonZoomIn = findViewById(R.id.btn_zoom_in);
        buttonZoomOut = findViewById(R.id.btn_zoom_out);
        buttonGpsFix = findViewById(R.id.btn_gps_fix);
        buttonMenu = findViewById(R.id.btn_menu);
        mCurrentPlace = (CurrentPlaceView) findViewById(R.id.currentPlace);
        buttonCreateMarkerRect = (CreateMarkerRect) findViewById(btn_rectcreate);
        containerSlider = (PagerContainer) findViewById(R.id.container_slider);
        sliderBackground = (FrameLayout) findViewById(R.id.slider_background);
        buttonSearch.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonZoomIn.setOnClickListener(this);
        buttonZoomOut.setOnClickListener(this);
        buttonGpsFix.setOnClickListener(this);
        buttonMenu.setOnClickListener(this);
        buttonBackSlider.setOnClickListener(this);
        sliderBackground.setOnClickListener(this);
        buttonCreateMarkerRect.setOnClickListener(this);
        mCurrentPlace.setOnClickListener(this);

        mMarkerDrawable = ContextCompat.getDrawable(this, R.drawable.ic_marker);
        if (!checkPermission(this)) {
            showPermissionDialog();
        }
        mMapView.setVisibility(View.GONE);
        mController = mMapView.getMapController();
        setMapZoom(DEFAULT_ZOOM);
        mController.addMapListener(this);
        handleMarkers();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mLocationRequestAccurate = new LocationRequest();
        mLocationRequestAccurate.setInterval(4000);
        mLocationRequestAccurate.setFastestInterval(1000);
        mLocationRequestAccurate.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestAccurate.setSmallestDisplacement(1.0f);

        mLocationRequestCoarse = new LocationRequest();
        mLocationRequestCoarse.setInterval(10000);
        mLocationRequestCoarse.setFastestInterval(5000);
        mLocationRequestCoarse.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequestCoarse.setSmallestDisplacement(10.0f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Intent intent1 = intent;
        handleNotificationMarker(intent1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            Log.d(TAG, "location service already disconnected");
        }
        mHandler.removeCallbacks(mMapRunnable);
        startService(new Intent(this, LocationService.class));
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
            mHandler.post(mMapMoveRunnable);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mHandler.postDelayed(mMapRunnable, BUTTON_DELAY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mapCreateRect:
                hideCurrentPlace();
                showCreateMarkerDialog();
                break;
            case R.id.tvCurrentPlace:
                hideCurrentPlace();
                startSearchActivity(mCurrentPlace.getCurrentPlaceText().getText().toString());
                break;
            case R.id.action_close_place:
                hideCurrentPlace();
                getIntent().setAction("");
                break;
            case R.id.btn_gps_fix:
                centerPosition();
                break;
            case R.id.btn_zoom_in:
                setMapZoom(mZoom++);
                mController.zoomIn();
                break;
            case R.id.btn_zoom_out:
                setMapZoom(mZoom--);
                mController.zoomOut();
                break;
            case R.id.btn_menu:
                hideCurrentPlace();
                mHandler.post(mMapControlRunnable);
                List<MapQuestMarker> markers = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadAll();
                showSliderMarkers(markers);
                break;
            case R.id.btn_search:
                startSearchActivity("");
                break;
            case R.id.slider_background:
            case R.id.btn_back_slider:
                hideSlider();
                mHandler.post(mMapRunnable);
                break;
            case R.id.btn_delete:
                long selected = selectedSlideMarkerId;
                hideSlider();
                showDeleteDialog(selected);
                break;
            case R.id.btn_rectcreate:
            case R.id.btn_add:
            case R.id.btn_create:
                hideCurrentPlace();
                showCreateMarkerDialog();
                break;
            default:

        }
    }

    private void handleNotificationMarker(Intent intent) {
        if (intent.getAction().equals(ACTION_NOTIFICATION)) {
            double lat = intent.getDoubleExtra(EXTRA_LAT, 0.0);
            double lon = intent.getDoubleExtra(EXTRA_LON, 0.0);
            _notifyID = intent.getLongExtra(EXTRA_MARKER_ID, -1);

            MapQuestMarker marker = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadByRowId(_notifyID);

            ArrayList<MapQuestMarker> markers = new ArrayList<>();
            markers.add(marker);
            showSliderMarkers(markers);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID_PROXIMITY_NOTIFICATION);
        }
    }

    private void hideSlider() {
        selectedSlideMarkerId = -1;
        sliderBackground.setVisibility(View.GONE);
        buttonMenu.setVisibility(View.VISIBLE);
        mController.setPositionNoAnimationTo(mSavedPoint);
    }

    private void hideCurrentPlace() {
        mCurrentPlace.hide();
        buttonZoomIn.setVisibility(View.VISIBLE);
        buttonZoomOut.setVisibility(View.VISIBLE);
        buttonCreate.setVisibility(View.VISIBLE);
        buttonCreateMarkerRect.setVisibility(View.INVISIBLE);
    }

    private void showCurrentPlace() {
        mCurrentPlace.show();
        buttonCreateMarkerRect.setVisibility(View.VISIBLE);
        buttonZoomIn.setVisibility(View.INVISIBLE);
        buttonZoomOut.setVisibility(View.INVISIBLE);
        buttonCreate.setVisibility(View.INVISIBLE);
    }

    private void showSliderMarkers(final List<MapQuestMarker> markers) {
        final MarkerPagerAdapter adapter = new MarkerPagerAdapter(this, markers);
        final List<MapQuestMarker> list = adapter.getList();
        if (list.isEmpty())
            return;

        mCurrentPlace.setVisibility(View.GONE);
        MapQuestMarker marker = markers.get(0);
        selectedSlideMarkerId = marker.getId();
        sliderBackground.setVisibility(View.VISIBLE);
        final ViewPager pager = containerSlider.getViewPager();
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setPageMargin(15);
        pager.setClipChildren(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "Page selected : " + position);
                if (position < list.size()) {
                    final MapQuestMarker marker = list.get(position);
                    Log.d(TAG, "MapQuestMarker selected : " + marker);
                    selectedSlideMarkerId = marker.getId();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            setSliderHeightOffset(marker);
                        }
                    };
                    mSliderHandler.postDelayed(runnable, 300);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSavedPoint = mController.getMapCenter();
        setSliderHeightOffset(marker);
        buttonMenu.setVisibility(View.GONE);
    }

    private void setSliderHeightOffset(MapQuestMarker marker) {
        GeoPoint point = new GeoPoint(marker.getLatitude(), marker.getLongitude());
        ScreenPoint screenPoint = mController.getScreenPoint(point);
        float x = screenPoint.getX();
        float y = screenPoint.getY();
        y -= SLIDER_HEIGHT_PADDING;
        screenPoint.setY(y);

        GeoPoint point1 = mController.getGeoPoint(screenPoint);
        mController.setPositionNoAnimationTo(point1);
    }

    private void showCreateMarkerDialog() {
        mHandler.post(mMapControlRunnable);
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
                dialog.dismiss();
                mHandler.post(mMapRunnable);
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mHandler.post(mMapRunnable);
            }
        });
        dialog.findViewById(R.id.ic_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMarker(
                        ((EditText) dialog.findViewById(R.id.edit_title)).getText().toString(),
                        ((EditText) dialog.findViewById(R.id.edit_text)).getText().toString()
                );
                mController.notifyRepaint();
                dialog.dismiss();
                mHandler.post(mMapRunnable);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonCreate.setVisibility(View.GONE);
                    }
                });
            }
        });
        dialog.show();
    }

    public void editMarker(String title, String text, final long id) {
        hideSlider();
        showEditMarkerDialog(title, text, id);
    }

    private void showEditMarkerDialog(String title, String text, final long id) {
        mHandler.post(mMapControlRunnable);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_marker);
        final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
        final EditText editTitle = (EditText) dialog.findViewById(R.id.edit_title);

        editText.setText(text);
        editTitle.setText(title);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.dialog_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(mMapRunnable);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(mMapRunnable);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.ic_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapQuestMarker mapQuestMarker = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadByRowId(id);
                mapQuestMarker.setText(editText.getText().toString());
                mapQuestMarker.setTitle(editTitle.getText().toString());
                DsqApplication.sDaoSession.getMapQuestMarkerDao().update(mapQuestMarker);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createMarker(String title, String text) {
        GeoPoint point = mController.getMapCenter();
        Intent intent = getIntent();
        String action = intent.getAction();
        long id = intent.getLongExtra(EXTRA_SEARCH_ID, -1);
        SearchHistoryItem load = DsqApplication.sDaoSession.getSearchHistoryItemDao().load(id);
        String placeId = load != null ? load.getPlaceId() : "";
        double extraLat = load != null ? load.getLatitude() : 0.0;
        double extraLon = load != null ? load.getLongitude() : 0.0;
        if (action != null && action.equals(ACTION_MOVE_FROM_HISTORY)
                && !TextUtils.isEmpty(placeId) &&
                extraLat == point.getLat() &&
                extraLon == point.getLon()) {
            Log.i(TAG, "saving place id....");
            Place place = new Place(placeId);
            DsqApplication.sDaoSession.getPlaceDao().save(place);
        }
//
        MapQuestMarker marker = new MapQuestMarker(point.getLat(), point.getLon(), title, text);
        Log.i("MAP_TAG", "marker create in lat: " + marker.getLatitude() + " lon:" + point.getLon());
        DsqApplication.sDaoSession.getMapQuestMarkerDao().save(marker);
        List<MapQuestMarker> markers = new ArrayList<>(1);
        markers.add(marker);
        addMarkers(markers);
    }

    private void handleMarkers() {
        List<Overlay> list = mController.getOverlayManager().getOverlays();
        for (Overlay overlay : list) {
            overlay.clearOverlayItems();
        }
        List<MapQuestMarker> markers = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadAll();
        Log.d(TAG, "markers : ");
        for (MapQuestMarker marker : markers) {
            Log.d(TAG, marker.toString());
        }
        addMarkers(markers);
        mController.notifyRepaint();
    }

    private void addMarkers(List<MapQuestMarker> markers) {
        MyOverlay overlay = new MyOverlay(mController);
        for (MapQuestMarker marker : markers) {
            GeoPoint point = new GeoPoint(marker.getLatitude(), marker.getLongitude());
            MyOverlayItem item = new MyOverlayItem(point, mMarkerDrawable);
            item.text = marker.getText();
            item.title = marker.getTitle();
            item.id = marker.getId();
            overlay.addOverlayItem(item);
        }
        mController.getOverlayManager().addOverlay(overlay);
    }

    private void showInfoWindow(int x, int y, String title, String text, final long id) {
        Log.d(TAG, "clicked marker with id : " + id);
        mHandler.removeCallbacks(mMapRunnable);
        buttonCreate.setVisibility(View.INVISIBLE);
        buttonSearch.setVisibility(View.INVISIBLE);
        buttonZoomIn.setVisibility(View.INVISIBLE);
        buttonZoomOut.setVisibility(View.INVISIBLE);
        buttonGpsFix.setVisibility(View.INVISIBLE);
        buttonMenu.setVisibility(View.INVISIBLE);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.marker_info_window);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(ContextCompat.getColor(y, R.color.gray_mild));
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.dialog_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        boolean isTop = false;
        if (y < mScreenHeight / 2) {
            isTop = true;
        }
        RelativeLayout content = (RelativeLayout) dialog.findViewById(R.id.content_window);
        int width = getResources().getDimensionPixelSize(R.dimen.info_window_width);
        int height = getResources().getDimensionPixelSize(R.dimen.info_window_height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = x - width / 2;
        if (params.leftMargin < 0) {
            params.leftMargin = 0;
        }
        if (params.leftMargin > mScreenWidth - width) {
            params.leftMargin = mScreenWidth - width;
        }
        if (isTop) {
            params.topMargin = y;
        } else {
            params.topMargin = y - width;
        }
        content.setLayoutParams(params);
        ((TextView) dialog.findViewById(R.id.text_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.text_text)).setText(text);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.post(mMapRunnable);
            }
        });
        dialog.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AsyncSession session = DsqApplication.sDaoSession.startAsyncSession();
                session.setListener(new AsyncOperationListener() {
                    @Override
                    public void onAsyncOperationCompleted(AsyncOperation operation) {
                        handleMarkers();
                    }
                });
                MapQuestMarker marker = DsqApplication.sDaoSession.getMapQuestMarkerDao().load(id);
                session.deleteInTx(MapQuestMarker.class, marker);
            }
        });
        dialog.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "edit marker " + id);
                String newTitle = ((EditText) dialog.findViewById(R.id.text_title)).getText().toString();
                String newText = ((EditText) dialog.findViewById(R.id.text_text)).getText().toString();
                Log.d(TAG, newTitle + "\n" + newText);
                MapQuestMarker marker = DsqApplication.sDaoSession.getMapQuestMarkerDao().load(id);
                marker.setTitle(newTitle);
                marker.setText(newText);
                AsyncSession session = DsqApplication.sDaoSession.startAsyncSession();
                session.setListener(new AsyncOperationListener() {
                    @Override
                    public void onAsyncOperationCompleted(AsyncOperation operation) {
                        Log.d(TAG, "edit : complete!");
                        handleMarkers();
                    }
                });
                session.updateInTx(MapQuestMarker.class, marker);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void centerPosition() {
        if (checkPermission(this)) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                Log.d(TAG, "Last location : " + mLocation.getLatitude() + ", " + mLocation.getLongitude());
                GeoPoint point = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
                mController.setPositionNoAnimationTo(point);
            }
        }
    }

    private void setMapZoom(int zoom) {
        mZoom = zoom;
        mController.setZoomCurrent(mZoom);
    }

    private void setControlsTrack(boolean hideButtonCreate) {
        hideCurrentPlace();
        mHandler.removeCallbacks(mMapRunnable);
        buttonCreate.setVisibility(hideButtonCreate ? View.GONE : View.VISIBLE);
        buttonCreateMarkerRect.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.GONE);
        buttonZoomIn.setVisibility(View.GONE);
        buttonZoomOut.setVisibility(View.GONE);
        buttonGpsFix.setVisibility(View.GONE);
        buttonMenu.setVisibility(View.GONE);
    }

    private void setControlsUnTrack() {
        hideCurrentPlace();
        buttonCreate.setVisibility(View.VISIBLE);
        buttonCreateMarkerRect.setVisibility(View.GONE);
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

    private void startSearchActivity(String currentPlaceText) {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        double latitude = lastLocation == null ? 0.0 : lastLocation.getLatitude();
        double longitude = lastLocation == null ? 0.0 : lastLocation.getLongitude();

        Intent intent = SearchActivity.buildIntent(this, currentPlaceText, latitude, longitude);
        startActivity(intent);
    }

    private void showDeleteDialog(final long id) {
        if (id == -1) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Удалить маркер?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                AsyncSession session = DsqApplication.sDaoSession.startAsyncSession();
                final MapQuestMarkerDao dao = DsqApplication.sDaoSession.getMapQuestMarkerDao();
                session.setListener(new AsyncOperationListener() {
                    @Override
                    public void onAsyncOperationCompleted(AsyncOperation operation) {
                        dialog.dismiss();
                        mHandler.postDelayed(mMapRunnable, 0);
                        handleMarkers();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideSlider();
                                List<MapQuestMarker> mapQuestMarkers = dao.loadAll();
                                showSliderMarkers(mapQuestMarkers);
                            }
                        });
                    }
                });

                MapQuestMarker marker = dao.load(id);
                session.deleteInTx(MapQuestMarker.class, marker);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHandler.postDelayed(mMapRunnable, 0);
            }
        });
        builder.show();

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
        Intent intent = getIntent();

        if (intent != null && intent.getAction() != null &&
                intent.getAction().equals(ACTION_MOVE_FROM_HISTORY)) {
            mHandler.postDelayed(mSearchRunnable, 0);
            Long id = intent.getLongExtra(EXTRA_SEARCH_ID, -1);
            SearchHistoryItem load = DsqApplication.sDaoSession.getSearchHistoryItemDao().load(id);
            String title = load.getTitle();
            double lat = load.getLatitude();
            double lon = load.getLongitude();
            GeoPoint point = new GeoPoint(lat, lon);
            mController.setPositionNoAnimationTo(point);
            mCurrentPlace.setText(title);

        } else if (intent != null && intent.getAction() != null
                && intent.getAction().equals(ACTION_NOTIFICATION)) {
            handleNotificationMarker(intent);
        } else
            centerPosition();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequestCoarse, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        List<MapQuestMarker> markers = DsqApplication.sDaoSession.getMapQuestMarkerDao().loadAll();
        boolean isNearToMarker = false;
        StringBuilder notificationMessage = new StringBuilder();
        double lat = 0.0, lon = 0.0;
        long id = -1;
        for (MapQuestMarker marker : markers) {
            double distance = Utils.calculateDistance(marker.getLatitude(), marker.getLongitude(), mLocation.getLatitude(), mLocation.getLongitude());
            if (distance < PROXIMITY_THRESHOLD) {
                isNearToMarker = true;
            }
            if (distance < NOTIFICATION_THRESHOLD) {
                lat = marker.getLatitude();
                lon = marker.getLongitude();
                id = marker.getId();
                if (id == _notifyID)
                    continue;
                notificationMessage.append(getString(R.string.distance_to));
                notificationMessage.append(" ");
                notificationMessage.append(marker.getTitle());
                notificationMessage.append(" ");
                notificationMessage.append(mDecimalFormat.format(distance));
                notificationMessage.append(" ");
                notificationMessage.append("m");
                notificationMessage.append("\n");
            }
        }

        if (isNearToMarker && !isHighAccuracyMode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequestAccurate, this);
            isHighAccuracyMode = true;
        } else if (!isNearToMarker && isHighAccuracyMode && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequestCoarse, this);
        }

        if (notificationMessage.length() > 1) {
            notificationMessage.setLength(notificationMessage.length() - 1);
            Notification notification = Notifications.createNotification(this, getString(R.string.notification_title), notificationMessage.toString(), id, lat, lon);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(ID_PROXIMITY_NOTIFICATION, notification);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        Log.e("MAP_TAG", "error:" + errorCode);
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


    private Runnable mMapRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedSlideMarkerId == -1)// no slider
                setControlsUnTrack();
        }
    };


    private Runnable mMapControlRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedSlideMarkerId == -1)// no slider
                setControlsTrack(true);
        }
    };

    private Runnable mMapMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (selectedSlideMarkerId == -1)// no slider
                setControlsTrack(false);
        }
    };

    private Runnable mSearchRunnable = new Runnable() {
        @Override
        public void run() {
            showCurrentPlace();
        }
    };


    private class MyOverlayItem extends OverlayItem {
        public long id;
        public String title;
        public String text;

        public MyOverlayItem(GeoPoint geoPoint, Drawable drawable) {
            super(geoPoint, drawable);
        }
    }

    private class MyOverlay extends Overlay {
        public MyOverlay(MapController mapController) {
            super(mapController);
        }

        @Override
        public boolean onSingleTapUp(float v, float v1) {
            OverlayItem item = a(v, v1);
            if (item != null) {
                MyOverlayItem overlayItem = (MyOverlayItem) item;
                MapQuestMarkerDao mapQuestMarkerDao = DsqApplication.sDaoSession.getMapQuestMarkerDao();
                List<MapQuestMarker> mapQuestMarkers = mapQuestMarkerDao.loadAll();
                MapQuestMarker load = mapQuestMarkerDao.load(overlayItem.id);// extra load for update data
                mapQuestMarkers.remove(load);
                mapQuestMarkers.add(0, load);
                setSliderHeightOffset(load);

                showSliderMarkers(mapQuestMarkers);

            }
            return super.onSingleTapUp(v, v1);
        }
    }

    @Override
    public void onMapActionEvent(MapEvent mapEvent) {
    }
}
