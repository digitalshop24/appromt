package by.digitalshop.quests;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

import by.digitalshop.quests.model.DaoMaster;
import by.digitalshop.quests.model.DaoSession;
import by.digitalshop.quests.utils.CustomExceptionHandler;

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



        File crashReport = new File(Environment.getExternalStorageDirectory(),"MapQuestErrors"); // TODO remove from google play store version
        if(!crashReport.exists()){
            crashReport.mkdir();
        }

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                crashReport.getAbsolutePath(), ""));
    }

    public static DsqApplication getInstance() {
        return INSTANCE;
    }
}
