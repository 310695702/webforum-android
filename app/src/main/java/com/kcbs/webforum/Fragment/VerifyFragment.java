package com.kcbs.webforum.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.event.backloginEvent;
import com.kcbs.webforum.event.toRegisterEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.view.VerifyCodeView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerifyFragment extends Fragment {
    private View view;
    VerifyCodeView verifyCodeView;
    private String inputcode;
    private MyHandler mhandler = new MyHandler(this);
    private TextView tv_resend, tv_register;
    private boolean flag = true;//用于判断是否点击重新发送
    private toRegisterEvent toRegisterEvent;
    private ImageView action_close;
    private static List<String> register;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_verify, container, false);
            try {
                register = SavaDataUtils.getData(getContext(), "register", 1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }
            init(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void init(View view) {
        verifyCodeView = view.findViewById(R.id.verifyCodeView);
        action_close = view.findViewById(R.id.action_close);
        action_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tv_resend = view.findViewById(R.id.tv_resend);
        new Thread() {
            @Override
            public void run() {
                super.run();
                int i = 30;
                while (flag) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.arg1 = i;
                    if (i > 0) {
                        message.what = 0;
                        mhandler.sendMessage(message);
                        i--;
                    } else {
                        flag = false;
                        message.what = 1;
                        mhandler.sendMessage(message);
                    }
                }

            }
        }.start();
        tv_register = view.findViewById(R.id.tv_register);
        verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                inputcode = verifyCodeView.getEditContent();
            }

            @Override
            public void invalidContent() {

            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_resend.getText().equals("重新发送")) {
                    getCode();
                }
            }
        });
    }

    private void getCode() {
        try {
            OkHttpUtils.getInstance().doget("/getCode" + "?username=" + SavaDataUtils.getData(getContext(), "register", 1).get(0)
                    + "&email=" + SavaDataUtils.getData(getContext(), "register", 1).get(2), new INetCallback<Result<String>>() {

                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus() == 10000) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                int i = 30;
                                while (flag) {
                                    try {
                                        sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message message = new Message();
                                    message.arg1 = i;
                                    if (i > 0) {
                                        message.what = 0;
                                        mhandler.sendMessage(message);
                                        i--;
                                    } else {
                                        flag = false;
                                        message.what = 1;
                                        mhandler.sendMessage(message);
                                    }
                                }

                            }
                        }.start();
                    } else {
                        AppUtils.Toast.shouToast(getContext(), data.getMsg());
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            AppUtils.Toast.shouToast(getContext(), "发生意外错误,请联系管理员:310695702\n" + e.getMessage());
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
            AppUtils.Toast.shouToast(getContext(), "发生意外错误,请联系管理员:310695702\n" + e.getMessage());
        }
    }

    private void register() {
        if (inputcode.length() == 6) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("username", register.get(0));
            map.put("password", register.get(1));
            map.put("code", inputcode);
            OkHttpUtils.getInstance().dopost("/register", map, new INetCallback<Result<String>>() {

                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus() == 10000) {
                        AppUtils.Toast.shouToast(getContext(), "注册成功!");
                        EventBus.getDefault().post(new backloginEvent());
                        return;
                    }
                    AppUtils.Toast.shouToast(getContext(), data.getMsg());
                    toRegisterEvent = new toRegisterEvent();
                    EventBus.getDefault().postSticky(toRegisterEvent);
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

    //弱引用 防止内存泄漏
    private static class MyHandler extends Handler {
        private WeakReference<VerifyFragment> weakReference;

        public MyHandler(VerifyFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            VerifyFragment fragment = weakReference.get();
            switch (msg.what) {
                case 0:
                    int color = WebApplication.getContext().getResources().getColor(R.color.colorGrey);
                    fragment.tv_resend.setTextColor(color);
                    break;
                case 1:
                    int color2 = WebApplication.getContext().getResources().getColor(R.color.colorRed);
                    fragment.tv_resend.setTextColor(color2);
                    break;
            }
            int s = msg.arg1;
            if (s == 0) {
                fragment.tv_resend.setText("重新发送");
            } else {
                fragment.tv_resend.setText(s + "s后重新发送");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SavaDataUtils.setData(WebApplication.getContext(), new ArrayList<>(), 1, "register");
        if (toRegisterEvent != null) {
            EventBus.getDefault().removeStickyEvent(toRegisterEvent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
