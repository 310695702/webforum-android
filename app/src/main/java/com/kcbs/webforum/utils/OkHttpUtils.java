package com.kcbs.webforum.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.kcbs.webforum.interceptor.MyHeaderInterceptor;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.net.IhttpInvoke;
import com.kcbs.webforum.WebApplication;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils implements IhttpInvoke {
    public static OkHttpUtils sInstance;
    public static OkHttpUtils getInstance(){
        if (sInstance==null){
            sInstance = new OkHttpUtils();
        }
        return sInstance;
    }
    private Handler handler = new Handler(Looper.getMainLooper());
    private static OkHttpClient client;
    public OkHttpUtils(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NotNull String s) {
                Log.d("hyx-bysj",s);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(new MyHeaderInterceptor())
                .addInterceptor(logging)
                .build();
    }
    @Override
    public void doget(String url, INetCallback callback) {
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .get()
                .build();
        executeRequest(callback, request);
    }

    @Override
    public void dopost(String url, HashMap<Object, Object> params, INetCallback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params!=null){
            for (Object obj:params.keySet()){
                formBodyBuilder.add(String.valueOf(obj),String.valueOf(params.get(obj)));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(formBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }

    @Override
    public void dopostMutipart(String url, File file, INetCallback callback) {
        MultipartBody.Builder mutipartBodyBuilder = new MultipartBody.Builder();
        mutipartBodyBuilder.setType(MultipartBody.FORM);
        if (file!=null){
            String type = file.getName().substring(file.getName().lastIndexOf("."));
            mutipartBodyBuilder.addFormDataPart("file",file.getName(),RequestBody.create(file,MediaType.parse("image/gif")));
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(mutipartBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }

    @Override
    public void dopostMutipartFiles(String url, List<File> files, INetCallback callback) {
        MultipartBody.Builder mutipartBodyBuilder = new MultipartBody.Builder();
        mutipartBodyBuilder.setType(MultipartBody.FORM);
        if (files!=null){
            for (File file:files){
                mutipartBodyBuilder.addFormDataPart("images",file.getName(),RequestBody.create(file,MediaType.parse("image/gif")));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(mutipartBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }


    @Override
    public void dopostMutipartAndObj(String url, HashMap<Object, Object> params, List<String> urls, INetCallback callback) {
        MultipartBody.Builder mutipartBodyBuilder = new MultipartBody.Builder();
        mutipartBodyBuilder.setType(MultipartBody.FORM);
        if (urls!=null){
            for (String s:urls){
                mutipartBodyBuilder.addFormDataPart("urls",s);
            }
        }
        if (params!=null){
            for (Object obj:params.keySet()){
                mutipartBodyBuilder.addFormDataPart(String.valueOf(obj),String.valueOf(params.get(obj)));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(mutipartBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }

    @Override
    public void dopostMutipartAndFile(String url,HashMap<Object,Object> params,File file, INetCallback callback) {
        MultipartBody.Builder mutipartBodyBuilder = new MultipartBody.Builder();
        mutipartBodyBuilder.setType(MultipartBody.FORM);
        if (file!=null){
            String type = file.getName().substring(file.getName().lastIndexOf("."));
            mutipartBodyBuilder.addFormDataPart("file",file.getName(),RequestBody.create(file,MediaType.parse("image/gif")));
        }
        if (params!=null){
            for (Object obj:params.keySet()){
                mutipartBodyBuilder.addFormDataPart(String.valueOf(obj),String.valueOf(params.get(obj)));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(mutipartBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }


    @Override
    public void dopostJson(String url, String jsonstr, INetCallback callback) {
        MediaType mediaType = MediaType.get("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonstr,mediaType);
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .post(requestBody)
                .build();
        executeRequest(callback,request);
    }

    @Override
    public void doput(String url, HashMap<Object, Object> params, INetCallback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params!=null){
            for (Object obj:params.keySet()){
                formBodyBuilder.add(String.valueOf(obj),String.valueOf(params.get(obj)));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .put(formBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }

    @Override
    public void dodelete(String url, HashMap<Object, Object> params, INetCallback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params!=null){
            for (Object obj:params.keySet()){
                formBodyBuilder.add(String.valueOf(obj),String.valueOf(params.get(obj)));
            }
        }
        Request request = new Request.Builder()
                .url(WebApplication.HOST+url)
                .delete(formBodyBuilder.build())
                .build();
        executeRequest(callback,request);
    }

    private void executeRequest(INetCallback callback, Request request) {
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respstr = null;
                try {
                    respstr = response.body().string();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(e);
                        }
                    });
                    return;
                }
                String finalRespstr = respstr;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(new Gson().fromJson(finalRespstr,callback.getType()));
                    }
                });
            }
        });
    }
}
