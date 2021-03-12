package com.kcbs.webforum.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.kcbs.webforum.pojo.CommentVo;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.kcbs.webforum.utils.TimeUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zzhoujay.richtext.RichText;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private ImageView iv_back;
    private RecyclerView rv_comment;
    private TextView tv_null, tv_title;
    private long userId;
    private int i = 1;
    private List<CommentVo.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private SwipeRefreshLayout rf_refresh;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        StatusBar.fitSystemBar(this);
        userId = getIntent().getLongExtra("userId", -1);
        try {
            List<User> user = SavaDataUtils.getData(CommentActivity.this, "User", 0);
            if (user.get(0).getUserId() == userId) {
                tv_title = findViewById(R.id.tv_title);
                tv_title.setText("我的评论");
            } else {
                tv_title = findViewById(R.id.tv_title);
                tv_title.setText("TA的评论");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            tv_title = findViewById(R.id.tv_title);
            tv_title.setText("TA的评论");
        }
        if (userId == -1) {
            tv_null = findViewById(R.id.tv_null);
            tv_null.setText("网络错误，请重新登录后重试");
            tv_null.setVisibility(View.VISIBLE);
        } else {
            init();
        }
    }

    private void init() {
        iv_back = findViewById(R.id.iv_back);
        tv_null = findViewById(R.id.tv_null);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rv_comment = findViewById(R.id.rv_comment);
        rf_refresh = findViewById(R.id.rf_refresh);
        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                i = 1;
                list.clear();
                selectComment(i, 20);
            }
        });
        selectComment(i, 20);
    }

    private void selectComment(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/selectUserComment?pageNum=" + pageNum + "&pageSize=" + pageSize + "&userId=" + userId, new INetCallback<Result<CommentVo>>() {
            @Override
            public void onSuccess(Result<CommentVo> data) {
                rf_refresh.setRefreshing(false);
                dismissLoading();
                if (data.getStatus() == 10000) {
                    list.addAll(data.getData().getList());
                    if (list.size() == 0) {
                        tv_null.setVisibility(View.VISIBLE);
                    } else {
                        tv_null.setVisibility(View.GONE);
                    }
                    if (adapter == null) {
                        adapter = new MyListAdapter(CommentActivity.this, list);
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                ContentActivity.start(CommentActivity.this, (long) list.get(position).getParentId());
                            }
                        });
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (tv_title.getText().equals("我的评论")) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(CommentActivity.this)
                                            .setMessage("你要删除此条评论吗?一旦删除无法恢复。")
                                            .setPositiveButton("取消", null)
                                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    delComment(list.get(position).getCommentId(), position);
                                                }
                                            })
                                            .create();
                                    alertDialog.show();
                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                                }
                            }
                        });
                        rv_comment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                        rv_comment.setAdapter(adapter);
                        rv_comment.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                                    if (data.getData().getPages() > i) {
                                        showLoading();
                                        i++;
                                        selectComment(i, 20);
                                    } else {
                                        AppUtils.Toast.shouToast(CommentActivity.this, "到底了~");
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
                    CommentActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
                dismissLoading();
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<CommentVo>>() {
                }.getType();
            }
        });
    }

    private void delComment(int commentId, int position) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("commentId", commentId);
        OkHttpUtils.getInstance().dopost("/deleteComment", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(CommentActivity.this, "删除成功");
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    CommentActivity.this.finish();
                } else {
                    AppUtils.Toast.shouToast(CommentActivity.this, data.getMsg());
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(CommentActivity.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }


    public static void start(Context context, Long userId) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
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

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<CommentVo.ListBean> datasource;
        private com.kcbs.webforum.onClickListener onClickListener;
        private com.kcbs.webforum.onLongClickListener onLongClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnLongClickListener(com.kcbs.webforum.onLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
        }

        public MyListAdapter(Context context, List<CommentVo.ListBean> datasource) {
            this.context = context;
            this.datasource = datasource;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(datasource.get(position).getHeadSculpture()).apply(options).into(holder.riv_head);
            holder.tv_name.setText(datasource.get(position).getUsername());
            holder.tv_categoryName.setText("板块:" + datasource.get(position).getCategoryName());
            RichText.from(datasource.get(position).getContent()).autoFix(true)
                    .into(holder.tv_content);
            holder.tv_time.setText(TimeUtils.calculate(TimeUtils.timeToStamp(datasource.get(position).getCommentTime())));
            holder.tv_title.setText("原帖:" + datasource.get(position).getTitle());
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });

            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickListener.onLongClick(position);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private RoundedImageView riv_head;
            private TextView tv_name, tv_time, tv_content, tv_categoryName, tv_title;
            private ConstraintLayout root;
            private CardView cardview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                riv_head = itemView.findViewById(R.id.riv_head);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_categoryName = itemView.findViewById(R.id.tv_categoryName);
                tv_title = itemView.findViewById(R.id.tv_title);
                root = itemView.findViewById(R.id.root);
                cardview = itemView.findViewById(R.id.cardview);
            }
        }
    }
}
