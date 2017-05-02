package com.yang.eric.a17010.protocol.beans;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 */

public class Address {
    //4B 地址本ID 配置的ID唯一标识
    private int id;
    //地址名称长度
    private byte nameLength;
    //地址名称
    private String name;
    //1B 终端类型
    private byte type;
    //4B 北斗地址
    private int BDAddress;
    //2B 组织架构长度
    private int number;
    //组织架构路径
    private String orgAddress;

    public void decode(byte[] bytes){
        try {
            int index = 0;
            setId(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
            index += 4;
            setNameLength(bytes[index++]);
            setName(new String(Arrays.copyOfRange(bytes,index,index + getNameLength())));
            index += getNameLength();
            setType(bytes[index++]);
            setBDAddress(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
            index += 4;
            setNumber(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index+2)));
            index += 2;
            setOrgAddress(new String(Arrays.copyOfRange(bytes,index,index+getNumber())));
            index += getNumber();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode Address success!");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode Address failed!" + e.toString());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getBDAddress() {
        return BDAddress;
    }

    public void setBDAddress(int BDAddress) {
        this.BDAddress = BDAddress;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public void setNameLength(byte nameLength) {
        this.nameLength = nameLength;
    }
}
