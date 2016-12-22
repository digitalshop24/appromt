package by.digitalshop.quests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        setStatusBarColor(Color.BLACK);
    }

    public void onStartClick(View w) {
        startActivity(SearchActivity.buildIntent(this));
    }

    public void onClickMap(View v) {
        startActivity(MapActivity.buildIntent(this));
    }
}
