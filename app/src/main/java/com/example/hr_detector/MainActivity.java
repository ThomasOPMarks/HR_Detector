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
    private int targetHeartRate;
    private int numClicked;
    private int currentHeartRate;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all values to 0
        targetHeartRate = 0;
        numClicked = 0;
        currentHeartRate = 0;

        //Set text views, one to display target HR and the other to display current HR
        sTextView = (TextView) findViewById(R.id.selectHR);
        cTextView = (TextView) findViewById(R.id.current);

        //Initialize text to display current HR and set to invisible
        cTextView.setText("Current Heart Rate: ...");
        cTextView.setVisibility(View.INVISIBLE);

        //Initialize HR sensor, and sensor manager
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //get permission to test HR, if this is the first time the app is being used on this device
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.BODY_SENSORS},
                1);

        //Initialize number picker with values from 40 to 180
        final NumberPicker np = (NumberPicker) findViewById(R.id.number_picker);
        np.setMinValue(40);
        np.setMaxValue(180);

        //Listener for the number picker. Sets target HR to selected value
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                targetHeartRate = newVal;
            }
        });

        //Next/Back button
        final Button next = findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //If the user has clicked here, the target HR is set up, show current HR
                if(numClicked == 0){
                    sTextView.setText("Target Heart Rate: " + targetHeartRate);
                    cTextView.setVisibility(v.VISIBLE);
                    np.setVisibility(v.INVISIBLE);
                    next.setText("BACK");
                    numClicked++;
                //If the user has clicked again, go back to first screen to display number picker
                } else{
                    cTextView.setVisibility(v.INVISIBLE);
                    //cTextView.setText("Current Heart Rate: ...");
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
    //Requests permission to read HR, because it is sensitive information
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
    //Handles HR change
    public void onSensorChanged(SensorEvent event){

        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            String msg = "" + (int)event.values[0];
            currentHeartRate = (int) event.values[0];
            sTextView.setText("Target Heart Rate: " + targetHeartRate);
            cTextView.setText("Current Heart Rate: " + currentHeartRate);
            sTextView.setVisibility(View.VISIBLE);
            cTextView.setVisibility(View.VISIBLE);
            if (currentHeartRate >= targetHeartRate) { //Target HR has been achieved
                //Display message showing Target HR is reached, in the future send signal for Bluno to turn on
                cTextView.setText("Signal bluetooth device that the mood has been achieved!!");
                currentHeartRate = 0;
                //Turn off HR monitor to save battery
                mSensorManager.unregisterListener(this,mHeartRateSensor);
            }
        }

    }

    @Override
    //Handles accuracy change. Currently does nothing.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //log change in sensor accuracy
        Log.i("onAccuracyChanged", "-----------------------------------------------------------------------------------------------onAccuracyChanged - accuracy: " + accuracy);
    }
}

