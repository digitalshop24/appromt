package by.digitalshop.quests;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesApi;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.history.AutocompleteHistoryManager;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.Place;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import by.digitalshop.quests.adapter.SimplePlaceAdapter;
import by.digitalshop.quests.fragments.SearchHistoryFragment;
import by.digitalshop.quests.fragments.SearchPopularFragment;
import by.digitalshop.quests.model.SearchHistoryItem;
import se.walkercrou.places.GooglePlaces;

public class SearchActivity extends BaseActivity {
    private final static String  EXTRA_LAT = "EXTRA_LAT";
    private final static String  EXTRA_LON = "EXTRA_LON";

    public static Intent buildIntent(Context context,double lat,double lon){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_LAT,lat);
        intent.putExtra(EXTRA_LON,lon);
        return intent;
    }

    private double mLat,mLon;
    private String title,placeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setStatusBarColor(getResources().getColor(R.color.purple));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        HistoryPagerAdapter  mPagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);

        View viewById = findViewById(R.id.places_autocomplete);

        final PlacesAutocompleteTextView autocompleteTextView = (PlacesAutocompleteTextView) viewById;
        autocompleteTextView.setResultType(AutocompleteResultType.ADDRESS);
        autocompleteTextView.setLanguageCode("ru");
        autocompleteTextView.setRadiusMeters(2000L);

        autocompleteTextView.setLocationBiasEnabled(true);
        double lat = getIntent().getDoubleExtra(EXTRA_LAT, 0.0);
        double lon = getIntent().getDoubleExtra(EXTRA_LON, 0.0);

        Location targetLocation = new Location("");
        targetLocation.setAccuracy(1);
        targetLocation.setLatitude(lat);//your coords of course
        targetLocation.setLongitude(lon);

        PlacesApi api = autocompleteTextView.getApi();
        AutocompleteHistoryManager historyManager = autocompleteTextView.getHistoryManager();

        final SimplePlaceAdapter adapter = new SimplePlaceAdapter(this,api,
                AutocompleteResultType.ADDRESS,historyManager);
        autocompleteTextView.setAdapter(adapter);

        autocompleteTextView.setCurrentLocation(targetLocation);
        autocompleteTextView.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull final Place place) {
                final List<String> split = Arrays.asList(place.description.split(","));
                String s = split.get(0);
                autocompleteTextView.setText(s);

                final Runnable target = new Runnable() {
                    @Override
                    public void run() {
                        GooglePlaces googlePlaces = new GooglePlaces(("AIzaSyDTDqVGIHTL4JyRgrV7rdHEYq0HNE59aek"));
                        se.walkercrou.places.Place placeById = googlePlaces.getPlaceById(place.place_id);

                        title = place.description;
                        placeID = place.place_id;

                        mLat = placeById.getLatitude();
                        mLon = placeById.getLongitude();
                    }
                };
                new Thread(target).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_done:
                if(TextUtils.isEmpty(placeID)) {
                    Log.e("TAG","place id is empty");
                    return false;
                }
                startActivity(MapActivity.startIntentMap(this,mLat,mLon,placeID));
                // TODO check selected fragment
                // save to db
                SearchHistoryItem searchItem = new SearchHistoryItem(mLat,mLon,title,placeID,new Date().getTime());
                DsqApplication.sDaoSession.getSearchHistoryItemDao().save(searchItem);

        }
        return false;
    }

    private class HistoryPagerAdapter extends FragmentStatePagerAdapter {

        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SearchHistoryFragment();
                case 1:
                    return new SearchPopularFragment();
                default:
                    return null;
            }
        }
    }
}
