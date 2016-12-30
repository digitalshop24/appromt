package by.digitalshop.quests.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.digitalshop.quests.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchPopularFragment extends BaseSearchFragment {

    public SearchPopularFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_search_popular, container, false);
        return inflate;
    }
}
