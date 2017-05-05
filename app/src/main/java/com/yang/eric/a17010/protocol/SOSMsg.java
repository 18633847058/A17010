package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.utils.TransformUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Yang on 2017/5/4.
 */

public class SOSMsg {

    //协议号   信息总长13 + length
    private byte type = 0x0B;
    //序列号
    private int number;
    private int longitude;
    private int latitude;

    //描述长度
    private byte length;
    //告警信息
    private String describe;
    private byte checkNum;

    private byte[] encode() {
        byte[] bytes = new byte[13 + getLength()];
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
        bytes[index++] = getLength();
        try {
            System.arraycopy(getDescribe().getBytes("gb2312"), 0, bytes, index , getLength());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[bytes.length - 1] = checkNum;
        return bytes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
        try {
            setLength(((byte) describe.getBytes("gb2312").length));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }

    public static void main(String[] args) {
        System.out.print(new SOSMsg().getLength());
    }
}
