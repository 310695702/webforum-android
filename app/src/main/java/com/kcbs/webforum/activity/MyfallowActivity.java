package com.kcbs.webforum.activity;

import android.content.Context;
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
import java.util.List;

public class MyfallowActivity extends AppCompatActivity {
    private RecyclerView rv_fallow;
    private User user;
    private int i = 1;
    private List<PostResult.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private SwipeRefreshLayout rf_refresh;
    private TextView tv_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfallow);
        StatusBar.fitSystemBar(this);
        try {
            user = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        tv_null = findViewById(R.id.tv_null);
        rv_fallow = findViewById(R.id.rv_fallow);
        rf_refresh = findViewById(R.id.rf_refresh);
        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                i = 1;
                selectMyFallow(i, 20);
            }
        });
        selectMyFallow(i, 20);
    }

    private void selectMyFallow(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/getSubscribePost?pageNum=" + pageNum + "&pageSize=" + pageSize, new INetCallback<Result<PostResult>>() {
            @Override
            public void onSuccess(Result<PostResult> data) {
                dismissLoading();
                rf_refresh.setRefreshing(false);
                if (data.getStatus() == 10000) {
                    list.addAll(data.getData().getList());
                    if (list.size() == 0) {
                        tv_null.setVisibility(View.VISIBLE);
                    } else {
                        tv_null.setVisibility(View.GONE);
                    }
                    if (adapter == null) {
                        adapter = new MyListAdapter(MyfallowActivity.this, list);
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                ContentActivity.start(MyfallowActivity.this, (long) list.get(position).getPostId());
                            }
                        });
                        rv_fallow.setLayoutManager(new LinearLayoutManager(MyfallowActivity.this));
                        rv_fallow.setAdapter(adapter);
                        rv_fallow.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                                    if (data.getData().getPages() > i) {
                                        showLoading();
                                        i++;
                                        selectMyFallow(i, 20);
                                    } else {
                                        AppUtils.Toast.shouToast(MyfallowActivity.this, "到底了~");
                                    }
                                }
                            }
                        });
                    } else {
                        System.out.println(data.getStatus() + "");
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    MyfallowActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                dismissLoading();
                rf_refresh.setRefreshing(false);
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult>>() {
                }.getType();
            }
        });
    }

    public void MyFallowClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
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

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<PostResult.ListBean> datasource;
        private com.kcbs.webforum.onClickListener onClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
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
            holder.tv_time.setText("发布于" + TimeUtils.calculate(TimeUtils.timeToStamp(datasource.get(position).getCreateTime())));
            holder.tv_name.setText(datasource.get(position).getUserName());
            selectImage(datasource.get(position).getPostId(), holder.tv_context, holder.rv_post);
            if (datasource.get(position).getContent().contains("<") && datasource.get(position).getContent().contains("/") && datasource.get(position).getContent().contains(">")) {
                RichText.from(datasource.get(position).getContent()).autoFix(true).clickable(true).noImage(true)
                        .into(holder.tv_context);
            } else if (datasource.get(position).getContent().contains("!") && datasource.get(position).getContent().contains("[") && datasource.get(position).getContent().contains("]") && datasource.get(position).getContent().contains("(") && datasource.get(position).getContent().contains(")")) {
                RichText.from(datasource.get(position).getContent()).type(RichType.MARKDOWN).autoFix(true).clickable(true).noImage(true)
                        .into(holder.tv_context);
            } else {
                holder.tv_context.setText(datasource.get(position).getContent());
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
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
            holder.tv_commentnum.setText(datasource.get(position).getCommentNum() + "");
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(datasource.get(position).getHeadSculpture()).apply(options).into(holder.riv_head);
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_title, tv_context, tv_name, tv_time, tv_commentnum;
            private ConstraintLayout root;
            private RoundedImageView riv_head;
            private RecyclerView rv_post;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_context = itemView.findViewById(R.id.tv_content);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_commentnum = itemView.findViewById(R.id.tv_commentnum);
                root = itemView.findViewById(R.id.root);
                riv_head = itemView.findViewById(R.id.riv_head);
                rv_post = itemView.findViewById(R.id.rv_post);
            }
        }

    }

    private void selectImage(int postId, TextView textView, RecyclerView rv_post) {
        OkHttpUtils.getInstance().doget("/getPostImages?postId=" + postId, new INetCallback<Result<List<String>>>() {
            @Override
            public void onSuccess(Result<List<String>> data) {
                if (data.getStatus() == 10000 && data.getData().size() > 0) {
                    MyGridViewAdapter adapter = new MyGridViewAdapter(data.getData(), MyfallowActivity.this);
                    adapter.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(int position) {
                            if (textView != null) {
                                PhotoViewer.INSTANCE
                                        //设置当前位置
                                        .setCurrentPage(0)
                                        //设置图片控件容器
                                        //他需要容器的目的是
                                        //显示缩放动画
                                        .setClickSingleImg(data.getData().get(position), textView)
                                        //设置图片加载回调
                                        .setShowImageViewInterface((imageView, url) -> {
                                            RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                                    .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                            //使用Glide显示图片
                                            Glide.with(MyfallowActivity.this)
                                                    .load(url)
                                                    .apply(options).into(imageView);
                                        })
                                        //启动界面
                                        .start(MyfallowActivity.this);
                            }
                        }
                    });
                    rv_post.setLayoutManager(new GridLayoutManager(MyfallowActivity.this, 3));
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}