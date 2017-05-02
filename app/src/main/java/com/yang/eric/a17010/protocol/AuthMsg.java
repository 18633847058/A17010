package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.TransformUtils;

/**
 * Created by Yang on 2017/4/21.
 * 鉴权消息
 */

public class AuthMsg {
    //协议号
    private byte type = 0x02;
    //序列号
    private byte number = 0x01;
    //用户名
    private String username;
    //设备号
    private long UUID;
    //北斗ID
    private int BDID;
    //校验值
    private byte checkNum;

    public byte[] encode() {
        byte[] bytes = new byte[32];
        int index= 0 ;
        bytes[index++] = getType();
        bytes[index++] = 0x00;
        bytes[index++] = getNumber();
        System.arraycopy(getUsername().getBytes(), 0, bytes, index , getUsername().getBytes().length);
        index += 16 ;
        System.arraycopy(TransformUtils.longTobyte8(getUUID()), 0, bytes, index , 8);
        index += 8;
        System.arraycopy(TransformUtils.intTobyte4(getBDID()), 0, bytes, index , 4);
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[31] = checkNum;
        return bytes;
    }

    public byte getType() {
        return type;
    }

    public byte getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }

    public int getBDID() {
        return BDID;
    }

    public void setBDID(int BDID) {
        this.BDID = BDID;
    }
}
