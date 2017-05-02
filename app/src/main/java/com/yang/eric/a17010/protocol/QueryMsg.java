package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.utils.TransformUtils;

/**
 * Created by Yang on 2017/4/28.
 */

public class QueryMsg {
    //协议号
    private byte type = 0x04;
    //序列号
    private int number;
    //数据类型
    // 0x01：打卡配置
    // 0x02：巡检路线
    // 0x03：观测对象类型
    // 0x04：观测对象
    // 0x05：围栏信息
    // 0x06：自定义POI信息
    // 0x07：地址本
    // 0x08：公告信息
    private byte dataType;
    //本地数据库版本的UUID
    private long UUID;
    //校验和
    private byte checkNum;

    public byte[] encode(byte dataType) {
        byte[] bytes = new byte[13];
        int index = 0;
        System.arraycopy(TransformUtils.intTobyte2(MapsApplication.i++), 0, bytes, index , 2);
        index += 2;
        bytes[index++] = dataType;
        System.arraycopy(TransformUtils.longTobyte8(getUUID()), 0, bytes, index , 8);
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[13] = checkNum;
        return bytes;
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

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }
}
