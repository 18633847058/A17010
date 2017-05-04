package com.yang.eric.a17010.protocol.config;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TransformUtils;

import java.util.Arrays;
import java.util.List;

import static com.yang.eric.a17010.utils.Constants.REQUEST_RESPONSE_DECODE;

/**
 * Created by Yang on 2017/5/2.
 * 打卡信息
 */

public class Punch {

    //8B 配置的ID唯一标识
    private long id;
    //1B 上班配置数
    private byte workNumber;
    //上班配置N
    private List<Config>  workConfig;

    //1B 下班配置数
    private byte leaveNumber;
    //下班配置N
    private List<Config>  leaveConfig;
    //上班周期
    private byte[] workTime = new byte[7];
    //打卡阈值  有效半径 单位为米 2字节
    private int radius;

    public void decode(byte[] bytes) {
        try {
            int index = 0;
            setId(TransformUtils.byte8ToLong(Arrays.copyOfRange(bytes,index,index + 8)));
            index += 8;
            setWorkNumber(bytes[index++]);
            for (int i = 0; i < workNumber; i++) {
                Config config = new Config();
                config.setH(bytes[index++]);
                config.setM(bytes[index++]);
                config.setLongitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                config.setLatitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                workConfig.add(config);
            }
            setLeaveNumber(bytes[index++]);
            for (int i = 0; i < leaveNumber; i++) {
                Config config = new Config();
                config.setH(bytes[index++]);
                config.setM(bytes[index++]);
                config.setLongitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                config.setLatitude(TransformUtils.byte4ToInt(Arrays.copyOfRange(bytes,index,index + 4)));
                index += 4;
                leaveConfig.add(config);
            }
            setWorkTime(Arrays.copyOfRange(bytes,index,7));
            index += 7;
            setRadius(TransformUtils.byte2ToInt(Arrays.copyOfRange(bytes,index,index + 2)));
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode punch success!");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(REQUEST_RESPONSE_DECODE, "decode punch failed!" + e.toString());
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(byte workNumber) {
        this.workNumber = workNumber;
    }

    public List<Config> getWorkConfig() {
        return workConfig;
    }

    public void setWorkConfig(List<Config> workConfig) {
        this.workConfig = workConfig;
    }

    public byte getLeaveNumber() {
        return leaveNumber;
    }

    public void setLeaveNumber(byte leaveNumber) {
        this.leaveNumber = leaveNumber;
    }

    public List<Config> getLeaveConfig() {
        return leaveConfig;
    }

    public void setLeaveConfig(List<Config> leaveConfig) {
        this.leaveConfig = leaveConfig;
    }

    public byte[] getWorkTime() {
        return workTime;
    }

    public void setWorkTime(byte[] workTime) {
        this.workTime = workTime;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private class Config{
        // 时间 2B 时分各占一个字节
        private byte h;
        private byte m;
        //经纬度 4B 有符号整数 单位0.1秒
        private int longitude;
        private int latitude;

        public byte getH() {
            return h;
        }

        public void setH(byte h) {
            this.h = h;
        }

        public byte getM() {
            return m;
        }

        public void setM(byte m) {
            this.m = m;
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
    }
}
