package com.kcbs.webforum.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.zenglb.downloadinstaller.DownloadInstaller;
import com.zenglb.downloadinstaller.DownloadProgressCallBack;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import static com.kcbs.webforum.WebApplication.getContext;

public class SettingActivity extends AppCompatActivity {
    private CardView cd_p1, cd_p2, cd_submit, cd_5;
    private EditText et_p1, et_p2;
    private SharedPreferences sharedPreferences;
    private static final int REQUEST_EXTERNAL_STORAGE = 1; // 不可改
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBar.fitSystemBar(this);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        init();
    }

    private void init() {
        cd_p1 = findViewById(R.id.cd_2);
        cd_p2 = findViewById(R.id.cd_3);
        cd_submit = findViewById(R.id.cd_4);
        et_p1 = findViewById(R.id.et_p1);
        et_p2 = findViewById(R.id.et_p2);
        cd_5 = findViewById(R.id.cd_5);
    }

    private void checkPermission(Result<List<Double>> data) {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if ((permission != PackageManager.PERMISSION_GRANTED) || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            AppUtils.Toast.shouToast(getContext(), "开始下载更新");
            new DownloadInstaller(getContext(), WebApplication.HOST + "/download/webforum-" + data.getData().get(1) + ".apk?100", true, new DownloadProgressCallBack() {
                @Override
                public void downloadProgress(int progress) {

                }

                @Override
                public void downloadException(Exception e) {
                    AppUtils.copy(WebApplication.HOST + "/download/webforum-" + data.getData().get(1) + ".apk?100", getContext());
                    AppUtils.Toast.shouToast(getContext(), "自动更新失败，已为您复制下载连接，您可以到浏览器打开，自行下载更新");
                }

                @Override
                public void onInstallStart() {
                    checkVersion();
                }
            }).start();
        }
    }

    public void SettingOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_person:
                startActivity(new Intent(this, GeQian.class));
                break;
            case R.id.tv_updatapwd:
                if (sharedPreferences.getString("token", null) != null) {
                    VisibilityBtn();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.tv_submit:
                update();
                break;
            case R.id.tv_about:
                AppUtils.Toast.shouToast(this, AppUtils.getAppName(this)
                        + " 版本:" + AppUtils.getVersionName(this));
                break;
            case R.id.tv_checkupdate:
                checkVersion();
                break;
            case R.id.tv_personinfo:
                if (sharedPreferences.getString("token", null) != null) {
                    startActivity(new Intent(this,UserInfoActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }
    }

    private void checkVersion() {
        OkHttpUtils.getInstance().doget("/GetVersion", new INetCallback<Result<List<Double>>>() {
            @Override
            public void onSuccess(Result<List<Double>> data) {
                if (data.getStatus() == 10000) {
                    if (!data.getData().get(1).toString().equals(AppUtils.getVersionName(SettingActivity.this))) {
                        AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                                .setMessage("检测到有新版本，您是否需要更新？")
                                .setPositiveButton("取消", null)
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        checkPermission(data);
                                    }
                                })
                                .create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                    } else {
                        AppUtils.Toast.shouToast(SettingActivity.this, "您已经是最新版本啦~");
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    SettingActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<Double>>>() {
                }.getType();
            }
        });
    }

    private void update() {
        if (et_p1.getText().toString().equals(et_p2.getText().toString())) {
            if (et_p1.getText().length() >= 8) {
                HashMap<Object, Object> map = new HashMap<>();
                map.put("password", et_p1.getText().toString());
                OkHttpUtils.getInstance().dopost("/updatePassword", map, new INetCallback<Result<String>>() {
                    @Override
                    public void onSuccess(Result<String> data) {
                        if (data.getStatus() == 10000) {
                            AppUtils.Toast.shouToast(SettingActivity.this, "密码修改成功");
                            cd_p1.setVisibility(View.GONE);
                            cd_p2.setVisibility(View.GONE);
                            cd_submit.setVisibility(View.GONE);
                            cd_5.setVisibility(View.VISIBLE);
                        } else if (data.getStatus() == 10020) {
                            LoginExpiredEvent event = new LoginExpiredEvent();
                            EventBus.getDefault().post(event);
                            SettingActivity.this.finish();
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
            } else {
                AppUtils.Toast.shouToast(SettingActivity.this, "密码不能少于8位");
            }
        } else {
            AppUtils.Toast.shouToast(SettingActivity.this, "两次输入密码不一致");
        }
    }

    private void VisibilityBtn() {
        cd_p1.setVisibility(View.VISIBLE);
        cd_p2.setVisibility(View.VISIBLE);
        cd_submit.setVisibility(View.VISIBLE);
        cd_5.setVisibility(View.INVISIBLE);
    }
}
