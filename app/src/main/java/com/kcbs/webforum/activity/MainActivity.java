package com.kcbs.webforum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.Fragment.AdminFragment;
import com.kcbs.webforum.Fragment.MainFragment;
import com.kcbs.webforum.R;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.event.LogoutEvent;
import com.kcbs.webforum.event.RefreshEvent;
import com.kcbs.webforum.event.ToAdminEvent;
import com.kcbs.webforum.event.ToUserEvent;
import com.kcbs.webforum.event.toLoginEvent;
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

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long firsttime = 0l;
    private RefreshEvent event;


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


    @Subscribe
    public void ToUserEvent(ToUserEvent event) {
        StatusBar.lightStatusBar(this, false);
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Container, mainFragment).commit();
    }

    @Subscribe
    public void ToAdminEvent(ToAdminEvent event) {
        StatusBar.lightStatusBar(this, true);
        AdminFragment fragment = new AdminFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.Container, fragment).commit();
    }

    @Subscribe
    public void toLoginEvent(toLoginEvent event) {
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Subscribe
    public void logoutEvent(LogoutEvent event) {
        logoutAndClear();
        getUser();
    }


    @Subscribe(sticky = true)
    public void LoginExpiredEvent(LoginExpiredEvent expiredEvent) {
        logoutAndClear();
        getUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启用沉浸式布局，白底黑字
        StatusBar.fitSystemBar(this);
        StatusBar.lightStatusBar(this, false);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getUser();

    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    private void getUser() {
        OkHttpUtils.getInstance().doget("/getUser", new INetCallback<Result<User>>() {

            @Override
            public void onSuccess(Result<User> data) {
                if (data.getStatus() == 10000) {
                    User user = data.getData();
                    List<User> users = new ArrayList<>();
                    users.add(0, user);
                    SavaDataUtils.setData(MainActivity.this, users, 0, "User");
                    if (user.getRole() == 1) {
                        StatusBar.lightStatusBar(MainActivity.this, true);
                        MainFragment mainFragment = new MainFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.Container, mainFragment).commit();
                    }
                    if (user.getRole() == 2) {
                        StatusBar.lightStatusBar(MainActivity.this, true);
                        AdminFragment fragment = new AdminFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.Container, fragment).commit();
                    }

                } else {
                    StatusBar.lightStatusBar(MainActivity.this, false);
                    MainFragment mainFragment = new MainFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.Container, mainFragment).commit();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(MainActivity.this, "登录过期,请重新登录");
                logoutAndClear();
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<User>>() {
                }.getType();
            }
        });
    }


    private void logoutAndClear() {
        editor.remove("token").commit();
        SavaDataUtils.setData(this, new ArrayList<>(), 0, "User");
        StatusBar.lightStatusBar(this, false);
        startActivity(new Intent(this, LoginActivity.class));

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - firsttime >= 2000) {
                AppUtils.Toast.shouToast(MainActivity.this, "再次点击退出");
                firsttime = System.currentTimeMillis();
                return false;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //点击空白收起键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();  //得到点击焦点
            if (isShouldHideKeyboard(v, ev)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}