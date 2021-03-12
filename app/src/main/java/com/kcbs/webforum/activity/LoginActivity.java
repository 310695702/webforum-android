package com.kcbs.webforum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.Fragment.EmailLoginFragment;
import com.kcbs.webforum.Fragment.LoginFragment;
import com.kcbs.webforum.Fragment.RegistFragment;
import com.kcbs.webforum.Fragment.VerifyFragment;
import com.kcbs.webforum.R;
import com.kcbs.webforum.event.backloginEvent;
import com.kcbs.webforum.event.loginSuccessEvent;
import com.kcbs.webforum.event.toEmailLoginEvent;
import com.kcbs.webforum.event.toRegisterEvent;
import com.kcbs.webforum.event.toVerifyEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void toEmailLoginEvent(toEmailLoginEvent event) {
        EmailLoginFragment emailLoginFragment = new EmailLoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, emailLoginFragment).addToBackStack(null).commit();
    }

    @Subscribe
    public void loginSuccessEvent(loginSuccessEvent event) {
        if (event.getResult() != null) {
            editor.putString("token", String.valueOf(event.getResult().getData()));
            editor.commit();
        }
        getUser();
    }


    private void getUser() {
        OkHttpUtils.getInstance().doget("/getUser", new INetCallback<Result<User>>() {

            @Override
            public void onSuccess(Result<User> data) {
                if (data.getStatus() == 10000) {
                    User user = data.getData();
                    List<User> users = new ArrayList<>();
                    users.add(0, user);
                    SavaDataUtils.setData(LoginActivity.this, users, 0, "User");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    AppUtils.Toast.shouToast(LoginActivity.this, "登录失败,请重新登录");
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(LoginActivity.this, "登录过期,请重新登录");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<User>>() {
                }.getType();
            }
        });
    }

    @Subscribe
    public void toRegistEvent(toRegisterEvent event) {
        RegistFragment registFragment = new RegistFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, registFragment).addToBackStack(null).commit();
    }

    @Subscribe
    public void toVerifyEvent(toVerifyEvent event) {
        VerifyFragment verifyFragment = new VerifyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, verifyFragment).commit();
    }


    @Subscribe
    public void backloginEvent(backloginEvent event) {
        LoginFragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBar.fitSystemBar(this);
        StatusBar.lightStatusBar(this, true);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        LoginFragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, loginFragment).commit();
    }


}
