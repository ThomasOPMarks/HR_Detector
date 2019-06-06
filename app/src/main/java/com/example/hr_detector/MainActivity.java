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
    private boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all values to 0
        targetHeartRate = 0;
        numClicked = 0;
        currentHeartRate = 0;

        //Set text views, one to display target HR and the other to display current HR
        sTextView = findViewById(R.id.selectHR);
        cTextView = findViewById(R.id.current);

        //Initialize text to display current HR and set to invisible
        cTextView.setText("Current HR: ... - Flaccid");
        cTextView.setVisibility(View.INVISIBLE);

        //Initialize HR sensor, and sensor manager
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //get permission to test HR, if this is the first time the app is being used on this device
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.BODY_SENSORS},
                1);

        //Initialize number picker with values from 40 to 180
        final NumberPicker np = findViewById(R.id.number_picker);
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
                    sTextView.setText("Target HR: " + targetHeartRate);
                    cTextView.setVisibility(View.VISIBLE);
                    np.setVisibility(View.INVISIBLE);
                    next.setText("BACK");
                    numClicked++;
                //If the user has clicked again, go back to first screen to display number picker
                } else{
                    cTextView.setVisibility(View.INVISIBLE);
                    np.setVisibility(View.VISIBLE);
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

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted
                Log.i("permsGranted", "we have permission registering the heartrate sensor");
                mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

            }
            //Otherwise the request was not granted
        }
        else{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    //Handles HR change
    public void onSensorChanged(SensorEvent event){

        if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            currentHeartRate = (int) event.values[0];

            sTextView.setVisibility(View.VISIBLE);
            cTextView.setVisibility(View.VISIBLE);
            boolean currentState = currentHeartRate >= targetHeartRate;
            //If the state has changed a message needs to be sent
            if(currentState != this.state){
                //if the erection needs to be turned on send the message to do so
                if(currentState){
                    //TODO Send a message indicating the erection to turn on


                }else { //otherwise it is the message to turn the erection off
                    //TODO Send a message indicating the erection to turn off

                }
            }
            //Now update the display based on the value of the heart monitor
            if(currentState){//if in the erect state
                sTextView.setText("Target HR: " + targetHeartRate + " - Erect");
                cTextView.setText("Current HR: " + currentHeartRate);
            }
            else{//if in the passive state
                sTextView.setText("Target HR: " + targetHeartRate + " - Flaccid");
                cTextView.setText("Current HR: " + currentHeartRate);
            }
            //update the state to match the current state
            this.state = currentState;


        }

    }

    @Override
    //Handles accuracy change. Currently does nothing.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //log change in sensor accuracy
        Log.i("onAccuracyChanged", "-----------------------------------------------------------------------------------------------onAccuracyChanged - accuracy: " + accuracy);
    }
}

