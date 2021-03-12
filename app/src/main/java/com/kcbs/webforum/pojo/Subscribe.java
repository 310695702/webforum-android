package com.kcbs.webforum.pojo;

import java.util.Date;

public class Subscribe {
    private Long id;

    private Long beSubscribe;

    private Long subscribe;

    private Byte isDel;

    private String updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBeSubscribe() {
        return beSubscribe;
    }

    public void setBeSubscribe(Long beSubscribe) {
        this.beSubscribe = beSubscribe;
    }

    public Long getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Long subscribe) {
        this.subscribe = subscribe;
    }

    public Byte getIsDel() {
        return isDel;
    }

    public void setIsDel(Byte isDel) {
        this.isDel = isDel;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}