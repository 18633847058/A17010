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
        byte[] data = new byte[2];
        data[1] = (byte) (i & 0xFF);
        data[0] = (byte) ((i >> 8) & 0xFF);
        return data;
    }
    public static byte[] intTobyte4(int i) {
        byte[] data = new byte[4];
        data[3] = (byte) (i & 0xFF);
        data[2] = (byte) ((i >> 8) & 0xFF);
        data[1] = (byte) ((i >> 16) & 0xFF);
        data[0] = (byte) ((i >> 24) & 0xFF);
        return data;
    }
    public static byte[] longTobyte8(long num) {
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) {
            data[i] = (byte)(num >>>(56-(i*8)));
        }
        return data;
    }
}
