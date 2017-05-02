package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.LogUtils;

import static com.yang.eric.a17010.utils.Constants.LOGIN_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/4/21.
 */

public class Response {

    private byte type = 0x00;
    private byte number = 0x01;
    private byte responseType;
    //0x00 成功 0x01 失败
    private byte result;
    private byte checkNum;

    public boolean decode(byte[] bytes) {
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        if (bytes.length == 6 && type == bytes[0] && checkNum == bytes[bytes.length - 1] && number == bytes[2]) {
            setResponseType(bytes[3]);
            setResult(bytes[4]);
            LogUtils.e(LOGIN_RESPONSE_DECODE, "decode success!");
            return true;
        } else {
            LogUtils.e(LOGIN_RESPONSE_DECODE, "decode failed!");
            return false;
        }
    }
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public byte getResponseType() {
        return responseType;
    }

    public void setResponseType(byte responseType) {
        this.responseType = responseType;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
