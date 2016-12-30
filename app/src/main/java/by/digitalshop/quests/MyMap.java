package by.digitalshop.quests;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.greenrobot.eventbus.EventBus;

import by.digitalshop.quests.event.MapMotionEvent;
import ru.yandex.yandexmapkit.MapView;

/**
 * Created by CoolerBy on 26.12.2016.
 */

public class MyMap extends MapView {
    public MyMap(Context context, String s) {
        super(context, s);
    }

    public MyMap(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction()==MotionEvent.ACTION_DOWN || ev.getAction()==MotionEvent.ACTION_UP){
            EventBus.getDefault().post(new MapMotionEvent(ev.getAction()));
        }
        return super.dispatchTouchEvent(ev);
    }
}
