package com.ahad.sensor;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ahad.sensor.db.DataBaseHelper;

public class App extends Application {
    private static DataBaseHelper dataBaseHelper;
    private static final String TAG = "MyTAG:App";
    @Override
    public void onCreate() {
        super.onCreate();
        dataBaseHelper = new DataBaseHelper(getBaseContext());
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
//        Log.d(TAG, "onCreate: "+sqLiteDatabase.toString());
    }

    public static DataBaseHelper getDataBaseHelper() {
        return dataBaseHelper;
    }
}
