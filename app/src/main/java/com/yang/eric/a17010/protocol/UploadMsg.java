package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.PunchInfo;
import com.yang.eric.a17010.protocol.config.Position;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yang.eric.a17010.MapsApplication.i;


/**
 * Created by Yang on 2017/5/3.
 */

public class UploadMsg {
    //协议号
    private byte type = 0x05;
    //序列号
    private int number;
    //数据类型
//    0x01：打卡申请
//    0x02：观测数据上传
//    0x03：巡检轨迹上传
//    0x04：图像上传
    private byte dataType;
    //数据总包数 2B
    private int dataTotal;
    //数据包号 2B
    private int dataNumber;
    //数据内容
    private byte[] content;
    //校验值
    private byte checkNum;

    public List<byte[]> encode(List<Object> objects, byte dataType) {
        List<byte[]> list = new ArrayList<>();
        this.dataType = dataType;
        if (getDataType() == 0x01) {
            //打卡信息上传
            List<Byte> byteList = new ArrayList<>();
            PunchInfo punchInfo = (PunchInfo) objects.remove(0);
            byteList.add(type);
            byte[] byteNumber = TransformUtils.intTobyte2(i);
            i++;
            byteList.add(byteNumber[0]);
            byteList.add(byteNumber[1]);
            byteList.add(dataType);
            setDataTotal(1);
            byteList.add((byte) 0x00);
            byteList.add((byte) 0x01);
            setDataNumber(1);
            byteList.add((byte) 0x00);
            byteList.add((byte) 0x01);
            byte[] id = TransformUtils.longTobyte8(punchInfo.getId());
            for (Byte b: id) {
                byteList.add(b);
            }
            byteList.add(punchInfo.getType());
            byte[] longitude = TransformUtils.intTobyte4(punchInfo.getLongitude());
            for (Byte b: longitude) {
                byteList.add(b);
            }
            byte[] latitude = TransformUtils.intTobyte4(punchInfo.getLatitude());
            for (Byte b: latitude) {
                byteList.add(b);
            }
            byte[] bytes = new byte[byteList.size() + 1];
            for (int i = 0; i < byteList.size(); i++) {
                bytes[i] = byteList.get(i);
                checkNum ^= byteList.get(i);
            }
            bytes[byteList.size()] = checkNum;
            list.add(bytes);
        } else if (getDataType() == 0x03) {
            //巡检轨迹信息
            List<Byte> byteList = null;
            setDataTotal(
                    (objects.size()%36 ==0)?objects.size()/36 : (objects.size()/36 + 1));
            for (int i = 0; i < objects.size(); i++) {
                if ( i%36 == 0 ) {
                    if (null != byteList) {
                        byte[] bytes = new byte[byteList.size() + 1];
                        for (int j = 0; j < byteList.size(); j++) {
                            bytes[j] = byteList.get(j);
                            checkNum ^= byteList.get(j);
                        }
                        bytes[byteList.size()] = checkNum;
                        list.add(bytes);
                    }
                    byteList = new ArrayList<>();
                    byteList.add(type);
                    byte[] byteNumber = TransformUtils.intTobyte2(MapsApplication.i);
                    MapsApplication.i++;
                    byteList.add(byteNumber[0]);
                    byteList.add(byteNumber[1]);
                    byteList.add(dataType);
                    byte[] total = TransformUtils.intTobyte2(getDataTotal());
                    byteList.add(total[0]);
                    byteList.add(total[1]);
                    setDataNumber(i/36 + 1);
                    byte[] number = TransformUtils.intTobyte2(getDataNumber());
                    byteList.add(number[0]);
                    byteList.add(number[1]);
                }
                Position position = (Position) objects.get(i);
                byte[] id = TransformUtils.longTobyte8(position.getId());
                for (Byte b: id) {
                    byteList.add(b);
                }
                String[] s = position.getDate().split("-");
                for (int j = 0; j < s.length; j++) {
                    byteList.add(Byte.valueOf(s[i]));
                }
                byte[] longitude = TransformUtils.intTobyte4(position.getLongitude());
                for (Byte b: longitude) {
                    byteList.add(b);
                }
                byte[] latitude = TransformUtils.intTobyte4(position.getLatitude());
                for (Byte b: latitude) {
                    byteList.add(b);
                }
            }
            if (byteList.size()%36 != 0) {
                byte[] bytes = new byte[byteList.size() + 1];
                for (int j = 0; j < byteList.size(); j++) {
                    bytes[j] = byteList.get(j);
                    checkNum ^= byteList.get(j);
                }
                bytes[byteList.size()] = checkNum;
                list.add(bytes);
            }
        } else if (getDataType() == 0x04) {
            //图片上传



        }
        return list;
    }


    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
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

    public int getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(int dataTotal) {
        this.dataTotal = dataTotal;
    }

    public int getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
