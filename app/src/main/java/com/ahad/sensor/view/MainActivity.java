package com.ahad.sensor.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ahad.sensor.R;
import com.ahad.sensor.db.MySharedPreference;
import com.ahad.sensor.model.SensorData;
import com.ahad.sensor.service.MySensorService;

import static com.ahad.sensor.db.MySharedPreference.ACCELEROMETER_KEY;
import static com.ahad.sensor.db.MySharedPreference.GYROSCOPE_KEY;
import static com.ahad.sensor.db.MySharedPreference.LIGHT_KEY;
import static com.ahad.sensor.db.MySharedPreference.PROXIMITY_KEY;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "MyTAG:MainAct";

    private static MainActivity activity;
    private static final int JOB_ID = 123;

    private SensorManager sensorManager;
    private Sensor lightSensor,proximitySensor,accelerometerSensor,gyroscopeSensor;

    private TextView lightText,proximityText,accelerometerText,gyroscopeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        scheduleJob();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager!=null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
        initViews();
    }
    private void initViews(){
        lightText = findViewById(R.id.light_text_view);
        proximityText = findViewById(R.id.proximity_text_view);
        accelerometerText = findViewById(R.id.accelerometer_text_view);
        gyroscopeText = findViewById(R.id.gyroscope_text_view);
    }

    public void scheduleJob() {
        if(MySharedPreference.isServiceIsRunning(getBaseContext())){
//            Log.d(TAG, "scheduleJob: service running");
            return;
        }
        ComponentName componentName = new ComponentName(this, MySensorService.class);
        JobInfo info = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(1 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(activity, "Notification service has been started", Toast.LENGTH_SHORT).show();
            MySharedPreference.serviceStarted(getBaseContext());
        } else {
            Toast.makeText(activity, "Can not start notification service", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAccelerometerDetails(View view) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(DetailsActivity.DATA_KEY,MySharedPreference.ACCELEROMETER_KEY);
        startActivity(intent);
    }

    public void showProximityDetails(View view) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(DetailsActivity.DATA_KEY, PROXIMITY_KEY);
        startActivity(intent);
    }

    public void showLightDetails(View view) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(DetailsActivity.DATA_KEY,MySharedPreference.LIGHT_KEY);
        startActivity(intent);
    }

    public void showGyroscopeDetails(View view) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(DetailsActivity.DATA_KEY,MySharedPreference.GYROSCOPE_KEY);
        startActivity(intent);
    }

    public static MainActivity getReference(){
        return activity;
    }

    public void updateSensorsValue(){

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        long time = System.currentTimeMillis();
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            String value = ""+event.values[0];
            lightText.setText("Value: "+value);
            MySharedPreference.saveSensorValue(getBaseContext()
                    ,new SensorData(-1,LIGHT_KEY,value,time),LIGHT_KEY);
        }
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            String value = ""+event.values[0];
            proximityText.setText("Value: "+value);
            MySharedPreference.saveSensorValue(getBaseContext()
                    ,new SensorData(-1,PROXIMITY_KEY,value,time),PROXIMITY_KEY);
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String value = "\nX"+x+"\nY"+y+"\nZ"+z;
            accelerometerText.setText("Value: "+value);
            MySharedPreference.saveSensorValue(getBaseContext()
                    ,new SensorData(-1,ACCELEROMETER_KEY,""+x+","+y+","+z,time),ACCELEROMETER_KEY);
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String value = "\nX"+x+"\nY"+y+"\nZ"+z;
            gyroscopeText.setText("Value: "+value);
            MySharedPreference.saveSensorValue(getBaseContext()
                    ,new SensorData(-1,GYROSCOPE_KEY,""+x+","+y+","+z,time),GYROSCOPE_KEY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lightSensor!=null)
            sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        if(proximitySensor!=null)
            sensorManager.registerListener(this,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        if(accelerometerSensor!=null)
            sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        if(gyroscopeSensor!=null)
            sensorManager.registerListener(this,gyroscopeSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(lightSensor!=null)
            sensorManager.unregisterListener(this,lightSensor);
        if(proximitySensor!=null)
            sensorManager.unregisterListener(this,proximitySensor);
        if(accelerometerSensor!=null)
            sensorManager.unregisterListener(this,accelerometerSensor);
        if(gyroscopeSensor!=null)
            sensorManager.unregisterListener(this,gyroscopeSensor);
    }
}