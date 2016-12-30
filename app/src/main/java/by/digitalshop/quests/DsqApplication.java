package by.digitalshop.quests;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import by.digitalshop.quests.model.DaoMaster;
import by.digitalshop.quests.model.DaoSession;
import by.digitalshop.quests.model.MapQuestMarker;

/**
 * Created by CoolerBy on 27.12.2016.
 */

public class DsqApplication extends Application {
    private static DsqApplication INSTANCE;
    public static DaoSession sDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(this, "notes", null).getWritableDatabase();
        sDaoSession = new DaoMaster(db).newSession();

        List<MapQuestMarker> markers = sDaoSession.getMapQuestMarkerDao().loadAll();
        Log.d("AAAAAAAAAAAAA", markers.toString());
    }

    public static DsqApplication getInstance() {
        return INSTANCE;
    }
}
