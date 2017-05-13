package by.digitalshop.quests.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CoolerBy on 22.02.2017.
 */

public class PreferenceUtils {
    public static String key = "MyAPP";

    public static void setFirstStart(Context context,boolean f){
        SharedPreferences mPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PreferenceKeys.FIRST_START, f);
        editor.commit();
    }

    public static boolean isFirstStart(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(PreferenceKeys.FIRST_START,true);
    }
}
