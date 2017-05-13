package by.digitalshop.quests.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seatgeek.placesautocomplete.PlacesApi;
import com.seatgeek.placesautocomplete.adapter.AbstractPlacesAutocompleteAdapter;
import com.seatgeek.placesautocomplete.history.AutocompleteHistoryManager;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.Place;

import java.util.Arrays;
import java.util.List;

import by.digitalshop.quests.R;

/**
 * Created by CoolerBy on 12.04.2017.
 */

public class SimplePlaceAdapter extends AbstractPlacesAutocompleteAdapter {

    public SimplePlaceAdapter(@NonNull Context context, @NonNull PlacesApi api, @Nullable AutocompleteResultType autocompleteResultType, @Nullable AutocompleteHistoryManager history) {
        super(context, api, autocompleteResultType, history);
    }

    @Override
    protected View newView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_autocomplete, parent, false);
    }

    @Override
    protected void bindView(View view, Place place) {
        final List<String> split = Arrays.asList(place.description.split(","));
        final StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(split.get(0));
        titleBuilder.append(",");
        titleBuilder.append(split.get(1));
        final StringBuilder desc = new StringBuilder();
        for(int i = 2;i < split.size();i++){
            desc.append(split.get(i));
            if(i != split.size())
            desc.append(",");
        }

        ((TextView) view.findViewById(R.id.title)).setText(titleBuilder.toString());
        ((TextView) view.findViewById(R.id.desciption)).setText(desc.toString());
    }
}
