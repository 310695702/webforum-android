package com.kcbs.webforum.utils;

import android.util.DisplayMetrics;

import com.kcbs.webforum.WebApplication;


public class PixUtils {

    public static int dp2px(int dpValue) {
        DisplayMetrics metrics = WebApplication.getContext().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = WebApplication.getContext().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = WebApplication.getContext().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
