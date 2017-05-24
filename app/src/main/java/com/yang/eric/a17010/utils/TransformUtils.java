package com.yang.eric.a17010.utils;

/**
 * Created by Yang on 2017/4/19.
 */

public class TransformUtils {

    public static int byte2ToInt(byte[] bytes) {
        int length =  ((bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF);
        return length;
    }
    public static int byte4ToInt(byte[] bytes) {
        int length =  ((bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF) ;
        return length;

//        int value;
//        value =  ((bytes[0] & 0xFF)<<24)
//                |((bytes[1] & 0xFF)<<16)
//                |((bytes[2] & 0xFF)<<8)
//                |(bytes[3] & 0xFF);
//        return value;
    }
    public static long byte8ToLong(byte[] bytes) {
        long temp;
        long res = 0;
        for (int i = 0; i < 8; i++) {
            res <<= 8;
            temp = bytes[i] & 0xFF;
            res |= temp;
        }
        return res;
    }

    public static byte[] intTobyte2(int i) {
        int tmp = Math.abs(i);
        byte[] data = new byte[2];
        data[1] = (byte) (tmp & 0xFF);
        data[0] = (byte) ((tmp >> 8) & 0xFF);
        if (i<0) {
            data[0] |= 0x80;
        }
        return data;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] intTobyte4(int i) {
        int tmp = Math.abs(i);
        byte[] data = new byte[4];
        data[3] = (byte) (tmp & 0xFF);
        data[2] = (byte) ((tmp >> 8) & 0xFF);
        data[1] = (byte) ((tmp >> 16) & 0xFF);
        data[0] = (byte) ((tmp >> 24) & 0xFF);
        if (i<0) {
            data[0] |= 0x80;
        }
        return data;
    }
    public static byte[] longTobyte8(long num) {
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) {
            data[i] = (byte)(num >>>(56-(i*8)));
        }
        return data;
    }

    public static void main(String[] args) {
        System.out.println(intTobyte2(-36000));
    }
}
