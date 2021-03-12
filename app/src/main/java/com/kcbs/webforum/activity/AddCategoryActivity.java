package com.kcbs.webforum.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.gson.reflect.TypeToken;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TResult;
import com.kcbs.webforum.R;
import com.kcbs.webforum.dialog.LoadingDialog;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.StatusBar;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;

public class AddCategoryActivity extends TakePhotoActivity {
    private ImageView iv_categoryImage;
    private File file = null;
    private EditText et_categoryName;
    private static final int REQUEST_EXTERNAL_STORAGE = 1; // 不可改
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategory);
        StatusBar.fitSystemBar(this);
        init();
    }

    private void init() {
        iv_categoryImage = findViewById(R.id.iv_categoryImage);
        et_categoryName = findViewById(R.id.et_categoryName);
    }

    public void AddCategoryClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                addCategory();
                break;
            case R.id.tv_addImg:
                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                if ((permission != PackageManager.PERMISSION_GRANTED) || permission2 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                } else {
                    onClickTake(getTakePhoto());
                }
                break;
        }
    }

    private void addCategory() {
        if (et_categoryName.getText().toString().equals("")) {
            AppUtils.Toast.shouToast(AddCategoryActivity.this, "请输入新增分类名称");
        } else {
            if (file != null) {
                showLoading();
                HashMap<Object, Object> map = new HashMap<>();
                map.put("categoryName", et_categoryName.getText().toString());
                OkHttpUtils.getInstance().dopostMutipartAndFile("/admin/addCategory", map, file, new INetCallback<Result<String>>() {
                    @Override
                    public void onSuccess(Result<String> data) {
                        dismissLoading();
                        if (data.getStatus() == 10000) {
                            AppUtils.Toast.shouToast(AddCategoryActivity.this, "新增成功!");
                            finish();
                        }
                    }

                    @Override
                    public void onFailed(Throwable ex) {
                        dismissLoading();
                        AppUtils.Toast.shouToast(AddCategoryActivity.this, "网络异常");
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<Result<String>>() {
                        }.getType();
                    }
                });
            } else {
                AppUtils.Toast.shouToast(AddCategoryActivity.this, "请选择图片");
            }

        }
    }

    private LoadingDialog mLoadingDialog = null;

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.feed_add_ing));
        }
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } else {
            runOnUiThread(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        iv_categoryImage.setImageURI(Uri.parse(result.getImage().getOriginalPath()));
        file = new File(result.getImage().getOriginalPath());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    public void onClickTake(TakePhoto takePhoto) {
        configCompress(takePhoto);
        takePhoto.onPickFromGallery();//根据需求这里面放最大图片数 单选使用takePhoto.onPickFromGallery();
    }

    private void configCompress(TakePhoto takePhoto) {//压缩的配置
        int maxSize = Integer.parseInt("409600");//最大 压缩
        int width = Integer.parseInt("800");//宽
        int height = Integer.parseInt("800");//高
        CompressConfig config;
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(false)//拍照压缩后是否显示原图
                .create();
        takePhoto.onEnableCompress(config, false);//是否显示进度条
    }

}
