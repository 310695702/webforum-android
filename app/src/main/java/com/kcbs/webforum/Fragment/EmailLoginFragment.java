package com.kcbs.webforum.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;

import com.kcbs.webforum.event.backloginEvent;
import com.kcbs.webforum.event.loginSuccessEvent;
import com.kcbs.webforum.event.toLoginEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.EmailUtils;
import com.kcbs.webforum.utils.ListenerUtils;
import com.kcbs.webforum.utils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;

public class EmailLoginFragment extends Fragment {
    private View rootView;
    private EditText et_email,et_code;
    private ImageView iv_cancelemail,action_close;
    private TextView tv_backlogin,tv_login;
    private Button btn_getCode;
    private boolean flag = true;
    private MyHandler mhandler = new MyHandler(this);
    private loginSuccessEvent loginSuccessEvent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_emaillogin,container,false);
            init(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent!=null){
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void init(View rootView) {
        et_code = rootView.findViewById(R.id.et_code);
        et_email = rootView.findViewById(R.id.et_email);
        iv_cancelemail = rootView.findViewById(R.id.iv_cancelemail);
        ListenerUtils.AddOnChangeListener(et_email,iv_cancelemail);
        ListenerUtils.setClearListener(iv_cancelemail,et_email);
        tv_backlogin = rootView.findViewById(R.id.tv_backlogin);
        tv_backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new backloginEvent());
            }
        });
        action_close = rootView.findViewById(R.id.action_close);
        action_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tv_login = rootView.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_getCode = rootView.findViewById(R.id.btn_getCode);
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_getCode.getText().equals("获取验证码")){
                    getCode();
                }

            }
        });
    }

    private void getCode() {
        if (EmailUtils.isEmail(et_email.getText().toString())){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    int i=30;
                    while (flag){
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.arg1 = i;
                        if (i>0){
                            message.what = 0;
                            mhandler.sendMessage(message);
                            i--;
                        }else {
                            flag=false;
                            message.what = 1;
                            mhandler.sendMessage(message);
                        }
                    }

                }
            }.start();
            HashMap<Object,Object> map = new HashMap<>();
            map.put("email",et_email.getText().toString());
            OkHttpUtils.getInstance().dopost("/findPassword", map, new INetCallback<Result<String>>() {
                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus()==10000){
                        AppUtils.Toast.shouToast(getContext(),"邮件发送成功");
                    }else {
                        AppUtils.Toast.shouToast(getContext(),data.getMsg());
                    }
                }

                @Override
                public void onFailed(Throwable ex) {
                    AppUtils.Toast.shouToast(getContext(),"网络异常");
                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<String>>(){}.getType();
                }
            });
        }else {
            AppUtils.Toast.shouToast(getContext(),"请输入正确的邮箱");
        }
    }

    private void login() {
        if (et_code.getText().length()!=6){
            AppUtils.Toast.shouToast(getContext(),"请输入正确的验证码");
            return;
        }
        if (!et_email.getText().toString().equals("")&&!et_code.getText().toString().equals("")&&EmailUtils.isEmail(et_email.getText().toString())){
        HashMap<Object,Object> map = new HashMap<>();
        map.put("username",et_email.getText().toString());
        map.put("password",et_code.getText().toString());
        OkHttpUtils.getInstance().dopost("/login", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus()==10000){
                    loginSuccessEvent = new loginSuccessEvent(data);
                    EventBus.getDefault().postSticky(loginSuccessEvent);
                }else if (data.getStatus()==10007){
                    AppUtils.Toast.shouToast(getContext(),"验证码错误");
                    return;
                }
                AppUtils.Toast.shouToast(getContext(),data.getMsg());
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>(){}.getType();
            }
        });
        }else {
            AppUtils.Toast.shouToast(getContext(),"请输入邮箱及验证码");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loginSuccessEvent!=null){
            EventBus.getDefault().removeStickyEvent(loginSuccessEvent);
        }
    }

    //弱引用 防止内存泄漏
    private static class MyHandler extends Handler {
        private WeakReference<EmailLoginFragment> weakReference;

        public MyHandler(EmailLoginFragment fragment){
            this.weakReference = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EmailLoginFragment fragment = weakReference.get();
            switch (msg.what) {
                case 0:
                    fragment.btn_getCode.setAlpha(0.4f);
                    break;
                case 1:
                    fragment.btn_getCode.setAlpha(1f);
                    break;
            }
            int s = msg.arg1;
            if (s==0){
                fragment.btn_getCode.setText("重新发送");
            }else {
                fragment.btn_getCode.setText(s+"s后重新发送");
            }
        }
    }
}
