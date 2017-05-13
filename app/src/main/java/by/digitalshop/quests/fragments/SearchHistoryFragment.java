package by.digitalshop.quests.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.digitalshop.quests.R;
import by.digitalshop.quests.adapter.SearchHistoryAdapter;
import by.digitalshop.quests.model.Place;
import by.digitalshop.quests.model.SearchHistoryItem;

import static by.digitalshop.quests.DsqApplication.sDaoSession;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchHistoryFragment extends BaseSearchFragment {

    public SearchHistoryFragment() {
    }

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_search_history, container, false);
        recyclerView = (RecyclerView) inflate.findViewById(R.id.rRecycle);

        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layout);
        List<SearchHistoryItem> loadAll = sDaoSession.getSearchHistoryItemDao().loadAll();
        List<Place> places = sDaoSession.getPlaceDao().loadAll();

        SearchHistoryAdapter adapter = new SearchHistoryAdapter(getActivity(),places);
        adapter.setItems(loadAll);
        recyclerView.setAdapter(adapter);

        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(R.string.history_search);
    }
}
