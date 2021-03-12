package com.kcbs.webforum.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

//缓存对象工具类
public class SavaDataUtils {
    public static final String HOMETYPELIST = "Cache";
    public static <T> void setData(Context context, List<T> list, int type, String tag) {
        File file = context.getCacheDir();
        File Cache = null;
        String name;
        if (type == 0) {
            name = HOMETYPELIST + tag;
            Cache = new File(file, name);
        } else if (type == 1) {
            name = HOMETYPELIST + tag;
            Cache = new File(file, name);
        } else {
            name = HOMETYPELIST + tag;
            Cache = new File(file, name);
        }
        if (Cache.exists()) {
            Cache.delete();
        }
        try {
            ObjectOutputStream outputStream =
                    new ObjectOutputStream(new FileOutputStream(Cache));
            outputStream.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static <T> List<T> getData(Context context,String tag, int type) throws IllegalAccessException, InstantiationException {
        File file = context.getCacheDir();
        String name;
        File cache;
        List<T> list = null;
        if (type == 0) {
            name = HOMETYPELIST + tag;
            cache = new File(file, name);
            if (!cache.exists()) {
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 1) {
            name = HOMETYPELIST + tag;
            cache = new File(file, name);
            if (!cache.exists()) {
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            name = HOMETYPELIST + tag;
            cache = new File(file, name);
            if (!cache.exists()) {
                return null;
            }
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cache));
                list = (List<T>) inputStream.readObject();
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

