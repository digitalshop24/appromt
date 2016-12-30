package by.digitalshop.quests.event;

/**
 * Created by CoolerBy on 27.12.2016.
 */
public class MapMotionEvent {
    private int mAction;

    public MapMotionEvent(int action) {
        mAction = action;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

    @Override
    public String toString() {
        return "MapMotionEvent{" +
                "mAction=" + mAction +
                '}';
    }
}
