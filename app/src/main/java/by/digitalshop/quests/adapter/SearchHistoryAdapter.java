package by.digitalshop.quests.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import by.digitalshop.quests.MapActivity;
import by.digitalshop.quests.R;
import by.digitalshop.quests.model.Place;
import by.digitalshop.quests.model.SearchHistoryItem;

/**
 * Created by CoolerBy on 11.02.2017.
 */

public class SearchHistoryAdapter extends BaseSearchAdapter<SearchHistoryItem> {
    WeakReference<Activity>mRef;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final List<Place> places;
    private int purpleColor,redColor;

    public SearchHistoryAdapter(Activity a,List<Place>places) {
        this.mRef = new WeakReference<Activity>(a);
        this.places = places;
        purpleColor = ContextCompat.getColor(a,R.color.purple);
        redColor = ContextCompat.getColor(a,R.color.red_light);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = (ViewHolder) holder;
        SearchHistoryItem searchHistoryItem = getItems().get(position);
        String title = searchHistoryItem.getTitle();
        long createDate = searchHistoryItem.getCreateDate();
        String format = dateFormat.format(new Date(createDate));

        h.image.setVisibility(View.GONE);
        h.title.setTextColor(purpleColor);

        final String placeId = searchHistoryItem.getPlaceId();
        if(places.contains(new Place(placeId))){
            h.image.setVisibility(View.VISIBLE);
            h.title.setTextColor(redColor);
        }

        h.title.setText(title);
        h.time.setText(format);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView time;

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView)itemView.findViewById(R.id.marker);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mRef.get() != null){
                        Activity activity = mRef.get();
                        int adapterPosition = getAdapterPosition();
                        final SearchHistoryItem searchHistoryItem = getItems().get(adapterPosition);
                        String placeId = searchHistoryItem.getPlaceId();
                        double latitude = searchHistoryItem.getLatitude();
                        double longitude = searchHistoryItem.getLongitude();
                        activity.startActivity(MapActivity.startIntentMap(activity, latitude, longitude,placeId
                        ));
                    }
                }
            });
        }
    }
}
