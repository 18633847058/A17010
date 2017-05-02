package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 */

public class RequestResponse {
    //协议号
    private byte type = (byte) 0x85;
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
    //本页数量
    private byte dateNumber;
    //内容
    private byte[] content;
    private byte checkNum;

    public boolean decode(byte[] bytes) {
        boolean flag = false;
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        if (type == bytes[0] && checkNum == bytes[bytes.length - 1]) {
            setDataType(bytes[3]);
            setDataPage(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,4,6)));
            setDateNumber(bytes[6]);
            setContent(Arrays.copyOfRange(bytes,7,bytes.length - 1));
            flag = true;
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode success!");
        } else {
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode failed!");
        }
        return flag;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
