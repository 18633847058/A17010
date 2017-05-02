package com.yang.eric.a17010.protocol;

import com.yang.eric.a17010.utils.TransformUtils;

/**
 * Created by Yang on 2017/4/18.
 */

public class LoginMsg{

    private byte type = 0x01;
    private byte number = 0x01;
    private String ID;
    private String username;
    private String password;
    private int BDID;
    private byte checkNum;


    public byte[] encode() {
        byte[] bytes = new byte[56];
        int index= 0 ;
        bytes[index++] = getType();
        bytes[index++] = 0x00;
        bytes[index++] = getNumber();
        System.arraycopy(getID().getBytes(), 0, bytes, index , getID().getBytes().length);
        index += 16 ;
        System.arraycopy(getUsername().getBytes(), 0, bytes, index , getUsername().getBytes().length);
        index += 16;
        System.arraycopy(getPassword().getBytes(), 0, bytes, index , getPassword().getBytes().length);
        index += 16;
        System.arraycopy(TransformUtils.intTobyte4(getBDID()), 0, bytes, index , 4);
        for (int i = 0; i < bytes.length - 1; i++) {
            checkNum ^= bytes[i];
        }
        bytes[55] = checkNum;
        return bytes;
    }

    public byte getType() {
        return type;
    }

    public byte getNumber() {
        return number;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBDID() {
        return BDID;
    }

    public void setBDID(int BDID) {
        this.BDID = BDID;
    }

    public byte getCheckNum() {
        return checkNum;
    }

    @Override
    public String toString() {
        return "LoginMsg{" +
                "type=" + type +
                ", number=" + number +
                ", ID=" + ID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", BDID=" + BDID +
                ", checkNum=" + checkNum +
                '}';
    }

}
