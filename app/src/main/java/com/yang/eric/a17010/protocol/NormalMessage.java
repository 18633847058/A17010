package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.MESSAGE_DECODE;

/**
 * Created by Yang on 2017/5/3.
 */

public class NormalMessage {

    //或者是0x87 协议号
    private byte type = 0x07;
    //序列号 2B
    private int number;
    //信息长度
    private byte length;
    //信息内容
    private String content;
    //校验和
    private byte checkNum;

    public byte[] encode(){
        List<Byte> list = new ArrayList<>();
        list.add(getType());
        byte[] number = TransformUtils.intTobyte2(MapsApplication.i++);
        list.add(number[0]);
        list.add(number[1]);
        byte[] content = getContent().getBytes();
        list.add((byte) content.length);
        for (byte b :
                content) {
            list.add(b);
        }
        byte[] bytes = new byte[list.size() + 1];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
            checkNum ^= list.get(i);
        }
        bytes[list.size()] = checkNum;
        return bytes;
    }

    public void decode(byte[] bytes){
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        if (type == bytes[0] && checkNum == bytes[bytes.length - 1]) {
            setLength(bytes[3]);
            setContent(new String(Arrays.copyOfRange(bytes,4,4 + getLength())));
            LogUtils.e(MESSAGE_DECODE, "decode success!");
        } else {
            LogUtils.e(MESSAGE_DECODE, "decode failed!");
        }
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

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(byte checkNum) {
        this.checkNum = checkNum;
    }
}
