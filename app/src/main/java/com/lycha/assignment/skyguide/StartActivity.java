package com.lycha.assignment.skyguide;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {
    private boolean run = true;
    ImageView imageViewSky;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        imageViewSky = (ImageView)findViewById(R.id.imageViewSky);

        ActionBar actionBar = getActionBar();
        if(actionBar!=null)
            actionBar.hide();

        android.support.v7.app.ActionBar actionBarSup = getSupportActionBar();

        if(actionBarSup!=null)
            actionBarSup.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), CameraViewActivity.class);
                run = false;
                startActivity(intent);
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!run)
            finish();
    }
}
