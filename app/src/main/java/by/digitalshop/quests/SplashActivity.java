package by.digitalshop.quests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import by.digitalshop.quests.utils.PreferenceUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        setStatusBarColor(Color.BLACK);
        if(!PreferenceUtils.isFirstStart(this)){
            finish();
            startActivity(MapActivity.buildIntent(this));
        }else{
            PreferenceUtils.setFirstStart(this,false);
        }
    }

    public void onStartClick(View w) {
        startActivity(MapActivity.buildIntent(this));
        finish();
    }

    public void onClickMap(View v) {
        startActivity(MapActivity.buildIntent(this));
        finish();
    }
}
