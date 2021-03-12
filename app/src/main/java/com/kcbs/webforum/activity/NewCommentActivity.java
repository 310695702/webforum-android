package com.kcbs.webforum.activity;

import android.content.Context;
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
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.StatusBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.HashMap;

public class NewCommentActivity extends AppCompatActivity {
    private Long postId;
    private EditText et_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcomment);
        StatusBar.fitSystemBar(this);
        postId = getIntent().getLongExtra("postId", 0);
        et_content = findViewById(R.id.et_content);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 255) {
                    AppUtils.Toast.shouToast(NewCommentActivity.this, "最多评论255个字哦~");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void CommentOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_post:
                post();
                break;
        }
    }

    private void post() {
        if (et_content.getText().toString().length() < 5) {
            AppUtils.Toast.shouToast(NewCommentActivity.this, "最少评论5个字哦~");
        } else {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("content", et_content.getText().toString());
            map.put("postId", postId);
            OkHttpUtils.getInstance().dopost("/comment", map, new INetCallback<Result<String>>() {
                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus() == 10000) {
                        AppUtils.Toast.shouToast(NewCommentActivity.this, "评论成功");
                        finish();
                    } else if (data.getStatus() == 10020) {
                        LoginExpiredEvent event = new LoginExpiredEvent();
                        EventBus.getDefault().post(event);
                        NewCommentActivity.this.finish();
                    }
                }

                @Override
                public void onFailed(Throwable ex) {

                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<String>>() {
                    }.getType();
                }
            });
        }
    }

    public static void start(Context context, Long postId) {
        Intent intent = new Intent(context, NewCommentActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }
}
