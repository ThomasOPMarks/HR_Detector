package com.example.hr_detector;

import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView sTextView;
    private TextView cTextView;
    private int target_heart_rate;
    private int numClicked;
    private int current_heart_rate;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        target_heart_rate = 0;
        numClicked = 0;
        current_heart_rate = 0;

        sTextView = (TextView) findViewById(R.id.selectHR);
        cTextView = (TextView) findViewById(R.id.current);

        cTextView.setText("Current Heart Rate: " + current_heart_rate);
        cTextView.setVisibility(View.INVISIBLE);


        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.BODY_SENSORS},
                1);
        final NumberPicker np = (NumberPicker) findViewById(R.id.number_picker);

        np.setMinValue(40);
        np.setMaxValue(180);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                target_heart_rate = newVal;
            }
        });

        final Button next = findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(numClicked == 0){
                    sTextView.setText("Target Heart Rate: " + target_heart_rate);
                    np.setVisibility(v.INVISIBLE);
                    cTextView.setVisibility(v.VISIBLE);
                    next.setText("BACK");
                    numClicked++;
                } else{
                    cTextView.setText("Current Heart Rate: " + current_heart_rate);
                    cTextView.setVisibility(v.INVISIBLE);
                    np.setVisibility(v.VISIBLE);
                    sTextView.setText("Select Heart Rate");
                    next.setText("NEXT");
                    numClicked--;
                }

            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("got event", "onRequestPermissionsResult: " + requestCode);

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    Log.i("permsGranted", "we have permission registering the heartrate sensor");
                    mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event){

        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            String msg = "" + (int)event.values[0];
            current_heart_rate = (int) event.values[0];
            cTextView.setText("Current Heart Rate: " + current_heart_rate);
            sTextView.setVisibility(View.INVISIBLE);
            cTextView.setVisibility(View.VISIBLE);
            if (current_heart_rate >= target_heart_rate) {
                cTextView.setText("Signal bluetooth device that the mood has been achieved!!");
                //probably turn the heart rate monitor off here
                current_heart_rate = 0;
                mSensorManager.unregisterListener(this,mHeartRateSensor);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing for now
        Log.i("onAccuracyChanged", "-----------------------------------------------------------------------------------------------onAccuracyChanged - accuracy: " + accuracy);
    }
}

