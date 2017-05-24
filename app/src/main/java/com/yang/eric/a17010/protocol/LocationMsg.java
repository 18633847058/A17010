package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.Location;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/4/28.
 */

public class LocationMsg {
    //协议号  1字节
    private byte type = 0x03;
    //序列号  2字节
    private int number;
    //数据类型  1字节 0x00：实时上传  0x01：历史补传
    private byte dataType;
    //位置数量  1字节 最多10个
    private byte dataNumber;
    //位置集合
    private ArrayList<Location> locations = new ArrayList<>();
    //校验值
    private byte checkNum;

    public byte[] encode(ArrayList<Location> locations, byte dataType) {
        byte[] bytes;
        List<Byte> lists = new ArrayList<>();
        lists.add(type);
        number = MapsApplication.i ++;
        byte[] byteNumber = TransformUtils.intTobyte2(number);
        lists.add(byteNumber[0]);
        lists.add(byteNumber[1]);
        this.dataType = dataType;
        lists.add(dataType);
        dataNumber = (byte) locations.size();
        lists.add(dataNumber);
        for (Location l : locations) {
            this.locations.add(l);
            lists.add((byte) l.getType());
            String[] s = l.getTime().split("-");
            for (int i = 0; i < s.length; i++) {
                lists.add(Byte.valueOf(s[i]));
            }
            byte[] longitude = TransformUtils.intTobyte4(l.getLongitude());
            for (Byte b: longitude) {
                lists.add(b);
            }
            byte[] latitude = TransformUtils.intTobyte4(l.getLatitude());
            for (Byte b: latitude) {
                lists.add(b);
            }
            byte[] altitude = TransformUtils.intTobyte2(l.getAltitude());
            for (Byte b: altitude) {
                lists.add(b);
            }
            byte[] speed = TransformUtils.intTobyte2(l.getSpeed());
            for (Byte b: speed) {
                lists.add(b);
            }
            byte[] direction = TransformUtils.intTobyte2(l.getDirection());
            for (Byte b: direction) {
                lists.add(b);
            }
        }
        bytes = new byte[lists.size() + 1];
        for (int i = 0; i < lists.size(); i++) {
            bytes[i] = lists.get(i);
            checkNum ^= lists.get(i);
        }
        bytes[lists.size()] = checkNum;
        return bytes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public byte getDataType() {
        return dataType;
    }
}
