package com.yang.eric.a17010.protocol;

/**
 * Created by Yang on 2017/4/28.
 */

public class RequestMsg {
    //协议号
    private byte type = 0x05;
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
    private int dataPage;
    private byte dateNumber;
    private byte checkNum;
}
