package by.digitalshop.quests.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import by.digitalshop.quests.R;

/**
 * Created by CoolerBy on 23.05.2017.
 */

public class CreateMarkerRect extends RelativeLayout {
    public CreateMarkerRect(Context context) {
        super(context);
    }

    public CreateMarkerRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater from = LayoutInflater.from(getContext());
        View inflate = from.inflate(R.layout.view_create_marker_rect, this,false);
        addView(inflate);
    }
}
