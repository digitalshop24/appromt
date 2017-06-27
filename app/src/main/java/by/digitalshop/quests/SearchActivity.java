package by.digitalshop.quests;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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
import by.digitalshop.quests.model.SearchHistoryItem;
import se.walkercrou.places.AddressComponent;
import se.walkercrou.places.GooglePlaces;

public class SearchActivity extends BaseActivity {
    private final static String  EXTRA_LAT = "EXTRA_LAT";
    private final static String  EXTRA_LON = "EXTRA_LON";
    private final static String  EXTRA_TEXT = "EXTRA_TEXT";
    private PlacesAutocompleteTextView autocompleteTextView;

    public static Intent buildIntent(Context context,String text,double lat,double lon){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TEXT,text);
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
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(resize(getResources().getDrawable(R.drawable.ic_back)));

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        HistoryPagerAdapter  mPagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);

        View viewById = findViewById(R.id.places_autocomplete);

        autocompleteTextView = (PlacesAutocompleteTextView) viewById;
        autocompleteTextView.setResultType(AutocompleteResultType.ADDRESS);
        autocompleteTextView.setLanguageCode("ru");
        autocompleteTextView.setRadiusMeters(2000L);
        autocompleteTextView.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red_light),
                PorterDuff.Mode.SRC_ATOP);
        autocompleteTextView.setText(getIntent().getStringExtra(EXTRA_TEXT));

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
                autocompleteTextView.append(" ");
                autocompleteTextView.setSelection(s.length()+1);
                final Runnable target =  new Runnable() {
                    @Override
                    public void run() {

                        GooglePlaces googlePlaces = new GooglePlaces(("AIzaSyDTDqVGIHTL4JyRgrV7rdHEYq0HNE59aek"));
                        se.walkercrou.places.Place placeById = googlePlaces.getPlaceById(place.place_id);

                        boolean hasStreet = false;
                        List<AddressComponent> addressComponents = placeById.getAddressComponents();
                        for (AddressComponent component: addressComponents) {
                            List<String> types = component.getTypes();
                            if(types.contains("street_number")){
                                hasStreet = true;
                            }
                        }
                        title = place.description;
                        placeID = place.place_id;

                        mLat = placeById.getLatitude();
                        mLon = placeById.getLongitude();
                        SearchHistoryItem searchItem = new SearchHistoryItem(mLat,mLon,title,placeID,
                                new Date().getTime());

                        DsqApplication.sDaoSession.getSearchHistoryItemDao().save(searchItem);
                        final Long id =  DsqApplication.sDaoSession.getSearchHistoryItemDao().getKey(searchItem);

                        final Runnable showDropDown = new Runnable() {
                            @Override
                            public void run() {
                                autocompleteTextView.showDropDown();
                            }
                        };

                        final Runnable moveToStreet = new Runnable() {
                            @Override
                            public void run() {
                                startActivity(MapActivity.startIntentMap(SearchActivity.this,id));
                            }
                        };

                        final Runnable action;
                        if(hasStreet) {
                            action = moveToStreet;
                        }else{
                            action = showDropDown;
                        }
                        runOnUiThread(action);
                    }
                };
                new Thread(target).start();
            }
        });
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int d = (int) getResources().getDimension(R.dimen.scale_search_back_icon);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, d, d, false);
        return new BitmapDrawable(getResources(), bitmapResized);
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
            case R.id.action_cancel:
                autocompleteTextView.setText("");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    private class HistoryPagerAdapter extends FragmentStatePagerAdapter {

        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SearchHistoryFragment();
//                case 1:
//                    return new SearchPopularFragment();
                default:
                    return null;
            }
        }
    }
}
