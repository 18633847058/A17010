package com.yang.eric.a17010.protocol.config;

import java.text.SimpleDateFormat;

/**
 * Created by Yang on 2017/4/28.
 * 定位信息
 */

public class Position {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
    //ID
    private long id;
    //未知类型
    private byte type;
    //年月日时分秒
    private String date;
    //经度 有符号整数 单位0.1秒
    private int longitude;
    //纬度
    private int latitude;
    //速度 单位0.1米/秒
    private int speed;
    //方向 以北为方向单位度
    private int direction;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
