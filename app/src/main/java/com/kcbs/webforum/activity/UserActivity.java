package com.kcbs.webforum.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.Fragment.PublisherDialogFragment;
import com.kcbs.webforum.R;
import com.kcbs.webforum.event.BanEvent;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.wanglu.photoviewerlibrary.PhotoViewer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {
    private ImageView iv_head;
    private TextView tv_ban, tv_name, tv_content, tv_post, tv_comment, tv_fallow, tv_funs, tv_cancel;
    private CardView cardview;
    private long userId;
    private User saveUser, resultUser;
    private RecyclerView rv_info;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void BanEvent(BanEvent event) {
        Ban(event.getTime(), event.getMsg());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        StatusBar.fitSystemBar(this);
        StatusBar.lightStatusBar(this, false);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = getIntent().getLongExtra("UserId", -1);
        try {
            saveUser = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            saveUser = null;
        }
        if (userId != -1) {
            getUserById();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultUser != null) {
            if (sharedPreferences.getString("token", null) != null)
                check();
            try {
                saveUser = (User) SavaDataUtils.getData(this, "User", 0).get(0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                saveUser = null;
            }
        }

    }

    private void getUserById() {
        OkHttpUtils.getInstance().doget("/getUser?userId=" + userId, new INetCallback<Result<User>>() {
            @Override
            public void onSuccess(Result<User> data) {
                if (data.getStatus() == 10000) {
                    resultUser = data.getData();
                    init();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<User>>() {
                }.getType();
            }
        });
    }

    private void getResultUser() {
        OkHttpUtils.getInstance().doget("/admin/getUser?userId=" + userId, new INetCallback<Result<User>>() {
            @Override
            public void onSuccess(Result<User> data) {
                if (data.getStatus() == 10000) {
                    tv_ban.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (data.getData().getIsBan() == 1) {
                                AlertDialog dialog = new AlertDialog.Builder(UserActivity.this)
                                        .setTitle("该用户已被封禁")
                                        .setMessage("封禁原因为:" + data.getData().getBanMessage() + "\n解禁时间为:" + data.getData().getEndTime())
                                        .setPositiveButton("新的封禁", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                newBan();
                                            }
                                        })
                                        .setNeutralButton("取消", null)
                                        .setNegativeButton("取消封禁", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                cancelBan();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            } else {
                                newBan();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<User>>() {
                }.getType();
            }
        });
    }

    private void newBan() {
        final PublisherDialogFragment fragment = new PublisherDialogFragment();
        fragment.show(getSupportFragmentManager(), "设置封禁时长(单位:分钟)");
    }

    private void init() {
        rv_info = findViewById(R.id.rv_info);
        rv_info.setLayoutManager(new LinearLayoutManager(UserActivity.this));
        rv_info.setAdapter(new MyInfoAdapter(UserActivity.this));
        iv_head = findViewById(R.id.iv_head);
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);
        tv_post = findViewById(R.id.tv_post);
        tv_comment = findViewById(R.id.tv_comment);
        tv_fallow = findViewById(R.id.tv_fallow);
        tv_funs = findViewById(R.id.tv_funs);
        tv_cancel = findViewById(R.id.tv_cancel);
        cardview = findViewById(R.id.cardview);
        tv_ban = findViewById(R.id.tv_ban);
        if (saveUser != null) {
            if (saveUser.getRole() == 2) {
                tv_ban.setVisibility(View.VISIBLE);
                getResultUser();
            }
        }
        RequestOptions options = new RequestOptions()
                .fallback(R.drawable.error).error(R.drawable.error);
        Glide.with(this).load(Uri.parse(resultUser.getHeadSculpture()))
                .apply(options).into(iv_head);
        iv_head.setOnClickListener(v -> {
            PhotoViewer.INSTANCE
                    .setCurrentPage(0)
                    .setClickSingleImg(resultUser.getHeadSculpture(), iv_head)
                    .setShowImageViewInterface((imageView, url) -> {
                        //使用Glide显示图片
                        Glide.with(this)
                                .load(resultUser.getHeadSculpture())
                                .apply(options).into(imageView);
                    })
                    .start(this);
        });
        tv_name.setText(resultUser.getUsername());
        if (resultUser.getPersonalizedSignature() == null || resultUser.getPersonalizedSignature().equals("")) {
            tv_content.setText("这个朋友还没有签名哦~");
        } else {
            tv_content.setText(resultUser.getPersonalizedSignature());
        }
        selectPostNum();
        selectCommentNum();
        selectfallow();
        selectFuns();
        if (sharedPreferences.getString("token", null) == null) {
            cardview.setCardBackgroundColor(getResources().getColor(R.color.colorRed));
            tv_cancel.setTextColor(Color.WHITE);
            tv_cancel.setText("未登录");
        } else {
            check();
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveUser == null) {
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    return;
                }
                if (saveUser.getUserId() != resultUser.getUserId() && tv_cancel.getText().toString().equals("关注")) {
                    fallow();
                }
                if (saveUser.getUserId() != resultUser.getUserId() && tv_cancel.getText().toString().equals("取消关注")) {
                    AlertDialog dialog = new AlertDialog.Builder(UserActivity.this)
                            .setMessage("您确定要取消关注" + resultUser.getUsername() + "吗?取消后您将无法再看到对方的最新动态。")
                            .setPositiveButton("取消", null)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancel();
                                }
                            })
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                }
            }
        });
    }

    private void Ban(int time, String msg) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("banMsg", msg);
        map.put("banTime", time);
        map.put("userId", resultUser.getUserId());
        OkHttpUtils.getInstance().dopost("/admin/Ban", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(UserActivity.this, "封禁成功!");
                    getResultUser();
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

    private void cancelBan() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userId", resultUser.getUserId());
        OkHttpUtils.getInstance().dopost("/admin/unBan", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    getResultUser();
                    AppUtils.Toast.shouToast(UserActivity.this, "解禁成功!");
                } else {
                    AppUtils.Toast.shouToast(UserActivity.this, "解禁失败");
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

    public void UserClick(View view) {
        switch (view.getId()) {
            case R.id.tv_post:
                PostActivity.start(this, resultUser.getUserId());
                break;
            case R.id.tv_comment:
                CommentActivity.start(this, (long) resultUser.getUserId());
                break;
            case R.id.tv_fallow:
                FallowActivity.start(this, (long) resultUser.getUserId());
                break;
            case R.id.tv_funs:
                FunsActivity.start(this, (long) resultUser.getUserId());
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void cancel() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("beSubscribe", resultUser.getUserId());
        OkHttpUtils.getInstance().dodelete("/subscribe", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    cardview.setCardBackgroundColor(getResources().getColor(R.color.btn_blue_pressed));
                    tv_cancel.setText("关注");
                    tv_cancel.setTextColor(getResources().getColor(R.color.colorBlack));
                    selectFuns();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
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

    private void fallow() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("beSubscribe", resultUser.getUserId());
        OkHttpUtils.getInstance().doput("/subscribe", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    cardview.setCardBackgroundColor(getResources().getColor(R.color.colorRed));
                    tv_cancel.setText("取消关注");
                    tv_cancel.setTextColor(Color.WHITE);
                    selectFuns();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
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

    private void check() {
        OkHttpUtils.getInstance().doget("/user/checkSubscribe?userId=" + resultUser.getUserId(), new INetCallback<Result<Boolean>>() {

            @Override
            public void onSuccess(Result<Boolean> data) {
                if (data.getStatus() == 10000) {
                    if (data.getData() == true) {
                        cardview.setCardBackgroundColor(getResources().getColor(R.color.colorRed));
                        tv_cancel.setText("取消关注");
                    } else {
                        cardview.setCardBackgroundColor(getResources().getColor(R.color.btn_blue_pressed));
                        tv_cancel.setText("关注");
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<Boolean>>() {
                }.getType();
            }
        });
    }

    private void selectFuns() {
        OkHttpUtils.getInstance().doget("/getFunsNum?userId=" + resultUser.getUserId(), new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                tv_funs.setText(data.getData() + "粉丝");
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

    private void selectfallow() {
        OkHttpUtils.getInstance().doget("/getSubscribeNum?userId=" + resultUser.getUserId(), new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_fallow.setText(data.getData() + "关注");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
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

    private void selectCommentNum() {
        OkHttpUtils.getInstance().doget("/getCommentNum?userId=" + resultUser.getUserId(), new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_comment.setText(data.getData() + "评论");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
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

    private void selectPostNum() {
        OkHttpUtils.getInstance().doget("/getPostNum?userId=" + resultUser.getUserId(), new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_post.setText(data.getData() + "帖子");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    UserActivity.this.finish();
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

    public static void start(Context context, Long userId) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("UserId", userId);
        context.startActivity(intent);
    }

    private class MyInfoAdapter extends RecyclerView.Adapter<MyInfoAdapter.MyInfoViewHolder> {
        private Context context;

        public MyInfoAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public MyInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_info, parent, false);
            return new MyInfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyInfoViewHolder holder, int position) {
            if (position == 0) {
                String school = "暂时还没有填写哦~";
                if (resultUser.getSchool() != null) {
                    school = (String) resultUser.getSchool();
                }
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.school), null, null, null);
                holder.tv_content.setText("学校:" + school);
            }
            if (position == 1) {
                String sex = "保密~";
                if (resultUser.getSex() == 1) {
                    sex = "帅哥";
                }
                if (resultUser.getSex() == 2) {
                    sex = "美女";
                }
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.sex), null, null, null);
                holder.tv_content.setText("性别:" + sex);
            }
            if (position == 2) {
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.number), null, null, null);
                holder.tv_content.setText("累计字数:" + resultUser.getWordNumber());
            }
            if (position == 3) {
                String wechat = "暂时还没有填写哦~";
                if (resultUser.getWechat() != null) {
                    wechat = (String) resultUser.getWechat();
                }
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.wechat), null, null, null);
                holder.tv_content.setText("微信:" + wechat);
            }
            if (position == 4) {
                String qq = "暂时还没有填写哦~";
                if (resultUser.getQq() != null) {
                    qq = (String) resultUser.getQq();
                }
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.qq), null, null, null);
                holder.tv_content.setText("qq:" + qq);
            }
            if (position == 5) {
                String web = "暂时还没有填写哦~";
                if (resultUser.getPersonalWebsite() != null) {
                    web = (String) resultUser.getPersonalWebsite();
                }
                holder.tv_content.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.web), null, null, null);
                holder.tv_content.setText("个人网站:" + web);
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        class MyInfoViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_content;

            public MyInfoViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_content = itemView.findViewById(R.id.tv_content);
            }
        }
    }
}