package by.digitalshop.quests.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.digitalshop.quests.R;
import by.digitalshop.quests.model.MapQuestMarker;

/**
 * Created by CoolerBy on 13.01.2017.
 */

public class MarkerPagerAdapter extends PagerAdapter {
    private List<MapQuestMarker> mMapQuestMarkers;
    private LayoutInflater mLayoutInflater;

    public MarkerPagerAdapter(Context context, List<MapQuestMarker> markers) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMapQuestMarkers = markers;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.fragment_slide, container, false);
        container.addView(view);
        MapQuestMarker marker = mMapQuestMarkers.get(position);
        ((TextView) view.findViewById(R.id.text_title)).setText(marker.getTitle());
        ((TextView) view.findViewById(R.id.text_text)).setText(marker.getText());
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mMapQuestMarkers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public List<MapQuestMarker> getList() {
        return mMapQuestMarkers;
    }
}
