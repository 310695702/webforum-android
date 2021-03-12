package com.kcbs.webforum.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.dialog.LoadingDialog;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.onLongClickListener;
import com.kcbs.webforum.pojo.PostResult;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.kcbs.webforum.utils.TimeUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanglu.photoviewerlibrary.PhotoViewer;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichType;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PostActivity extends AppCompatActivity {
    private RecyclerView rv_post;
    private User saveUser;
    private int userId;
    private List<PostResult.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private TextView tv_title, tv_null;
    private int i = 1;
    private SwipeRefreshLayout rf_refresh;
    private ImageView iv_back;
    private int lasti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        StatusBar.fitSystemBar(this);
        userId = getIntent().getIntExtra("UserId", -1);
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
            init();
        }
    }

    private void init() {
        rv_post = findViewById(R.id.rv_post);
        tv_title = findViewById(R.id.tv_title);
        tv_null = findViewById(R.id.tv_null);
        rf_refresh = findViewById(R.id.rf_refresh);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (saveUser != null && saveUser.getUserId() == userId) {
            tv_title.setText("我的帖子");
        } else {
            tv_title.setText("TA的帖子");
        }
        selectPost(1, 20);
        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                i = 1;
                list.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                selectPost(1, 20);
            }
        });

    }

    private void selectPost(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/getPostByUserId?pageNum=" + pageNum + "&pageSize=" + pageSize + "&userId=" + userId, new INetCallback<Result<PostResult>>() {
            @Override
            public void onSuccess(Result<PostResult> data) {
                rf_refresh.setRefreshing(false);
                dismissLoading();
                if (data.getStatus() == 10000) {
                    list.addAll(data.getData().getList());
                    if (adapter == null) {
                        adapter = new MyListAdapter(PostActivity.this, list);
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (tv_title.getText().equals("我的帖子") || (saveUser != null && saveUser.getRole() == 2)) {
                                    AlertDialog dialog = new AlertDialog.Builder(PostActivity.this)
                                            .setMessage("您确定要删除\"" + list.get(position).getTitle() + "\"吗?一旦删除无法恢复。")
                                            .setPositiveButton("取消", null)
                                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    deletepost(position);
                                                }
                                            })
                                            .create();
                                    dialog.show();
                                }
                            }
                        });

                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                ContentActivity.start(PostActivity.this, (long) list.get(position).getPostId());
                            }
                        });
                        rv_post.setLayoutManager(new LinearLayoutManager(PostActivity.this));
                        rv_post.setAdapter(adapter);
                        rv_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                                    if (data.getData().getPages() > i) {
                                        showLoading();
                                        i++;
                                        selectPost(i, 20);
                                    } else {
                                        AppUtils.Toast.shouToast(PostActivity.this, "到底了~");
                                    }
                                }
                            }
                        });
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    if (i == 1 && data.getData().getTotal() == 0 && data.getData().getPageNum() == 1) {
                        if (saveUser.getUserId() == userId) {
                            tv_null.setText("你还没有发表过话题~");
                        } else {
                            tv_null.setText("TA还没有发表过话题~");
                        }
                    }

                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    PostActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
                dismissLoading();
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult>>() {
                }.getType();
            }
        });
    }

    private void deletepost(int position) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("postId", list.get(position).getPostId());
        OkHttpUtils.getInstance().dopost("/deletePost", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(PostActivity.this, "删除成功");
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    PostActivity.this.finish();
                } else {
                    AppUtils.Toast.shouToast(PostActivity.this, "删除失败,请重试");
                }

            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(PostActivity.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    public static void start(Context context, int userId) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("UserId", userId);
        context.startActivity(intent);
    }


    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<PostResult.ListBean> datasource;
        private com.kcbs.webforum.onLongClickListener onLongClickListener;
        private com.kcbs.webforum.onClickListener onClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnLongClickListener(com.kcbs.webforum.onLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
        }

        public MyListAdapter(Context context, List<PostResult.ListBean> datasource) {
            this.context = context;
            this.datasource = datasource;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tv_title.setText(datasource.get(position).getTitle());
            holder.tv_name.setText(datasource.get(position).getUserName());
            selectImage(datasource.get(position).getPostId(), holder.rv_post);
            if (datasource.get(position).getContent().contains("<") && datasource.get(position).getContent().contains("/") && datasource.get(position).getContent().contains(">")) {
                RichText.from(datasource.get(position).getContent()).autoFix(true).clickable(true).noImage(true)
                        .into(holder.tv_content);
            } else if (datasource.get(position).getContent().contains("!") && datasource.get(position).getContent().contains("[") && datasource.get(position).getContent().contains("]") && datasource.get(position).getContent().contains("(") && datasource.get(position).getContent().contains(")")) {
                RichText.from(datasource.get(position).getContent()).type(RichType.MARKDOWN).autoFix(true).clickable(true).noImage(true)
                        .into(holder.tv_content);
            } else {
                holder.tv_content.setText(datasource.get(position).getContent());
            }
            holder.rv_post.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        holder.root.performClick();  //模拟父控件的点击
                    }
                    return false;
                }
            });
            holder.tv_content.setOnClickListener(v -> {
                onClickListener.onClick(position);
            });
            holder.tv_commentnum.setText(datasource.get(position).getCommentNum() + "");
            holder.tv_time.setText("发布于"+ TimeUtils.timeTocalculate(datasource.get(position).getCreateTime()));
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickListener.onLongClick(position);
                    return true;
                }
            });
            holder.tv_content.setOnClickListener(v -> {
                onClickListener.onClick(position);
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(datasource.get(position).getHeadSculpture()).apply(options).into(holder.riv_head);
            //setBackGround(holder.root);
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_title, tv_content, tv_name, tv_commentnum, tv_time;
            private ConstraintLayout root;
            private RecyclerView rv_post;
            private RoundedImageView riv_head;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_commentnum = itemView.findViewById(R.id.tv_commentnum);
                tv_time = itemView.findViewById(R.id.tv_time);
                root = itemView.findViewById(R.id.root);
                rv_post = itemView.findViewById(R.id.rv_post);
                riv_head = itemView.findViewById(R.id.riv_head);
            }
        }
    }

    private void setBackGround(ConstraintLayout root) {
        Random random = new Random();
        int i = random.nextInt(16);
        while (lasti == i) {
            i = random.nextInt(16);
        }
        lasti = i;
        if (i == 1)
            root.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        if (i == 2)
            root.setBackgroundColor(getResources().getColor(R.color.bg_2));
        if (i == 3)
            root.setBackgroundColor(getResources().getColor(R.color.bg_3));
        if (i == 4)
            root.setBackgroundColor(getResources().getColor(R.color.bg_4));
        if (i == 5)
            root.setBackgroundColor(getResources().getColor(R.color.bg_5));
        if (i == 6)
            root.setBackgroundColor(getResources().getColor(R.color.bg_6));
        if (i == 7)
            root.setBackgroundColor(getResources().getColor(R.color.bg_7));
        if (i == 8)
            root.setBackgroundColor(getResources().getColor(R.color.bg_8));
        if (i == 9)
            root.setBackgroundColor(getResources().getColor(R.color.bg_9));
        if (i == 10)
            root.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        if (i == 11)
            root.setBackgroundColor(getResources().getColor(R.color.bg_11));
        if (i == 12)
            root.setBackgroundColor(getResources().getColor(R.color.bg_12));
        if (i == 13)
            root.setBackgroundColor(getResources().getColor(R.color.bg_13));
        if (i == 14)
            root.setBackgroundColor(getResources().getColor(R.color.bg_14));
        if (i == 15)
            root.setBackgroundColor(getResources().getColor(R.color.bg_15));
    }

    private void selectImage(int postId, RecyclerView rv_post) {
        OkHttpUtils.getInstance().doget("/getPostImages?postId=" + postId, new INetCallback<Result<List<String>>>() {
            @Override
            public void onSuccess(Result<List<String>> data) {
                if (data.getStatus() == 10000) {
                    MyGridViewAdapter adapter = new MyGridViewAdapter(data.getData(), PostActivity.this);
                    adapter.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(int position) {
                            PhotoViewer.INSTANCE
                                    .setClickSingleImg(data.getData().get(position), rv_post)
                                    .setShowImageViewInterface((imageView, url) -> {
                                        RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                                .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                        //使用Glide显示图片
                                        Glide.with(PostActivity.this)
                                                .load(url)

                                                .apply(options).into(imageView);
                                    }).start(PostActivity.this);
                        }
                    });
                    rv_post.setLayoutManager(new GridLayoutManager(PostActivity.this, 3));
                    rv_post.setAdapter(adapter);
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<String>>>() {
                }.getType();
            }
        });
    }

    private class MyGridViewAdapter extends RecyclerView.Adapter<MyGridViewAdapter.MyGridViewHolder> {
        private List<String> urls;
        private Context context;
        private onClickListener onClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public MyGridViewAdapter(List<String> urls, Context context) {
            this.urls = urls;
            this.context = context;
        }

        @NonNull
        @Override
        public MyGridViewAdapter.MyGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new MyGridViewAdapter.MyGridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyGridViewAdapter.MyGridViewHolder holder, int position) {
            holder.iv_imagecancel.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(urls.get(position)).apply(options).into(holder.iv_image);
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return urls == null ? 0 : urls.size();
        }

        class MyGridViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv_image, iv_imagecancel;

            public MyGridViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_image = itemView.findViewById(R.id.iv_image);
                iv_imagecancel = itemView.findViewById(R.id.iv_imagecancel);
            }
        }
    }

    private LoadingDialog mLoadingDialog = null;

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.feed_load_ing));
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


}