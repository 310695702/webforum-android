package com.kcbs.webforum.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TResult;
import com.kcbs.webforum.R;
import com.kcbs.webforum.activity.CommentActivity;
import com.kcbs.webforum.activity.FallowActivity;
import com.kcbs.webforum.activity.FunsActivity;
import com.kcbs.webforum.activity.LoginActivity;
import com.kcbs.webforum.activity.PostActivity;
import com.kcbs.webforum.activity.SettingActivity;
import com.kcbs.webforum.event.LogoutEvent;
import com.kcbs.webforum.event.toLoginEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.lxj.matisse.CaptureMode;
import com.lxj.matisse.Matisse;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanglu.photoviewerlibrary.PhotoViewer;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Type;

import static android.app.Activity.RESULT_OK;

public class MyFragement extends TakePhotoFragment {
    private View rootView;
    private ImageView iv_logout, iv_setting;
    private TextView tv_name, tv_content, tv_fallow, tv_funs, tv_mypost, tv_mycomment;
    private User user;
    private RoundedImageView riv_head;
    private static final int REQUEST_EXTERNAL_STORAGE = 1; // 不可改
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my, container, false);
            try {
                user = (User) SavaDataUtils.getData(getContext(), "User", 0).get(0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }catch (Exception e){
                user = null;
            }
            init(rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if ((permission != PackageManager.PERMISSION_GRANTED) || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            if (user!=null){
                showBottomDialog();
            }else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }
    }

    private void init(View view) {
        iv_logout = view.findViewById(R.id.iv_logout);
        tv_name = view.findViewById(R.id.tv_name);
        tv_content = view.findViewById(R.id.tv_content);
        riv_head = view.findViewById(R.id.riv_head);
        tv_mypost = view.findViewById(R.id.tv_mypost);
        iv_setting = view.findViewById(R.id.iv_setting);
        iv_setting.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;
        });
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });
        tv_mycomment = view.findViewById(R.id.tv_mycomment);
        tv_mycomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    CommentActivity.start(getContext(), (long) user.getUserId());
                }else {
                    EventBus.getDefault().post(new toLoginEvent());
                }
            }
        });
        tv_fallow = view.findViewById(R.id.tv_fallow);
        tv_funs = view.findViewById(R.id.tv_funs);

        iv_logout.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;
        });
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("你要退出登录吗?")
                        .setPositiveButton("还没想好", null)
                        .setNegativeButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        })
                        .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
            }
        });
        tv_fallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    FallowActivity.start(getContext(), (long) user.getUserId());
                }else {
                    EventBus.getDefault().post(new toLoginEvent());
                }
            }
        });
        tv_funs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    startActivity(new Intent(getContext(), FunsActivity.class));
                }else {
                    EventBus.getDefault().post(new toLoginEvent());
                }
            }
        });
        tv_mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user!=null){
                    PostActivity.start(getContext(), user.getUserId());
                }else {
                    EventBus.getDefault().post(new toLoginEvent());
                }
            }
        });

        if (user!=null){
            iv_logout.setVisibility(View.VISIBLE);
            tv_name.setText(user.getUsername());
            selectPostNum();
            selectComment();
            if (user.getHeadSculpture().contains("gif")) {

                RequestOptions options = new RequestOptions()
                        .fallback(R.drawable.error).error(R.drawable.error);
                Glide.with(getContext()).asGif().load(Uri.parse(user.getHeadSculpture()))
                        .apply(options).into(riv_head);
            } else {

                RequestOptions options = new RequestOptions()
                        .fallback(R.drawable.error).error(R.drawable.error);
                Glide.with(getContext()).load(Uri.parse(user.getHeadSculpture()))
                        .apply(options).into(riv_head);
            }
            if (user.getPersonalizedSignature().equals("")) {
                tv_content.setText("这个朋友还没有签名哦~");
            } else {
                tv_content.setText(user.getPersonalizedSignature());
            }
            selectfuns();
            selectFallow();
            url = user.getHeadSculpture();

        }else {
            url = "http://47.111.9.152:8088/images/morentouxiang.png";
            iv_logout.setVisibility(View.GONE);
            tv_name.setText("未登录");
            tv_mypost.setText("我的帖子");
            tv_mycomment.setText("我的评论");
            tv_funs.setText("0粉丝");
            tv_fallow.setText("0关注");
            tv_content.setText("这个朋友还没有登录哦~");
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(getContext()).asGif().load(url)
                    .apply(options).into(riv_head);
        }
        riv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void selectComment() {
        OkHttpUtils.getInstance().doget("/getCommentNum", new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_mycomment.setText("我的评论(" + data.getData() + ")");
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<Integer>>() {
                }.getType();
            }
        });
    }

    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(getContext(), R.layout.dialog, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_show_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoViewer.INSTANCE
                        .setClickSingleImg(url, riv_head)
                        .setShowImageViewInterface((imageView, url) -> {
                            RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                    .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                            //使用Glide显示图片
                            Glide.with(MyFragement.this)
                                    .load(url)

                                    .apply(options).into(imageView);
                        }).start(MyFragement.this);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matisse.from(MyFragement.this)
                        .jumpCapture(CaptureMode.Image)//直接跳拍摄，默认可以同时拍摄照片和视频
                        //.jumpCapture(CaptureMode.Image)//只拍照片
                        //.jumpCapture(CaptureMode.Video)//只拍视频
                        .isCrop(true) //开启裁剪
                        .forResult(1);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTake(getTakePhoto());
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        File file = new File(result.getImage().getOriginalPath());
        upload(file);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            File file = new File(Matisse.obtainCropResult(data));
            upload(file);
        }
    }


    private void upload(File file) {
        OkHttpUtils.getInstance().dopostMutipart("/user/upload", file, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {

                    RequestOptions options = new RequestOptions()
                            .fallback(R.drawable.error).error(R.drawable.error);
                    Glide.with(getContext()).load(data.getData()).apply(options).into(riv_head);
                    url = data.getData();
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

    private void selectPostNum() {
        OkHttpUtils.getInstance().doget("/getPostNum", new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_mypost.setText("我的帖子(" + data.getData() + ")");
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<Integer>>() {
                }.getType();
            }
        });
    }

    private void selectfuns() {
        OkHttpUtils.getInstance().doget("/getFunsNum", new INetCallback<Result<Integer>>() {

            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_funs.setText(data.getData() + "粉丝");
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<Integer>>() {
                }.getType();
            }
        });
    }

    private void selectFallow() {
        OkHttpUtils.getInstance().doget("/getSubscribeNum", new INetCallback<Result<Integer>>() {

            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_fallow.setText(data.getData() + "关注");
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<Integer>>() {
                }.getType();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        try {
            user = (User) SavaDataUtils.getData(getContext(), "User", 0).get(0);
            selectPostNum();
            selectfuns();
            selectFallow();
            selectComment();
            url = user.getHeadSculpture();
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.morentouxiang).error(R.drawable.morentouxiang);
            Glide.with(getContext()).load(url)
                    .apply(options).into(riv_head);
            iv_logout.setVisibility(View.VISIBLE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }catch (Exception e){
                user = null;
            }
        if (user!=null){
            if (user.getPersonalizedSignature().equals("")) {
                tv_content.setText("这个朋友还没有签名哦~");
            } else {
                tv_content.setText(user.getPersonalizedSignature());
            }
        }else {
            url = "http://47.111.9.152:8088/images/morentouxiang.png";
            iv_logout.setVisibility(View.GONE);
            tv_name.setText("未登录");
            tv_mypost.setText("我的帖子");
            tv_mycomment.setText("我的评论");
            tv_funs.setText("0粉丝");
            tv_fallow.setText("0关注");
            tv_content.setText("这个朋友还没有登录哦~");
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.morentouxiang).error(R.drawable.morentouxiang);
            Glide.with(getContext()).load(url)
                    .apply(options).into(riv_head);
        }
    }

    private void logout() {
        OkHttpUtils.getInstance().dopost("/logout", null, new INetCallback<Result<String>>() {

            @Override
            public void onSuccess(Result<String> data) {

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
        EventBus.getDefault().post(new LogoutEvent());
        user = null;
        update();
    }


}
