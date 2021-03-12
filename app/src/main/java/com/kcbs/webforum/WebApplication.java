package com.kcbs.webforum;

import android.app.Application;
import android.content.Context;

import com.kcbs.webforum.biz.TcpClientbiz;
import com.kcbs.webforum.utils.SavaDataUtils;

import java.util.ArrayList;
import java.util.List;

public class WebApplication extends Application {
    public static String HOST = "http://47.111.9.152:8088";
    private static Context context;
    public static TcpClientbiz mTcpclient;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SavaDataUtils.setData(context,new ArrayList<String>(),0,"content");
        mTcpclient = new TcpClientbiz();
    }

    // 调用
    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mTcpclient.onDestory();
    }
}
