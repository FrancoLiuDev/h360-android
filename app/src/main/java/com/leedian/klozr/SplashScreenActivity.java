package com.leedian.klozr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.leedian.klozr.view.activity.ContentListActivity;
import com.leedian.klozr.view.activity.ContentScanActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Intent intent;
        if (AppManager.isUserLogin())
            intent = new Intent(this, ContentListActivity.class);
        else
            intent = new Intent(this, ContentScanActivity.class);
        startActivity(intent);
        finish();
    }
}
