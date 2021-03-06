package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.utils.TransformUtils;

/**
 * Created by Yang on 2017/4/28.
 */

public class RequestMsg {
    //协议号
    private byte type = 0x05;
    //序列号
    private int number;
    //数据类型
    // 0x01：打卡配置 1
    // 0x02：巡检路线 1
    // 0x03：观测对象类型 1
    // 0x04：观测对象 10
    // 0x05：围栏信息  5
    // 0x06：自定义POI信息 5
    // 0x07：地址本 10
    // 0x08：公告信息 1
    private byte dataType;
    //数据页数  从1开始
    private int dataPage;
    //每页数量
    private byte dateNumber;
    private byte checkNum;

    public byte[] encode() {
        byte[] bytes = new byte[8];
        int index = 0;
        bytes[index++] = type;
        setNumber(MapsApplication.i);
        System.arraycopy(TransformUtils.intTobyte2(MapsApplication.i++), 0, bytes, index , 2);
        index += 2;
        bytes[index++] = dataType;
        System.arraycopy(TransformUtils.intTobyte2(getDataPage()), 0, bytes, index , 2);
        index += 2;
        bytes[index++] = dateNumber;
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[8] = checkNum;
        return bytes;
    }
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public int getDataPage() {
        return dataPage;
    }

    public void setDataPage(int dataPage) {
        this.dataPage = dataPage;
    }

    public byte getDateNumber() {
        return dateNumber;
    }

    public void setDateNumber(byte dateNumber) {
        this.dateNumber = dateNumber;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
