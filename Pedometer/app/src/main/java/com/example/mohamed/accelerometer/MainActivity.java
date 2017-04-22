package com.example.mohamed.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {


    private TextView acceleration, steps2second, distanceText, speedtext, steps;
    private Button show;
    private EditText heightText;

    private SensorManager sm;
    private Sensor accelerometer;

    private List<Double> list;
    private List<Double> bigList;

    private Boolean running = false;

    private long startTime, endTime;
    private double time, height, stride, speed, distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acceleration = (TextView) findViewById(R.id.Acceleration);
        steps = (TextView) findViewById(R.id.Steps);
        show = (Button) findViewById(R.id.show);
        steps2second = (TextView) findViewById(R.id.steps2sec);
        heightText = (EditText) findViewById(R.id.height);
        distanceText = (TextView) findViewById(R.id.distance);
        speedtext = (TextView) findViewById(R.id.speed);

        // Initialize Accelerometer sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        show.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (!running) {
                    running = true;
                    list = new ArrayList<Double>();
                    bigList = new ArrayList<Double>();
                    show.setText("Show result");
                    startTime = System.currentTimeMillis();
                    distance = 0;
                    distanceText.setText("Distance: " + distance + " m");
                    speed = 0;
                    speedtext.setText("Speed: " + speed + " m/s");
                    steps.setText("#Steps: " + 0 );
                } else {
                    running = false;
                    show.setText("Start");
                    steps.setText("#Steps: " + getSteps(bigList));
                    distanceText.setText("Distance: " + distance + " m");
                }
            }

        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            acceleration.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
            // calculate the magnitude mag^2 = x^2 + y^2 + z^2 and add mag to the list
            // we deal with mag due to count steps in all directions as magnitude neglects directions.
            double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(y, 2));
            list.add(mag);
            bigList.add(mag);
            endTime = System.currentTimeMillis();
            time = (endTime - startTime) / 1000.0;
            if (time >= 2.0) {
                int stepsInTwoSeconds = getSteps(this.list);
                steps2second.setText("#Steps in 2 second: " + stepsInTwoSeconds);
                height = Double.valueOf(heightText.getText().toString())/100;
                if (stepsInTwoSeconds > 0 && stepsInTwoSeconds <= 2) {
                    stride = height / 5;
                } else if (stepsInTwoSeconds > 2 && stepsInTwoSeconds <= 3) {
                    stride = height / 4;
                } else if (stepsInTwoSeconds > 3 && stepsInTwoSeconds <= 4) {
                    stride = height / 3;
                } else if (stepsInTwoSeconds > 4 && stepsInTwoSeconds <= 5) {
                    stride = height / 2;
                } else if (stepsInTwoSeconds > 5 && stepsInTwoSeconds <= 6) {
                    stride = height / 1.2;
                } else if (stepsInTwoSeconds > 6 && stepsInTwoSeconds < 8) {
                    stride = height;
                } else if (stepsInTwoSeconds >= 8) {
                    stride = 1.2 * height;
                }
                distance += stepsInTwoSeconds * stride;
                speed = stepsInTwoSeconds * stride / 2.0;
                speedtext.setText("Speed: " + speed + " m/s");
                startTime = endTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int getSteps(List<Double> list) {
        StatisticsUtil su = new StatisticsUtil();
        double mean = su.findMean(list);
        double std = su.standardDeviation(list, mean);
        int stepsNumber = su.finAllPeaks(list, std);
        list.clear();
        return stepsNumber;
    }
}