package by.digitalshop.quests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.digitalshop.quests.fragments.SearchHistoryFragment;

public class SearchActivity extends BaseActivity {

    private static final String[] COUNTRIES = new String[] { "Belgium",
            "France", "France_", "Italy", "Germany", "Spain" };
    final String ATTRIBUTE_NAME_TEXT = "text";

    public static Intent buildIntent(Context context){
        return new Intent(context,SearchActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setStatusBarColor(getResources().getColor(R.color.purple));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        HistoryPagerAdapter  mPagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);

        // упаковываем данные в понятную для адаптера структуру
        final ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                COUNTRIES.length);
        Map<String, Object> m;
        for (int i = 0; i < COUNTRIES.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TEXT, COUNTRIES[i]);
            data.add(m);
        }
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_TEXT };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { android.R.id.text1 };

        // создаем адаптер
        List<String> strings = Arrays.asList(COUNTRIES);
        CustomerAdapter sAdapter = new CustomerAdapter(this,R.layout.view_search_dropdown, new ArrayList<String>(strings));

//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                 R.layout.view_search_dropdown, COUNTRIES);
       final AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.searchView);

        textView.setAdapter(sAdapter);

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
            return new SearchHistoryFragment();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }
    public class CustomerAdapter extends ArrayAdapter<String> {
        private final String MY_DEBUG_TAG = "CustomerAdapter";
        private ArrayList<String> items;
        private ArrayList<String> itemsAll;
        private ArrayList<String> suggestions;
        private int viewResourceId;

        public CustomerAdapter(Context context, int viewResourceId, ArrayList<String> items) {
            super(context, viewResourceId, items);
            this.items = items;
            this.itemsAll = (ArrayList<String>) items.clone();
            this.suggestions = new ArrayList<String>();
            this.viewResourceId = viewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(viewResourceId, null);
            }
            String t = items.get(position);
            if (t != null) {
                TextView customerNameLabel = (TextView) v.findViewById(android.R.id.text1);
                if (customerNameLabel != null) {
                    customerNameLabel.setText(t);
                }
            }
            return v;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        final Filter nameFilter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                String str = ((String)(resultValue));
                return str;
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if(constraint != null) {
                    suggestions.clear();
                    for (String t : itemsAll) {
                        if(t.toLowerCase().startsWith(constraint.toString().toLowerCase())){
                            suggestions.add(t);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<String> filteredList = (ArrayList<String>) results.values;
                if(results != null && results.count > 0) {
                    clear();
                    for (String c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }
}
