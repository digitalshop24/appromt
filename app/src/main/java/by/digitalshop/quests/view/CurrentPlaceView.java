package by.digitalshop.quests.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import by.digitalshop.quests.R;

/**
 * Created by CoolerBy on 26.05.2017.
 */

public class CurrentPlaceView extends LinearLayout {

    private final TextView tvCurrentPlace;
    private final View imCancel;

    public CurrentPlaceView(Context context) {
        super(context);
        tvCurrentPlace = null;
        imCancel = null;
    }

    public CurrentPlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);


        LayoutInflater from = LayoutInflater.from(getContext());
        View inflate = from.inflate(R.layout.view_search_currentplace, this,false);
        tvCurrentPlace = (TextView) inflate.findViewById(R.id.tvCurrentPlace);
        imCancel = inflate.findViewById(R.id.action_close_place);
        addView(inflate);
        hide();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        tvCurrentPlace.setOnClickListener(l);
        imCancel.setOnClickListener(l);
    }

    public void setText(String text){
        tvCurrentPlace.setText(text);
    }

    public void show(){
        setVisibility(VISIBLE);
    }

    public void hide(){
      setVisibility(INVISIBLE);
    }

    public TextView getCurrentPlaceText() {
        return tvCurrentPlace;
    }
}
