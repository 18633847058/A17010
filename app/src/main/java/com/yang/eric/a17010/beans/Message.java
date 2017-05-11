package com.yang.eric.a17010.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Yang on 2017/5/9.
 */
@Entity
public class Message {

    @Id(autoincrement = true)
    private Long id;
    private Long mid;
    //内容
    private String content;
    //时间
    private String time;
    //接收发送
    private int received;
    //未读
    private int read;
    //类型
    private int type;
    @Transient
    private int sendState;
    @Generated(hash = 247157569)
    public Message(Long id, Long mid, String content, String time, int received,
            int read, int type) {
        this.id = id;
        this.mid = mid;
        this.content = content;
        this.time = time;
        this.received = received;
        this.read = read;
        this.type = type;
    }
    @Generated(hash = 637306882)
    public Message() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMid() {
        return this.mid;
    }
    public void setMid(Long mid) {
        this.mid = mid;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getReceived() {
        return this.received;
    }
    public void setReceived(int received) {
        this.received = received;
    }
    public int getRead() {
        return this.read;
    }
    public void setRead(int read) {
        this.read = read;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }
}
