package com.kcbs.webforum.activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kcbs.webforum.R;
import com.kcbs.webforum.utils.StatusBar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    //初始化显示保存id的数组
    private ImageSwitcher imageswithcher;
    //要显示的图片在图片数组中的索引
    private int index;
    //定义手指按下和抬起时X的坐标
    private float touchDownX;
    private float touchUpX;
    private static List<String> path = new ArrayList<>();
    private TextView tv_image;
    private ImageView imageView;
    private PlayerView playview;
    private SimpleExoPlayer player;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                imageswithcher.setImageDrawable(drawable);
            }
        }
    };
    private Drawable drawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.fitSystemBar(this);
        setContentView(R.layout.image_layout);
        player = new SimpleExoPlayer.Builder(this).build();
        playview = findViewById(R.id.playview);
        playview.setPlayer(player);
        //设置全屏显示
        index = getIntent().getIntExtra("position", 0);
        path = getIntent().getStringArrayListExtra("urls");
        tv_image = findViewById(R.id.tv_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAGS_CHANGED, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取ImageSwitcher对象并创建工厂
        imageswithcher = findViewById(R.id.imageswitcher);

        imageswithcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                //在重写的方法中创建ImageView用于显示图片
                imageView = new ImageView(ImageActivity.this);
                //给ImageView设定一个要显示的默认图片
                ImageSwitcher.LayoutParams layoutParams = new ImageSwitcher.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(layoutParams);
                if (path.size() > index) {
                    String s = path.get(index);
                    if (s.contains("video")) {
                        imageView.setVisibility(View.GONE);
                        playview.setVisibility(View.VISIBLE);
                        // Produces DataSource instances through which media data is loaded.
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(ImageActivity.this,
                                Util.getUserAgent(ImageActivity.this, "LYApplication"));
// This is the MediaSource representing the media to be played.
                        MediaSource videoSource =
                                new ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(Uri.parse(path.get(index)));
// Prepare the player with the source.
                        player.prepare(videoSource);
                        tv_image.setText(index + 1 + "/" + path.size());
                    } else {
                        playview.setVisibility(View.GONE);
                        imageswithcher.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions()
                                .fallback(R.drawable.error).error(R.drawable.error);
                        Glide.with(ImageActivity.this).load(Uri.parse(path.get(index)))
                                .apply(options).into(imageView);
                        tv_image.setText(index + 1 + "/" + path.size());
                    }
                }
                return imageView;
            }
        });
        playview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownX = event.getX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchUpX = event.getX();
                    //判断手指是否是从左向右滑动
                    if (touchUpX - touchDownX > 80) {
                        player.stop();
                        playview.setVisibility(View.GONE);
                        imageswithcher.setVisibility(View.VISIBLE);
                        //如果当前的图片是第一张图片 那么就让索引变成最后一张图片的索引  否则的话将当前图片的索引减一,此时的索引通过三目表达式来表达
                        index = index == 0 ? path.size() - 1 : index - 1;
                        //定义一下图片的切换方式
                        imageswithcher.setOutAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out));
                        imageswithcher.setInAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                        isvideo(player);
                        tv_image.setText(index + 1 + "/" + path.size());
                    } else if (touchDownX - touchUpX > 80) {
                        player.stop();
                        playview.setVisibility(View.GONE);
                        imageswithcher.setVisibility(View.VISIBLE);
                        //如果当前的索引值等于数组的长度 那个index设置为0.否则的话index+1
                        index = index == path.size() - 1 ? 0 : index + 1;
                        //定义一下图片的切换方式
                        imageswithcher.setOutAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out));
                        imageswithcher.setInAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                        isvideo(player);

                        tv_image.setText(index + 1 + "/" + path.size());
                    } else {

                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
        //为ImageView添加触发时间监听器
        imageswithcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDownX = motionEvent.getX();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    touchUpX = motionEvent.getX();
                    //判断手指是否是从左向右滑动
                    if (touchUpX - touchDownX > 80) {
                        //如果当前的图片是第一张图片 那么就让索引变成最后一张图片的索引  否则的话将当前图片的索引减一,此时的索引通过三目表达式来表达
                        index = index == 0 ? path.size() - 1 : index - 1;
                        //定义一下图片的切换方式
                        imageswithcher.setOutAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out));
                        imageswithcher.setInAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                        isvideo(player);
                        tv_image.setText(index + 1 + "/" + path.size());
                    } else if (touchDownX - touchUpX > 80) {
                        //如果当前的索引值等于数组的长度 那个index设置为0.否则的话index+1
                        index = index == path.size() - 1 ? 0 : index + 1;
                        //定义一下图片的切换方式
                        imageswithcher.setOutAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out));
                        imageswithcher.setInAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                        isvideo(player);
                        // imageswithcher.setImageURI(path.get(index));
                        tv_image.setText(index + 1 + "/" + path.size());
                    } else {
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void isvideo(SimpleExoPlayer player) {
        String s = "" + path.get(index);
        if (s.contains("video")) {
            player.retry();
            imageswithcher.setVisibility(View.GONE);
            playview.setVisibility(View.VISIBLE);
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(ImageActivity.this,
                    Util.getUserAgent(ImageActivity.this, "LYApplication"));
// This is the MediaSource representing the media to be played.
            MediaSource videoSource =
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(path.get(index)));
// Prepare the player with the source.
            player.prepare(videoSource);
        } else {
            imageswithcher.setVisibility(View.VISIBLE);
            playview.setVisibility(View.GONE);
            new Thread() {
                @Override
                public void run() {
                    loadImageFromNetwork(path.get(index));
                }
            }.start();
        }
    }


    private void loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
            this.drawable = drawable;
            handler.sendEmptyMessage(0);
        } catch (IOException e) {
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.release();
        }
    }
}
