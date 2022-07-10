package com.ahad.sensor.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ahad.sensor.R;
import com.ahad.sensor.db.DatabaseController;
import com.ahad.sensor.model.SensorData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ahad.sensor.db.MySharedPreference.LIGHT_KEY;
import static com.ahad.sensor.db.MySharedPreference.PROXIMITY_KEY;

public class DetailsActivity extends AppCompatActivity {
    public static final String DATA_KEY="key123";
    private static final String TAG = "MyTAG:Details";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    private Toolbar toolbar;
    private TextView textView;
    private List<BarEntry> barEntries;
    private BarChart barChart;
    private BarDataSet barDataSet1, barDataSet2,barDataSet3;

    private List<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String sensor = getIntent().getStringExtra(DATA_KEY);
//        Log.d(TAG, "onCreate: "+sensor);
        Toast.makeText(this, sensor, Toast.LENGTH_SHORT).show();
        init();
        toolbar.setTitle(sensor);
        setSupportActionBar(toolbar);
        if(sensor.equals(LIGHT_KEY) || sensor.equals(PROXIMITY_KEY)){
            showOneBarGraph(sensor);
        }
        else{
            showThreeBarGraph(sensor);
        }
    }
    private void init(){
        barChart = findViewById(R.id.details_bar_chart);
        toolbar = findViewById(R.id.details_toolbar);
        textView = findViewById(R.id.details_text);
        barEntries = new ArrayList<>();
    }

    private void showOneBarGraph(String sensor){
//        Log.d(TAG, "showOneBarGraph: called");
        List<Float> values = loadSensorValue(sensor);
        if (values.size() == 0){
            textView.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        }
        barDataSet1 = new BarDataSet(getBarEntries(values), "Sensor Data");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.teal));
        BarData data = new BarData(barDataSet1);
        setProperty(data,false,dates);
    }
    private void showThreeBarGraph(String sensor){
//        Log.d(TAG, "showThreeBarGraph: called");
        List<Map<String,Float>> values = loadSensorValues(sensor);
        if (values.size() == 0){
            textView.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        }
        barDataSet1 = new BarDataSet(getBarEntries(values,ONE), "Sensor X data");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.gray));
        barDataSet2 = new BarDataSet(getBarEntries(values,TWO), "Sensor Y data");
        barDataSet2.setColor(getApplicationContext().getResources().getColor(R.color.silver));
        barDataSet3 = new BarDataSet(getBarEntries(values,THREE), "Sensor Z data");
        barDataSet3.setColor(getApplicationContext().getResources().getColor(R.color.teal));

        BarData data = new BarData(barDataSet1, barDataSet2,barDataSet3);
        setProperty(data,true, dates);
    }

    private void setProperty(BarData data,boolean group,List<String> days){
//        Log.d(TAG, "setProperty: called");
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
        float barSpace = 0.00f;
        float groupSpace = 0.1f;
        data.setBarWidth(0.30f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.animate();
        if(group)
            barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }

    private List<Float> loadSensorValue(String sensor){
//        Log.d(TAG, "loadSensorValue: called");
        List<SensorData> sensorData = DatabaseController.getSensorData(sensor);
        List<Float> values = new ArrayList<>();
        dates = new ArrayList<>();
        for (SensorData data : sensorData) {
            values.add(Float.parseFloat(data.value));
//            Log.d(TAG, "loadSensorValue: "+data);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data.time);
            dates.add(cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.HOUR_OF_DAY)+" at "+cal.get(Calendar.DAY_OF_MONTH+1)+":"+cal.get(Calendar.MONTH)+":"+cal.get(Calendar.YEAR));
        }
        Toast.makeText(this, sensor+" has "+values.size()+" values", Toast.LENGTH_SHORT).show();
        return values;
    }
    private List<Map<String,Float>> loadSensorValues(String sensor){
//        Log.d(TAG, "loadSensorValues: called");
        List<SensorData> sensorData = DatabaseController.getSensorData(sensor);
        List<Map<String,Float>> values = new ArrayList<>();
        dates = new ArrayList<>();
        for (SensorData data : sensorData) {
//            Log.d(TAG, "loadSensorValues: "+data);
            Map<String,Float> map = new HashMap<>();
            String[] numbers = data.value.split(",");
            map.put(ONE,Float.parseFloat(numbers[0]));
            map.put(TWO,Float.parseFloat(numbers[1]));
            map.put(THREE,Float.parseFloat(numbers[2]));
            values.add(map);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data.time);
            dates.add(cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.HOUR_OF_DAY)+" at "+cal.get(Calendar.DAY_OF_MONTH+1)+":"+cal.get(Calendar.MONTH)+":"+cal.get(Calendar.YEAR));
        }
        Toast.makeText(this, sensor+" has "+sensorData.size()+" values", Toast.LENGTH_SHORT).show();
        return values;
    }


    private List<BarEntry> getBarEntries(List<Float> values) {
//        Log.d(TAG, "getBarEntries: called");
        barEntries = new ArrayList<>();
        int count = 1;
        for (Float f: values) {
            barEntries.add(new BarEntry(count, f));
            count++;
        }
        return barEntries;
    }
    private List<BarEntry> getBarEntries(List<Map<String,Float>> values,String position) {
//        Log.d(TAG, "getBarEntries: called");
        barEntries = new ArrayList<>();
        int count = 1;
        for (Map<String,Float> map: values) {
            if(map.get(ONE)!=null){
                barEntries.add(new BarEntry(count, map.get(position)));
                count++;
            }
        }
        return barEntries;
    }
}