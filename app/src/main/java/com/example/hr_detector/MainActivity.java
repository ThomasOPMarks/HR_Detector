package com.example.hr_detector;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.util.Log;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private TextView cTextView;
    private int target_heart_rate;
    private int numClicked;
    private int current_heart_rate;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    //private SensorEventListener sensorEventListener;


    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            current_heart_rate = (int) event.values[0]; //this is the certainty, not the actual HR value?
        }

        String TAG = "tag";
        Log.i(TAG, "--------------------------");
        Log.i(TAG, "message");
        Log.i(TAG, "" + event.sensor.getType());
        Log.i("live", "--------------");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing for now

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        target_heart_rate = 0;
        numClicked = 0;
        current_heart_rate = 0;

        mTextView = (TextView) findViewById(R.id.selectHR);
        cTextView = (TextView) findViewById(R.id.current);

        cTextView.setText("Current Heart Rate: " + current_heart_rate);
        cTextView.setVisibility(View.INVISIBLE);


        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);



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
                    np.setVisibility(v.INVISIBLE);
                    mTextView.setText("Target Heart Rate: " + target_heart_rate);
                    cTextView.setVisibility(v.VISIBLE);
                    next.setText("BACK");
                    numClicked++;
                } else{
                    cTextView.setText("Current Heart Rate: " + current_heart_rate);
                    cTextView.setVisibility(v.INVISIBLE);
                    np.setVisibility(v.VISIBLE);
                    mTextView.setText("Select Heart Rate");
                    next.setText("NEXT");
                    numClicked--;
                }

            }
        });







        // Enables Always-on
        setAmbientEnabled();
    }
}

