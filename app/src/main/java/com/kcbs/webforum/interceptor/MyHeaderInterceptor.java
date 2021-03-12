package com.kcbs.webforum.interceptor;

import android.content.Context;
import android.content.SharedPreferences;

import com.kcbs.webforum.WebApplication;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyHeaderInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        sharedPreferences = WebApplication.getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String token = null;
        Request request = chain.request();
        if (sharedPreferences.getString("token",null)!=null){
            token = sharedPreferences.getString("token",null);
            Request newrequest = request.newBuilder()
                    .addHeader("token", token)
                    .build();
            return chain.proceed(newrequest);
        }
        Request newrequest = request.newBuilder()
                .build();
        return chain.proceed(newrequest);
    }
}
