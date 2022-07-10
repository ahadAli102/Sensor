package com.ahad.sensor.service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.ahad.sensor.db.DataBaseHelper;
import com.ahad.sensor.db.DatabaseController;
import com.ahad.sensor.db.MySharedPreference;
import com.ahad.sensor.model.SensorData;
import com.ahad.sensor.util.MyNotificationManager;
import com.ahad.sensor.view.MainActivity;
import com.ahad.sensor.R;

import java.util.List;
import java.util.Map;

import static com.ahad.sensor.db.MySharedPreference.ACCELEROMETER_KEY;
import static com.ahad.sensor.db.MySharedPreference.GYROSCOPE_KEY;
import static com.ahad.sensor.db.MySharedPreference.LIGHT_KEY;
import static com.ahad.sensor.db.MySharedPreference.PROXIMITY_KEY;

public class MySensorService extends JobService {
    private static final String TAG = "MyTAG:JobService";
    private boolean isJobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
//        Log.d(TAG, "onStartJob: job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params){

        new Thread(() -> {
            Map<String, SensorData> sensorMap = MySharedPreference.getSensorsValue(getBaseContext());
            SensorData gyro = sensorMap.get(GYROSCOPE_KEY);
            SensorData proximity = sensorMap.get(PROXIMITY_KEY);
            SensorData accelerometer = sensorMap.get(ACCELEROMETER_KEY);
            SensorData light = sensorMap.get(LIGHT_KEY);

            StringBuilder sb = new StringBuilder();
            if(gyro!=null){
                DatabaseController.saveSensorData(gyro);
                String gyroValue = gyro.value;
                sb.append("Gyro "+gyroValue+"\n");
            }
            if(proximity!=null){
                DatabaseController.saveSensorData(proximity);
                String proximityValue = proximity.value;
                sb.append("Proximity "+proximityValue+"\n");
            }
            if(accelerometer!=null){
                DatabaseController.saveSensorData(accelerometer);
                String accelerometerValue = accelerometer.value;
                sb.append("Accelerometer "+accelerometerValue+"\n");
            }
            if(light!=null){
                DatabaseController.saveSensorData(light);
                String lightValue = light.value;
                sb.append("Light "+lightValue+"\n");
            }

            if(!isAppOnForeground(getBaseContext())){
                String title = "Last Sensors value";
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                MyNotificationManager.showNotification(getBaseContext(), title, sb.toString(), intent, 0);
            }
            else {
                MainActivity activity = MainActivity.getReference();
                if(activity!=null)
                    activity.updateSensorsValue();
            }
//            Log.d(TAG, "doBackgroundWork: complete "+System.currentTimeMillis());

            jobFinished(params,false);
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        isJobCancelled = true;
//        Log.d(TAG, "onStopJob: job cancelled before completion");
        return true;
    }
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
//                Log.e("isAppOnForeground",packageName);
                return true;
            }
        }
        return false;
    }

}
