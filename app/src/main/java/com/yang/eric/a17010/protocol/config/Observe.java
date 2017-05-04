package com.yang.eric.a17010.protocol.config;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 */

public class Observe {
    //8B 配置的ID唯一标识
    private long id;
    private byte nameLength;
    //观测对象名称
    private String name;
    //观测对象经纬度
    private int longitude;
    private int latitude;
    //观测对象参数数量
    private byte number;
    //观测对象类型信息ID
    private List<Integer> ids;

    public void decode(byte[] bytes) {
        try {
            int index = 0;
            setId(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes,index,index + 8)));
            index += 8;
            setNameLength(bytes[index++]);
            setName(new String(Arrays.copyOfRange(bytes,index,index + getNameLength())));
            index += getNameLength();
            setLongitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
            index += 4;
            setLatitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
            index += 4;
            setNumber(bytes[index++]);
            for (int i = 0; i < getNumber(); i++) {
                ids.add(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
                index += 2;
            }
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode observe success!");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode observe failed!" + e.toString());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public void setNameLength(byte nameLength) {
        this.nameLength = nameLength;
    }
}
