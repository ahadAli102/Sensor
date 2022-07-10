package com.ahad.sensor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ahad.sensor.model.SensorData;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="sensor.db";
    public static final String TABLE_NAME ="sensor_info";
    public static final String ID ="_id";
    public static final String NAME ="name";
    public static final String TIME ="time";
    public static final String VALUE ="value";
    public static final String createTableQuery=" CREATE TABLE  "+ TABLE_NAME +" ( "+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT , "
             + NAME +" VARCHAR(30), "+ TIME +" INTEGER, "+ VALUE +" VARCHAR(40) ); ";
    public static final String upgradeTableQuery = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    public static final String GET_DATA_QUERY = "SELECT * FROM "+ TABLE_NAME+" ORDER BY "+ID+" DESC";
    public static final int versionNo=2;
    private Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, versionNo);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(createTableQuery);
            Toast.makeText(context, "table is created", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Table is not created", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(upgradeTableQuery);
            onCreate(db);
            Toast.makeText(context, "table is upgraded", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "table is upgraded", Toast.LENGTH_SHORT).show();
        }

    }

    public long insert(SensorData data){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,data.name);
        contentValues.put(VALUE,data.value);
        contentValues.put(TIME,data.time);
        long value = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return value;

    }
    public List<SensorData> show(String type){
        List<SensorData> data = new ArrayList<>();
        String sql = GET_DATA_QUERY;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Long time = cursor.getLong(2);
            String value = cursor.getString(3);
            if(name.equals(type))
                data.add(new SensorData(id,name,value,time));
        }
        return data;
    }

}

