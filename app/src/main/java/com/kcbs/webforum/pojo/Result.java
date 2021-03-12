package com.kcbs.webforum.pojo;

import java.io.Serializable;

public class Result<T> implements Serializable {

    /**
     * status : 10000
     * msg : SUCCESS
     * data : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZWJmb3J1bS11c2VyIiwiaWF0IjoxNjA5NjAzNDQ1LCJpZCI6IjQiLCJuaWNrbmFtZSI6Imh5eCJ9.fw7O_rmBHjg2W9LXWxajUaZP2ngZ5YHwU_9zl6pdfqI
     */

    private int status;
    private String msg;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
