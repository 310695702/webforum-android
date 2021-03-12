package com.kcbs.webforum.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.event.loginSuccessEvent;
import com.kcbs.webforum.event.toEmailLoginEvent;
import com.kcbs.webforum.event.toRegisterEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.R;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.ListenerUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.pojo.Result;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.HashMap;

public class LoginFragment extends Fragment {
    private View rootView;
    private TextView tv_register,tv_login,tv_email;
    private EditText et_username,et_password;
    private loginSuccessEvent event;
    private ImageView iv_cancelpassword,iv_cancelusername,action_close;
    private toEmailLoginEvent emailLoginEvent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_login,container,false);
            init(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent!=null){
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void init(View view) {
        tv_register = view.findViewById(R.id.tv_register);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        tv_login = view.findViewById(R.id.tv_login);
        tv_email = view.findViewById(R.id.tv_email);
        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLoginEvent = new toEmailLoginEvent();
                EventBus.getDefault().post(emailLoginEvent);
            }
        });
        action_close = view.findViewById(R.id.action_close);
        action_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        iv_cancelusername = view.findViewById(R.id.iv_cancelusername);
        iv_cancelpassword = view.findViewById(R.id.iv_cancelpassword);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new toRegisterEvent());
            }
        });
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               login();
            }
        });
        ListenerUtils.AddOnChangeListener(et_username,iv_cancelusername);
        ListenerUtils.AddOnChangeListener(et_password,iv_cancelpassword);
        ListenerUtils.setClearListener(iv_cancelusername,et_username);
        ListenerUtils.setClearListener(iv_cancelpassword,et_password);
    }



    private void login() {
        if (et_username.getText().toString().equals("")){
            AppUtils.Toast.shouToast(getContext(),"用户名不能为空");
            return;
        }
        if (et_password.getText().toString().equals("")){
            AppUtils.Toast.shouToast(getContext(),"密码不能为空");
            return;
        }
        HashMap<Object,Object> map = new HashMap<>();
        map.put("username",et_username.getText().toString());
        map.put("password",et_password.getText().toString());
        OkHttpUtils.getInstance().dopost("/login", map, new INetCallback<Result<String>>() {

            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus()==10000&&data.getMsg().equals("SUCCESS")){
                    event = new loginSuccessEvent(data);
                    EventBus.getDefault().post(event);
                }
                AppUtils.Toast.shouToast(getContext(),data.getMsg());
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(getContext(),ex.getMessage());
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>(){}.getType();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (emailLoginEvent!=null){
            EventBus.getDefault().removeStickyEvent(emailLoginEvent);
        }
    }

}
