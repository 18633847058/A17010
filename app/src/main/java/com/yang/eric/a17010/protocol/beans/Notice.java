package com.yang.eric.a17010.protocol.beans;

/**
 * Created by Yang on 2017/5/2.
 */

public class Notice {
    //公告ID 4B
    private int id;
    //公告名称长度 1B
    private byte n;
    //公告名称
    private String name;
    //公告内容长度 2B
    private int m;
    //公告内容
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getN() {
        return n;
    }

    public void setN(byte n) {
        this.n = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
