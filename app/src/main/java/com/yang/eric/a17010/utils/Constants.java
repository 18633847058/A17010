package com.yang.eric.a17010.utils;

import android.os.Environment;

/**
 * Created by Yang on 2017/4/11.
 */

public class Constants {

    public static final String SETTINGS = "user_settings";
    public static final String ISLOGIN = "is_login";
    public static final String IMEI = "IMEI";
    public static final String COM_YANG_ERIC_A17010_LOCAL_BROADCAST = "com.yang.eric.a17010.LOCAL_BROADCAST";
    public static final String SERVER_IP = "192.168.1.210";
    public static final String SERVER_PORT = "15001";
    public static final byte[] HEAD_BYTES = new byte[] { 0x78, 0x78 };
    public static final byte[] TAIL_BYTES = new byte[] { 0x0D, 0x0A };

    public static final String LOGIN_RESPONSE_DECODE = "LoginResponse decode";
    public static final String QUERY_RESPONSE_DECODE = "QueryResponse decode";
    public static final String REQUEST_RESPONSE_DECODE = "RequestResponse decode";
    public static final String MESSAGE_DECODE = "Message decode";

    public static final String CHECK_COED = "check_code";
    public static final String USERNAME = "username";




    //激活key
    public static final String KEY = "jxz236-20141118-02-L-F-A11100";
    //应用跟目录
    public static final String APP_PATH = Environment.getExternalStorageDirectory().getPath()+"/mapbar/app";
    //应用名
    public static final String APP_NAME = "qyfw";
}
