package com.ahad.sensor.model;

import java.io.Serializable;

public class SensorData implements Serializable {
    public int id;
    public String name;
    public String value;
    public long time;

    public SensorData(int id, String name, String value, long time) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.time = time;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("id:"+ id)
                .append(" name:"+ name)
                .append(" value:"+ value)
                .append(" time:"+ time)
                .toString();
    }
}
