package com.yang.eric.a17010.protocol.beans;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 */

public class ObservedType {
    //8B 配置的ID唯一标识
    private long id;
    //
    private byte nameLength;
    //
    private String name;
    //1B 观测对象的参数数量
    private byte number;

    private List<Observer> observers;

    private void decode(byte[] bytes) {
        try {
            int index = 0;
            setId(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes,index,index + 8)));
            index += 8;
            setNameLength(bytes[index++]);
            setName(new String(Arrays.copyOfRange(bytes,index,index + getNameLength())));
            index += getNameLength();
            setNumber(bytes[index++]);
            for (int i = 0; i < number; i++) {
                Observer o = new Observer();
                o.setId(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
                index += 2;
                o.setControl(bytes[index++]);
                o.setType(bytes[index++]);
                o.setDecribeNumber(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
                index += 2;
                o.setBytes(Arrays.copyOfRange(bytes,index,index + o.getDecribeNumber()));
                index += o.getDecribeNumber();
                observers.add(o);
                LogUtils.e(REQUEST_RESPONSE_DECODE, "decode observedType success!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode deservedType failed!" + e.toString());
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

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public void setObservers(List<Observer> observers) {
        this.observers = observers;
    }

    public byte getNameLength() {
        return nameLength;
    }

    public void setNameLength(byte nameLength) {
        this.nameLength = nameLength;
    }

    public class Observer {
        private int id;
        //1B 0:上报参数 1:控制参数
        private byte control;
        //1B 类型
        // 0x00：开关
        // 数据长度1B
        // 取值：开/关
        // 0x01：无符号整数
        // 数据长度4B
        // 取值：0-4294967295
        // 0x02：有符号整数
        // 数据长度4B
        // 取值：-2147483648～2147483647
        // 0x03：浮点数
        // 数据长度8B
        // 取值：浮点数取值范围
        // 0x04：整数数据选项值
        // 数据长度1B
        // 取值：在描述列表中选择
        // 0x05：浮点数数据选项值
        // 数据长度1B
        // 取值：在描述列表中选择
        // 0x06：语言描述选项值
        // 数据长度1B
        // 取值：在描述列表中选择
        private byte type;
        //描述长度
        private int decribeNumber;
        //描述信息
        private byte[] bytes;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public byte getControl() {
            return control;
        }

        public void setControl(byte control) {
            this.control = control;
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }

        public int getDecribeNumber() {
            return decribeNumber;
        }

        public void setDecribeNumber(int decribeNumber) {
            this.decribeNumber = decribeNumber;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}
