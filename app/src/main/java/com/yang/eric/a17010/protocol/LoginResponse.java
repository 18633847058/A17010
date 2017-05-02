package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;

import static com.yang.eric.a17010.utils.Constants.LOGIN_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/4/19.
 */

public class LoginResponse {

    private byte type = (byte) 0x81;
    private byte number = 0x01;

    private byte result;
    private int remoteID;

    private long UUID;
    private byte checkNum;

    public boolean decode(byte[] bytes) {
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        if (bytes.length == 17 && type == bytes[0] && checkNum == bytes[bytes.length - 1] && number == bytes[2]) {
            setResult(bytes[3]);
            setRemoteID(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes, 4, 8)));
            setUUID(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes, 8, 16)));
            LogUtils.e(LOGIN_RESPONSE_DECODE, "decode success!");
            return true;
        } else {
            LogUtils.e(LOGIN_RESPONSE_DECODE, "decode failed!");
            return false;
        }
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public int getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(int remoteID) {
        this.remoteID = remoteID;
    }

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }

    public byte getCheckNum() {
        return checkNum;
    }

}
