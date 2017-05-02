package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
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

    private List<Position> positions;
    //校验值
    private byte checkNum;

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

    public byte getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(byte dataNumber) {
        this.dataNumber = dataNumber;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public byte[] encode(List<Position> positions, byte dataType) {
        byte[] bytes;
        number = 1;
        dataNumber = 0;
        List<Byte> lists = new ArrayList<>();
        lists.add(type);
        byte[] byteNumber = TransformUtils.intTobyte2(MapsApplication.i);
        MapsApplication.i++;
        lists.add(byteNumber[0]);
        lists.add(byteNumber[1]);
        lists.add(dataType);
        for (Position p : positions) {
            String[] s = p.getDate().split("=");
            for (int i = 0; i < s.length; i++) {
                lists.add(Byte.valueOf(s[i]));
            }
            byte[] longitude = TransformUtils.intTobyte4(p.getLongitude());
            for (Byte b: longitude) {
                lists.add(b);
            }
            byte[] latitude = TransformUtils.intTobyte4(p.getLatitude());
            for (Byte b: latitude) {
                lists.add(b);
            }
            byte[] speed = TransformUtils.intTobyte2(p.getSpeed());
            for (Byte b: speed) {
                lists.add(b);
            }
            byte[] direction = TransformUtils.intTobyte2(p.getDirection());
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
}
