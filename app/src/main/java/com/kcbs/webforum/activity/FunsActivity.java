package com.kcbs.webforum.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.kcbs.webforum.pojo.Page;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunsActivity extends AppCompatActivity {
    private RecyclerView rv_funs;
    private List<Page.DataBean.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private Long userId;
    private TextView tv_null;
    private SwipeRefreshLayout rf_refresh;
    private int i = 1;
    private User saveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funs);
        StatusBar.fitSystemBar(this);
        userId = getIntent().getLongExtra("userId", 0);
        try {
            saveUser = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            saveUser = null;
        }
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            getFunsList(1, 20);
        }
    }

    public static void start(Context context, Long userId) {
        Intent intent = new Intent(context, FunsActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    private void init() {
        rf_refresh = findViewById(R.id.rf_refresh);
        rv_funs = findViewById(R.id.rv_funs);
        tv_null = findViewById(R.id.tv_null);
        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                i = 1;
                getFunsList(i, 20);
            }
        });
        getFunsList(i, 20);
    }

    private void getFunsList(int pageNum, int pageSize) {
        String url = null;
        if (userId == 0) {
            url = "/getFunsList?pageNum=" + pageNum + "&pageSize=" + pageSize;
        } else {
            url = "/getFunsList?pageNum=" + pageNum + "&pageSize=" + pageSize + "&beSubscribe=" + userId;
        }
        OkHttpUtils.getInstance().doget(url, new INetCallback<Page>() {
            @Override
            public void onSuccess(Page data) {
                dismissLoading();
                rf_refresh.setRefreshing(false);
                if (data.getStatus() == 10000) {
                    list.clear();
                    list.addAll(data.getData().getList());
                    if (list.size() == 0) {
                        tv_null.setVisibility(View.VISIBLE);
                    } else {
                        tv_null.setVisibility(View.GONE);
                    }
                    if (adapter == null) {
                        adapter = new MyListAdapter(FunsActivity.this, list);
                        rv_funs.setLayoutManager(new LinearLayoutManager(FunsActivity.this));
                        rv_funs.setAdapter(adapter);
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                UserActivity.start(FunsActivity.this, (long) list.get(position).getUserId());
                            }
                        });
                        rv_funs.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                                    if (data.getData().getPages() > i) {
                                        showLoading();
                                        i++;
                                        getFunsList(i, 20);
                                    } else {
                                        AppUtils.Toast.shouToast(FunsActivity.this, "到底了~");
                                    }
                                }
                            }
                        });
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    FunsActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
                dismissLoading();
            }

            @Override
            public Type getType() {
                return new TypeToken<Page>() {
                }.getType();
            }
        });
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

    public void FunsOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<Page.DataBean.ListBean> datasource;
        private com.kcbs.webforum.onClickListener onClickListener;

        public void setOnClickListener(onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public MyListAdapter(Context context, List<Page.DataBean.ListBean> datasource) {
            this.context = context;
            this.datasource = datasource;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_myfuns, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(Uri.parse(datasource.get(position).getHeadSculpture() + "?" + String.valueOf(System.currentTimeMillis()).substring(0, 8)))
                    .apply(options).into(holder.riv_head);
            holder.tv_name.setText(datasource.get(position).getUsername());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
            if (saveUser != null && (userId == 0)) {
                holder.tv_btn.setVisibility(View.VISIBLE);
                if (datasource.get(position).getSubscribeStatus() == 0) {
                    holder.tv_btn.setText("关注");
                }
                if (datasource.get(position).getSubscribeStatus() == 2) {
                    holder.tv_btn.setText("互相关注");
                }
            } else {
                holder.tv_btn.setVisibility(View.GONE);
            }
            holder.tv_btn.setOnClickListener(v -> {
                if (holder.tv_btn.getText().equals("互相关注")) {
                    AlertDialog dialog = new AlertDialog.Builder(FunsActivity.this)
                            .setMessage("您确定要取消关注" + datasource.get(position).getUsername() + "吗?取消后您将无法再看到对方的最新动态。")
                            .setPositiveButton("取消", null)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancelfallow(datasource.get(position).getUserId(), holder.tv_btn);
                                }
                            })
                            .create();
                    dialog.show();

                }
                if (holder.tv_btn.getText().equals("关注")) {
                    fallow(datasource.get(position).getUserId(), holder.tv_btn);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private RoundedImageView riv_head;
            private TextView tv_name, tv_btn;
            private RelativeLayout root;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                riv_head = itemView.findViewById(R.id.riv_head);
                root = itemView.findViewById(R.id.root);
                tv_btn = itemView.findViewById(R.id.tv_btn);
            }
        }
    }

    private void fallow(int userId, TextView tv_btn) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("beSubscribe", userId);
        OkHttpUtils.getInstance().doput("/subscribe", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    tv_btn.setText("互相关注");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    finish();
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


    private void cancelfallow(int userId, TextView tv_btn) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("beSubscribe", userId);
        OkHttpUtils.getInstance().dodelete("/subscribe", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    tv_btn.setText("关注");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    finish();
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
}