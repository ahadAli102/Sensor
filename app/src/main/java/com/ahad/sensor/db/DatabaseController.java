package com.ahad.sensor.db;

import com.ahad.sensor.App;
import com.ahad.sensor.model.SensorData;

import java.util.List;

public class DatabaseController {
    private static DataBaseHelper dataBaseHelper;
    static {
        dataBaseHelper = App.getDataBaseHelper();
    }

    public static long saveSensorData( SensorData data){
        long id = -1;
        id = dataBaseHelper.insert(data);
        return id;
    }
    public static List<SensorData> getSensorData(String type){
        return dataBaseHelper.show(type);
    }
}
