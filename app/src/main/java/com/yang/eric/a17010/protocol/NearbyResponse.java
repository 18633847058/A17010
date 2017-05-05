package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.beans.Nearby;
import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.NEARBY_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/4.
 */

public class NearbyResponse {

    //协议号
    private byte type = (byte) 0x8A;
    //应答序列号
    private int number;

    //数量
    private byte dataTotal;
    //友邻位置
    private List<Nearby> nearbies = new ArrayList<>();

    private byte checkNum;

    public boolean decode(byte[] bytes) {
        boolean flag = false;
        for (int i = 0; i < bytes.length; i++) {
            checkNum ^= bytes[i];
        }
        if (type == bytes[0] && checkNum == bytes[bytes.length - 1]) {
            try {
                int index = 1;
                setNumber(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
                index += 2;
                setDataTotal(bytes[index++]);
                for (int i = 0; i < getDataTotal(); i++) {
                    Nearby nearby = new Nearby();
                    nearby.setId(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                    index += 4;
                    nearby.setLongitude(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                    index += 4;
                    nearby.setLatitude(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                    index += 4;
                    nearby.setInterval(bytes[index++]);
                    nearbies.add(nearby);
                }
                flag = true;
                LogUtils.e(NEARBY_RESPONSE_DECODE, "decode success!");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(NEARBY_RESPONSE_DECODE, "decode failed!");
            }
        } else {
            LogUtils.e(NEARBY_RESPONSE_DECODE, "decode failed!");
        }
        return flag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public byte getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(byte dataTotal) {
        this.dataTotal = dataTotal;
    }

    public List<Nearby> getNearbies() {
        return nearbies;
    }
}
