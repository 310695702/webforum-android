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
import com.kcbs.webforum.R;
import com.kcbs.webforum.event.backloginEvent;
import com.kcbs.webforum.event.toVerifyEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.ListenerUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegistFragment extends Fragment {
    private View view;
    private EditText et_email, et_username, et_password;
    private ImageView iv_cancelEmail,iv_cancelusername,iv_cancelpassword,action_close;
    private TextView tv_tologin,tv_getCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_register, container, false);
            init(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void init(View view) {
        et_email = view.findViewById(R.id.et_email);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        tv_tologin = view.findViewById(R.id.tv_tologin);
        iv_cancelEmail = view.findViewById(R.id.iv_cancelEmail);
        iv_cancelusername = view.findViewById(R.id.iv_cancelusername);
        iv_cancelpassword = view.findViewById(R.id.iv_cancelpassword);
        tv_getCode = view.findViewById(R.id.tv_getCode);
        action_close = view.findViewById(R.id.action_close);
        action_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tv_tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new backloginEvent());
            }
        });
        ListenerUtils.AddOnChangeListener(et_email,iv_cancelEmail);
        ListenerUtils.AddOnChangeListener(et_username,iv_cancelusername);
        ListenerUtils.AddOnChangeListener(et_password,iv_cancelpassword);
        ListenerUtils.setClearListener(iv_cancelEmail,et_email);
        ListenerUtils.setClearListener(iv_cancelusername,et_username);
        ListenerUtils.setClearListener(iv_cancelpassword,et_password);
        tv_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
    }

    private void getCode() {
        if (et_email.getText().toString().equals("")){
            AppUtils.Toast.shouToast(getContext(),"邮箱不能为空");
            return;
        }
        if (et_username.getText().toString().equals("")){
            AppUtils.Toast.shouToast(getContext(),"用户名不能为空");
            return;
        }
        if (et_password.getText().toString().equals("")){
            AppUtils.Toast.shouToast(getContext(),"密码不能为空");
            return;
        }
        OkHttpUtils.getInstance().doget("/getCode"+"?username="+et_username.getText().toString()
                +"&email="+et_email.getText().toString(), new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus()==10000){
                    List<String> register = new ArrayList<>();
                    register.add(0,et_username.getText().toString());
                    register.add(1,et_password.getText().toString());
                    register.add(2,et_email.getText().toString());
                    for (String s:register){
                        System.out.println(s);
                    }
                    SavaDataUtils.setData(getContext(),register,1,"register");
                    EventBus.getDefault().post(new toVerifyEvent());
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
    }

}
