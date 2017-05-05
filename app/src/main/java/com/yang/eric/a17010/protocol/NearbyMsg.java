package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.utils.TransformUtils;

/**
 * Created by Yang on 2017/5/3.
 */

public class NearbyMsg {

    //协议号
    private byte type = 0x0A;
    private int number;
    //查找位置
    private int longitude;
    private int latitude;
    //查找半径
    private int radius;

    private byte checkNum;

    public byte[] encode() {
        byte[] bytes = new byte[16];
        int index = 0;
        bytes[index++] = type;
        setNumber(MapsApplication.i++);
        byte[] number = TransformUtils.intTobyte2(getNumber());
        bytes[index++] = number[0];
        bytes[index++] = number[1];
        System.arraycopy(TransformUtils.intTobyte4(getLongitude()), 0, bytes, index , 4);
        index += 4;
        System.arraycopy(TransformUtils.intTobyte4(getLatitude()), 0, bytes, index , 4);
        index += 4;
        System.arraycopy(TransformUtils.intTobyte4(getRadius()), 0, bytes, index , 4);
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[16] = checkNum;
        return bytes;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
