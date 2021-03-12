package com.kcbs.webforum.utils;

public class EmailUtils {

    public static boolean isEmail(String email){
        if(email.contains("@")&&email.contains(".")) {
            if(email.lastIndexOf(".")>email.lastIndexOf("@")) {
                return true;
            }
        }
        return false;
    }
}
