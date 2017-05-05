package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;

import static com.yang.eric.a17010.utils.Constants.QUERY_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/4/28.
 */

public class QueryResponse {

    private byte type = (byte) 0x84;
    private int number;

    private byte dataType;
    private byte isUpdate;
    private long UUID;
    private int amount;

    private byte checkNum;

    public boolean decode(byte[] bytes) {
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        if (bytes.length == 16 && type == bytes[0] && checkNum == bytes[bytes.length - 1]) {
            setNumber(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,1,3)));
            setDataType(bytes[3]);
            setIsUpdate(bytes[4]);
            setUUID(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes, 5, 13)));
            setAmount(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes, 13, 15)));
            LogUtils.e(QUERY_RESPONSE_DECODE, "decode success!");
            return true;
        } else {
            LogUtils.e(QUERY_RESPONSE_DECODE, "decode failed!");
            return false;
        }
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

    public byte getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(byte isUpdate) {
        this.isUpdate = isUpdate;
    }

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
