package com.yang.eric.a17010.beans;

/**
 * Created by Yang on 2017/5/3.
 */

public class PunchInfo {
    //8B ID
    private long id;
    //打卡类型 1上班  0下班
    private byte type;
    //打卡地点 经纬度
    private int longitude;
    private int latitude;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
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
    //是否合法
    private byte right;
    //非法原因长度
    private byte length;
    //非法原因描述
    private String describe;
}
