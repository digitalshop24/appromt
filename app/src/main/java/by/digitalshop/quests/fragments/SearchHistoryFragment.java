package by.digitalshop.quests.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.digitalshop.quests.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchHistoryFragment extends BaseSearchFragment {

    public SearchHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_search_history, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(R.string.history_search);
    }
}
