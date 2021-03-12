package com.kcbs.webforum.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TResult;
import com.kcbs.webforum.R;
import com.kcbs.webforum.dialog.LoadingDialog;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.event.PostSuccessEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.StatusBar;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewPostActivity extends TakePhotoActivity {
    private ImageView iv_back;
    private Long categoryId;
    private EditText et_title, et_content;
    private PostSuccessEvent event;
    private TextView tv_post;
    private RecyclerView rv_image;
    private List<String> urls = new ArrayList<>();
    private ImageAdapter adapter;
    private static final int REQUEST_EXTERNAL_STORAGE = 1; // 不可改
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private boolean isDeleteAble = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        StatusBar.fitSystemBar(this);
        categoryId = getIntent().getLongExtra("categoryId", 0);
        init();
    }

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if ((permission != PackageManager.PERMISSION_GRANTED) || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            onClickTake(getTakePhoto());
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        if (urls.size() < 9) {
            urls.add(result.getImage().getOriginalPath());
            adapter.notifyDataSetChanged();
        } else {
            AppUtils.Toast.shouToast(NewPostActivity.this, "最多上传9张照片哦~");
        }

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
        int maxSize = Integer.parseInt("1048000");//最大 压缩
        int width = Integer.parseInt("800");//宽
        int height = Integer.parseInt("800");//高
        CompressConfig config;
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(false)//拍照压缩后是否显示原图
                .create();
        takePhoto.onEnableCompress(config, false);//是否显示进度条
    }


    private void init() {
        rv_image = findViewById(R.id.rv_image);
        adapter = new ImageAdapter(this);
        rv_image.setLayoutManager(new GridLayoutManager(this, 3));
        rv_image.setAdapter(adapter);
        iv_back = findViewById(R.id.iv_back);
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_post = findViewById(R.id.tv_post);
        tv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_title.getText().length() >= 2 && et_content.getText().length() >= 5) {
                    post();
                } else {
                    AppUtils.Toast.shouToast(NewPostActivity.this, "字数不够哦~");
                }

            }
        });
    }

    private LoadingDialog mLoadingDialog = null;

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.feed_publish_ing));
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

    private void post() {
        showLoading();
        if (urls.size() == 0) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("categoryId", categoryId);
            map.put("title", et_title.getText().toString());
            map.put("content", et_content.getText().toString());
            OkHttpUtils.getInstance().dopost("/user/post", map, new INetCallback<Result<String>>() {
                @Override
                public void onSuccess(Result<String> data) {
                    dismissLoading();
                    if (data.getStatus() == 10000) {
                        AppUtils.Toast.shouToast(NewPostActivity.this, "发布成功");
                        event = new PostSuccessEvent();
                        EventBus.getDefault().postSticky(event);
                        finish();
                    } else if (data.getStatus() == 10020) {
                        LoginExpiredEvent event = new LoginExpiredEvent();
                        EventBus.getDefault().post(event);
                        NewPostActivity.this.finish();
                    }
                }

                @Override
                public void onFailed(Throwable ex) {
                    AppUtils.Toast.shouToast(NewPostActivity.this, "网络异常");
                    dismissLoading();
                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<String>>() {
                    }.getType();
                }
            });
        } else {
            List<File> files = new ArrayList<>();
            for (String s : urls) {
                File file = new File(s);
                files.add(file);
            }
            OkHttpUtils.getInstance().dopostMutipartFiles("/user/uploadPostImage", files, new INetCallback<Result<List<String>>>() {
                @Override
                public void onSuccess(Result<List<String>> data) {
                    if (data.getStatus() == 10000 && data.getData() != null) {
                        HashMap<Object, Object> map = new HashMap<>();
                        map.put("categoryId", categoryId);
                        map.put("title", et_title.getText().toString());
                        map.put("content", et_content.getText().toString());
                        OkHttpUtils.getInstance().dopostMutipartAndObj("/user/post", map, data.getData(), new INetCallback<Result<String>>() {
                            @Override
                            public void onSuccess(Result<String> data) {
                                dismissLoading();
                                if (data.getStatus() == 10000) {
                                    AppUtils.Toast.shouToast(NewPostActivity.this, "发布成功");
                                    event = new PostSuccessEvent();
                                    EventBus.getDefault().postSticky(event);
                                    finish();
                                } else if (data.getStatus() == 10020) {
                                    LoginExpiredEvent event = new LoginExpiredEvent();
                                    EventBus.getDefault().post(event);
                                    NewPostActivity.this.finish();
                                }
                            }

                            @Override
                            public void onFailed(Throwable ex) {
                                AppUtils.Toast.shouToast(NewPostActivity.this, "网络异常");
                                dismissLoading();
                            }

                            @Override
                            public Type getType() {
                                return new TypeToken<Result<String>>() {
                                }.getType();
                            }
                        });
                    }
                }

                @Override
                public void onFailed(Throwable ex) {
                    AppUtils.Toast.shouToast(NewPostActivity.this, "网络异常");
                    dismissLoading();
                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<List<String>>>() {
                    }.getType();
                }
            });
        }

    }

    public static void start(Context context, Long categoryId) {
        Intent intent = new Intent(context, NewPostActivity.class);
        intent.putExtra("categoryId", categoryId);
        context.startActivity(intent);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            if (position == urls.size()) {
                RequestOptions options = new RequestOptions()
                        .fallback(R.drawable.error).error(R.drawable.error);
                Glide.with(context).load(getDrawable(R.drawable.imageadd)).apply(options).into(holder.iv_image);
                holder.iv_imagecancel.setVisibility(View.GONE);
            } else {
                RequestOptions options = new RequestOptions()
                        .fallback(R.drawable.error).error(R.drawable.error);
                Glide.with(context).load(urls.get(position)).apply(options).into(holder.iv_image);
                holder.iv_imagecancel.setVisibility(View.VISIBLE);
            }
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == urls.size()) {
                        checkPermission();
                    }
                }
            });
            holder.iv_imagecancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != urls.size()) {
                        if (isDeleteAble) {
                            isDeleteAble = false;
                            urls.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, urls.size() + 1);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                isDeleteAble = true;
                            }
                            ).start();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return urls.size() + 1;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv_image, iv_imagecancel;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_image = itemView.findViewById(R.id.iv_image);
                iv_imagecancel = itemView.findViewById(R.id.iv_imagecancel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
        }
    }
}
