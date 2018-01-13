package com.lycha.assignment.skyguide;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    TextView textViewSet, textViewCurrent;
    ImageView imageViewSkyS;
    SeekBar seekBarAccuracy;
    private SharedPreferences prefereces;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        prefereces = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefereces.edit();

        imageViewSkyS = (ImageView)findViewById(R.id.imageViewSkyS);
        textViewSet = (TextView)findViewById(R.id.textViewSet);
        textViewCurrent =(TextView)findViewById(R.id.textViewCurrent);
        textViewCurrent.setText("Current accuracy is "+(prefereces.getInt("Accuracy",5)) );
        seekBarAccuracy = (SeekBar)findViewById(R.id.seekBarAccuracy);
        seekBarAccuracy.setProgress(prefereces.getInt("Accuracy",5)-5);

        seekBarAccuracy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewCurrent.setText("Current accuracy is "+(seekBar.getProgress()+ 5) );
                editor.putInt("Accuracy",seekBar.getProgress() + 5);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
