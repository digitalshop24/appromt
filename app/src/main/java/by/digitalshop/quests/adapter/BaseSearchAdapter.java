package by.digitalshop.quests.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by CoolerBy on 06.02.2017.
 */

public abstract class BaseSearchAdapter<T> extends RecyclerView.Adapter {
    private List<T> items;


    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
