package com.kcbs.webforum.utils;

import com.kcbs.webforum.pojo.Category;

import java.util.ArrayList;
import java.util.List;

public class Helper {
    public static List<Category> initCategory(){
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setCategoryName("我的关注");
        category.setCategoryImage("http://47.111.9.152:8088/categoryImages/myfallow.png");
        categories.add(0,category);
        Category category1 = new Category();
        category1.setCategoryName("消息通知");
        category1.setCategoryImage("http://47.111.9.152:8088/categoryImages/gonggao.png");
        categories.add(1,category1);
        return categories;

    }

    public static List<String> StringList(){
        List<String> list = new ArrayList<>();
        list.add("推荐");
        list.add("热帖");
        list.add("精华");
        return list;
    }
}
