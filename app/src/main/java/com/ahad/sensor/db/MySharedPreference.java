package com.ahad.sensor.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.ahad.sensor.model.SensorData;
import com.ahad.sensor.util.ObjectSerializerHelper;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPreference {
    public static final String SERVICE_KEY = "service";
    public static final String GYROSCOPE_KEY = "Gyroscope";
    public static final String LIGHT_KEY = "Light";
    public static final String PROXIMITY_KEY = "Proximity";
    public static final String ACCELEROMETER_KEY = "Accelerometer";
    public static final String MY_PREFS_NAME = "service_shared_preference";

    public static boolean isServiceIsRunning(Context context){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(SERVICE_KEY, false);
    }
    public static void serviceStarted(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(SERVICE_KEY, true);
        editor.apply();
    }

    public static void saveSensorValue(Context context,SensorData sensor,String key){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, ObjectSerializerHelper.objectToString(sensor));
        editor.apply();
    }
    public static Map<String,SensorData> getSensorsValue(Context context){
        Map<String,SensorData> sensorMap= new HashMap<>();
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        String gyro = prefs.getString(GYROSCOPE_KEY,null);
        String accelerometer = prefs.getString(ACCELEROMETER_KEY,null);
        String proximity = prefs.getString(PROXIMITY_KEY,null);
        String light = prefs.getString(LIGHT_KEY,null);


        if(gyro!=null)
            sensorMap.put(GYROSCOPE_KEY, (SensorData) ObjectSerializerHelper.stringToObject(gyro));
        if(accelerometer!=null)
            sensorMap.put(ACCELEROMETER_KEY, (SensorData) ObjectSerializerHelper.stringToObject(accelerometer));
        if(proximity!=null)
            sensorMap.put(PROXIMITY_KEY, (SensorData) ObjectSerializerHelper.stringToObject(proximity));
        if(light!=null)
            sensorMap.put(LIGHT_KEY, (SensorData) ObjectSerializerHelper.stringToObject(light));

        return sensorMap;

    }
}
