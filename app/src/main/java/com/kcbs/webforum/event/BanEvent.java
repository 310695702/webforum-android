package com.kcbs.webforum.event;

public class BanEvent {
    private int time;
    private String msg;

    public BanEvent(int time,String msg){
        this.time = time*60;
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
