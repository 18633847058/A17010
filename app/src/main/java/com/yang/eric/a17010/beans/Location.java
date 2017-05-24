package com.yang.eric.a17010.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yang on 2017/5/23.
 */
@Entity
public class Location  {

    //自身id
    @Id(autoincrement = true)
    private Long id;

    @Transient
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");

    private String time;
    //定位类型
    private int type;
    //有符号整数
    private int latitude;
    private int longitude;
    private int altitude;
    private int speed;
    private int direction;
    private int upload;
    public Location() {
        this.time = simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }
    @Generated(hash = 99455632)
    public Location(Long id, String time, int type, int latitude, int longitude,
            int altitude, int speed, int direction, int upload) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.direction = direction;
        this.upload = upload;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getLatitude() {
        return this.latitude;
    }
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
    public int getLongitude() {
        return this.longitude;
    }
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
    public int getAltitude() {
        return this.altitude;
    }
    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }
    public int getSpeed() {
        return this.speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getDirection() {
        return this.direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public int getUpload() {
        return this.upload;
    }
    public void setUpload(int upload) {
        this.upload = upload;
    }
   
}
