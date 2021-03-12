package com.kcbs.webforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeQian extends AppCompatActivity {
    private EditText et_text;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geqian);
        StatusBar.fitSystemBar(this);
        try {
            user = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            user = null;
        }
        if (user == null) {
            GeQian.this.finish();
            startActivity(new Intent(GeQian.this, LoginActivity.class));
        } else {
            et_text = findViewById(R.id.et_text);
            et_text.setText(user.getPersonalizedSignature() + "");
            et_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().length() == 50) {
                        AppUtils.Toast.shouToast(GeQian.this, "最长50个字哦~");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

    }

    public void GeQianClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_save:
                update();
                break;
        }
    }

    private void update() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("personalizedSignature", et_text.getText() + "");
        OkHttpUtils.getInstance().dopost("/user/update", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(GeQian.this, "更新成功");
                    List<User> users = new ArrayList<>();
                    user.setPersonalizedSignature(et_text.getText().toString());
                    users.add(user);
                    SavaDataUtils.setData(GeQian.this, users, 0, "User");
                    finish();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    GeQian.this.finish();
                } else {
                    AppUtils.Toast.shouToast(GeQian.this, "更新失败,请重试");
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(GeQian.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

}
