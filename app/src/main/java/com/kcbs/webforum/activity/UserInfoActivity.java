package com.kcbs.webforum.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kcbs.webforum.R;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;

public class UserInfoActivity extends AppCompatActivity {
    private User saveUser;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        StatusBar.fitSystemBar(this);
        try {
            saveUser = (User) SavaDataUtils.getData(this,"User",0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }catch (Exception e){
            saveUser=null;
        }
        if (saveUser==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        init();
    }

    private void init() {
    }
}
