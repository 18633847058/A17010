package com.yang.eric.a17010.beans;

/**
 * Created by Yang on 2017/5/4.
 */

public class Nearby {

    private int id;
    private int longitude;
    private int latitude;
    //时间差
    private byte interval;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public byte getInterval() {
        return interval;
    }

    public void setInterval(byte interval) {
        this.interval = interval;
    }
}
