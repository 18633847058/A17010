package com.yang.eric.a17010.protocol.config;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 * 巡检信息
 */

public class Inspection {

    //8B 配置的ID唯一标识
    private long id;
    //路线名称长度
    private byte nameLength;
    // 路线名称
    private String name;
    //2B 偏移距离告警阈值
    private int offset;
    //2B 巡检轨迹的数量
    private int number;
    //巡检轨迹信息
    private List<Position> positions;

    public void decode(byte[] bytes) {
        try {
            int index = 0;
            setId(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes,index,index + 8)));
            index += 8;
            setNameLength(bytes[index++]);
            setName(new String(Arrays.copyOfRange(bytes,index,index + getNameLength())));
            index += getNameLength();
            setOffset(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
            index += 2;
            setNumber(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
            index += 2;
            for (int i = 0; i < number; i++) {
                Position p = new Position();
                p.setLongitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                p.setLatitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                positions.add(p);
                LogUtils.e(REQUEST_RESPONSE_DECODE, "decode Inspection success!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode Inspection failed!" + e.toString());
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public void setNameLength(byte nameLength) {
        this.nameLength = nameLength;
    }
}
