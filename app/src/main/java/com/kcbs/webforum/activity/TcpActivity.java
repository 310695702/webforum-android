package com.kcbs.webforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.biz.TcpClientbiz;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;

import java.util.ArrayList;
import java.util.List;

public class TcpActivity extends AppCompatActivity {
    private EditText mMsg;
    private Button mBtn_send;
    private TextView mTvContent;
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        StatusBar.fitSystemBar(this);
        init();
        try {
            List<String> list = SavaDataUtils.getData(WebApplication.getContext(), "content", 0);
            if (list.size() != 0) {
                mTvContent.setText("");
                for (String s : list) {
                    appendMsgToContent(s);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        WebApplication.mTcpclient.setOnMsgCommingListener(new TcpClientbiz.OnMsgCommingListener() {
            @Override
            public void onMsgComing(String msg) {
                List<String> list = new ArrayList<>();
                try {
                    if (SavaDataUtils.getData(WebApplication.getContext(), "content", 0) == null) {

                    } else {
                        list.addAll(SavaDataUtils.getData(WebApplication.getContext(), "content", 0));
                    }
                    list.add(msg);
                    SavaDataUtils.setData(WebApplication.getContext(), list, 0, "content");
                    mTvContent.setText("");
                    for (String s : list) {
                        appendMsgToContent(s);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception ex) {

            }
        });
        mBtn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMsg.getText().toString();
                mMsg.setText("");
                if (TextUtils.isEmpty(msg)) {
                    return;
                } else {
                    WebApplication.mTcpclient.sendMsg(msg);
                }
            }
        });
    }

    private void appendMsgToContent(String msg) {
        mTvContent.append(msg + "\n\n");
    }

    public static void start(Context context, String categoryName) {
        Intent intent = new Intent(context, TcpActivity.class);
        intent.putExtra("categoryName", categoryName);
        context.startActivity(intent);
    }

    private void init() {
        mMsg = findViewById(R.id.et_msg);
        mBtn_send = findViewById(R.id.btn_send);
        mTvContent = findViewById(R.id.tv_content);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
