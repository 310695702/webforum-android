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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.dialog.LoadingDialog;
import com.kcbs.webforum.event.DelEvent;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.onLongClickListener;
import com.kcbs.webforum.pojo.Category;
import com.kcbs.webforum.pojo.CommentVo;
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
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnUrlClickListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private Long postId;
    private TextView tv_del, tv_null, tv_title, tv_name, tv_time, tv_content, tv_progress, tv_type;
    private RecyclerView rv_postContent, rv_image;
    private List<CommentVo.ListBean> list = new ArrayList<>();
    private MyContentListAdapter adapter;
    private RoundedImageView riv_head;
    private int i = 1;
    private int pages;
    private PostResult.ListBean post;
    private User user;
    private ConstraintLayout master;
    private DelEvent event;
    private OnUrlClickListener onUrlClickListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        StatusBar.fitSystemBar(this);
        postId = getIntent().getLongExtra("postId", 0);
        try {
            user = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            user = null;
        }
        if (postId == 0) {
            tv_null = findViewById(R.id.tv_null);
            tv_null.setVisibility(View.VISIBLE);
        } else {
            selectPost(postId);
        }
    }


    private void selectPost(Long postId) {
        OkHttpUtils.getInstance().doget("/getPostById?postId=" + postId, new INetCallback<Result<PostResult.ListBean>>() {

            @Override
            public void onSuccess(Result<PostResult.ListBean> data) {
                if (data.getStatus() == 10000) {
                    post = data.getData();
                    if (post != null) {
                        init();
                    } else {
                        finish();
                        AppUtils.Toast.shouToast(WebApplication.getContext(), "该帖子已被删除");
                    }
                } else {
                    finish();
                    AppUtils.Toast.shouToast(WebApplication.getContext(), "该帖子已被删除");
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult.ListBean>>() {
                }.getType();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            user = (User) SavaDataUtils.getData(this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            user = null;
        }
        if (postId != 0) {
            selectPost(postId);
        }
    }

    public void ContentOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_last:
                if (i >= 2) {
                    showLoading();
                    i--;
                    selectComment(i, 20);
                } else {
                    AppUtils.Toast.shouToast(this, "当前已经是第一页了");
                }
                break;
            case R.id.iv_next:
                if (pages - i == 1) {
                    showLoading();
                    i++;
                    selectComment(i, 20);
                } else {
                    AppUtils.Toast.shouToast(this, "当前已经是最后一页了");
                }
                break;
            case R.id.speak:
                if (user != null) {
                    NewCommentActivity.start(this, (long) post.getPostId());
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.tv_del:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("您确定要删除\"" + post.getTitle() + "\"吗?")
                        .setPositiveButton("取消", null)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletepost(postId);
                            }
                        })
                        .create();
                dialog.show();
                break;
        }
    }

    private void deletepost(Long postId) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("postId", postId);
        OkHttpUtils.getInstance().dopost("/deletePost", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(ContentActivity.this, "删除成功");
                    event = new DelEvent();
                    EventBus.getDefault().postSticky(event);
                    finish();
                } else {
                    AppUtils.Toast.shouToast(ContentActivity.this, "删除失败,请重试");
                }

            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(ContentActivity.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    private void init() {
        rv_image = findViewById(R.id.rv_image);
        tv_del = findViewById(R.id.tv_del);
        rv_postContent = findViewById(R.id.rv_postcontent);
        riv_head = findViewById(R.id.riv_head);
        tv_title = findViewById(R.id.tv_title);

        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_progress = findViewById(R.id.tv_progress);
        tv_content = findViewById(R.id.tv_content);
        tv_type = findViewById(R.id.tv_type);
        master = findViewById(R.id.master);
        onUrlClickListener = new OnUrlClickListener() {
            @Override
            public boolean urlClicked(String url) {
                Uri uri = Uri.parse("http://" + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return false;
            }
        };
        if (post.getContent().contains("<") && post.getContent().contains("/") && post.getContent().contains(">")) {
            RichText.from(post.getContent()).autoFix(true).clickable(true).noImage(false).urlClick(
                    onUrlClickListener)
                    .into(tv_content);
        } else if (post.getContent().contains("!") && post.getContent().contains("[") && post.getContent().contains("]") && post.getContent().contains("(") && post.getContent().contains(")")) {
            RichText.from(post.getContent()).type(RichType.MARKDOWN).autoFix(true).clickable(true).noImage(false).urlClick(
                    onUrlClickListener).imageClick(new OnImageClickListener() {
                @Override
                public void imageClicked(List<String> imageUrls, int position) {
                    PhotoViewer.INSTANCE
                            //设置当前位置
                            .setCurrentPage(0)
                            //设置图片控件容器
                            //他需要容器的目的是
                            //显示缩放动画
                            .setClickSingleImg(imageUrls.get(position), tv_content)
                            //设置图片加载回调
                            .setShowImageViewInterface((imageView, url) -> {
                                RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                        .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                //使用Glide显示图片
                                Glide.with(ContentActivity.this)
                                        .load(url)
                                        .apply(options).into(imageView);
                            })
                            //启动界面
                            .start(ContentActivity.this);
                }
            })
                    .into(tv_content);
        } else {
            tv_content.setText(post.getContent());
            selectImage(postId);
        }
        master.setOnLongClickListener(view -> {
            AppUtils.copy(tv_content.getText().toString(), this);
            AppUtils.Toast.shouToast(ContentActivity.this, "复制成功");
            return false;
        });
        master.setOnClickListener(view -> {

        });
        tv_type.setText(post.getCategoryName());
        tv_type.setOnClickListener(v -> {
            selectCategory(post.getCategoryId());
        });
        tv_time.setText(TimeUtils.calculate(TimeUtils.timeToStamp(post.getCreateTime())));
        tv_name.setText(post.getUserName());
        tv_title.setText(post.getTitle());
        riv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.start(ContentActivity.this, (long) post.getUserId());
            }
        });

        RequestOptions options = new RequestOptions()
                .fallback(R.drawable.error).error(R.drawable.error);
        Glide.with(this).load(post.getHeadSculpture()).apply(options).into(riv_head);
        selectComment(i, 20);
        if (user != null) {
            if (user.getRole() == 2 || user.getUserId() == post.getUserId()) {
                tv_del.setVisibility(View.VISIBLE);
                tv_title.setPadding(20, 0, 0, 0);
            }
        }
    }

    private void selectCategory(int categoryId) {
        OkHttpUtils.getInstance().doget("/getCategory", new INetCallback<Result<List<Category>>>() {

            @Override
            public void onSuccess(Result<List<Category>> data) {
                if (data.getStatus() == 10000) {
                    for (Category category : data.getData()) {
                        if (category.getCategoryId() == categoryId) {
                            MajorActivity.start(ContentActivity.this, category);
                        }
                    }
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<Category>>>() {
                }.getType();
            }
        });
    }

    private void selectImage(Long postId) {
        OkHttpUtils.getInstance().doget("/getPostImages?postId=" + postId, new INetCallback<Result<List<String>>>() {
            @Override
            public void onSuccess(Result<List<String>> data) {
                if (data.getStatus() == 10000 && data.getData().size() > 0) {
                    MyGridViewAdapter adapter = new MyGridViewAdapter(data.getData(), ContentActivity.this);
                    adapter.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(int position) {
                            PhotoViewer.INSTANCE
                                    //设置当前位置
                                    .setCurrentPage(0)
                                    //设置图片控件容器
                                    //他需要容器的目的是
                                    //显示缩放动画
                                    .setClickSingleImg(data.getData().get(position), tv_content)
                                    //设置图片加载回调
                                    .setShowImageViewInterface((imageView, url) -> {
                                        RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                                .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                        //使用Glide显示图片
                                        Glide.with(ContentActivity.this)
                                                .load(url)
                                                .apply(options).into(imageView);
                                    })
                                    //启动界面
                                    .start(ContentActivity.this);
                        }
                    });
                    rv_image.setLayoutManager(new GridLayoutManager(ContentActivity.this, 3));
                    rv_image.setAdapter(adapter);

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


    private void selectComment(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/selectComment?pageNum=" + pageNum + "&pageSize=" + pageSize + "&postId=" + postId, new INetCallback<Result<CommentVo>>() {
            @Override
            public void onSuccess(Result<CommentVo> data) {
                dismissLoading();
                if (data.getStatus() == 10000) {
                    list.clear();
                    list.addAll(data.getData().getList());
                    pages = data.getData().getPages();
                    if (pages != 0) {
                        tv_progress = findViewById(R.id.tv_progress);
                        tv_progress.setText(i + "/" + data.getData().getPages());
                    } else {
                        tv_progress = findViewById(R.id.tv_progress);
                        tv_progress.setText("0/0");
                    }
                    if (list.size() == 0) {
                        tv_null = findViewById(R.id.tv_null);
                        tv_null.setText("暂时还没有人评论哦~");
                        tv_null.setVisibility(View.VISIBLE);
                    } else {
                        tv_null = findViewById(R.id.tv_null);
                        tv_null.setVisibility(View.GONE);
                    }
                    if (adapter == null) {
                        adapter = new MyContentListAdapter(ContentActivity.this, list);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ContentActivity.this) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };
                        rv_postContent.setLayoutManager(layoutManager);
                        rv_postContent.setAdapter(adapter);
                        adapter.setOnClick((p, s) -> {
                            AppUtils.copy(s, ContentActivity.this);
                            AppUtils.Toast.shouToast(ContentActivity.this, "复制成功");
                        });
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (user != null && (list.get(position).getUserId() == user.getUserId() || post.getUserId() == user.getUserId() || user.getRole() == 2)) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(ContentActivity.this)
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
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    ContentActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                dismissLoading();
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<CommentVo>>() {
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

    private void delComment(int commentId, int position) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("commentId", commentId);
        OkHttpUtils.getInstance().dopost("/deleteComment", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                    AppUtils.Toast.shouToast(ContentActivity.this, "删除成功");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    ContentActivity.this.finish();
                } else {
                    AppUtils.Toast.shouToast(ContentActivity.this, "删除失败，请重试");
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(ContentActivity.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    public static void start(Context context, Long postId) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }

    public interface OnClick {
        void onClick(int position, String content);
    }

    private class MyContentListAdapter extends RecyclerView.Adapter<MyContentListAdapter.MyViewHolder> {
        private Context context;
        private List<CommentVo.ListBean> datasource;
        private com.kcbs.webforum.onLongClickListener onLongClickListener;
        private OnClick onClick;

        public void setOnClick(OnClick onClick) {
            this.onClick = onClick;
        }

        public void setOnLongClickListener(com.kcbs.webforum.onLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
        }

        public MyContentListAdapter(Context context, List<CommentVo.ListBean> datasource) {
            this.context = context;
            this.datasource = datasource;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_content_comment, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(list.get(position).getHeadSculpture()).apply(options).into(holder.riv_head);
            holder.tv_name.setText(list.get(position).getUsername());
            RichText.from(datasource.get(position).getContent()).autoFix(true).clickable(true)
                    .into(holder.tv_content);
            holder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onClick(position, datasource.get(position).getContent());
                }
            });
            holder.tv_time.setText(TimeUtils.calculate(TimeUtils.timeToStamp(list.get(position).getCommentTime())));
            holder.tv_num.setText(position + 1 + (i - 1) * 20 + "楼");
            holder.riv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserActivity.start(ContentActivity.this, (long) datasource.get(position).getUserId());
                }
            });
            if (datasource.get(position).getUserId() == post.getUserId()) {
                holder.tv_master.setText("楼主");
            }
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickListener.onLongClick(position);
                    return true;
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onClick(position, datasource.get(position).getContent());
                }
            });
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }


        private class MyViewHolder extends RecyclerView.ViewHolder {
            private RoundedImageView riv_head;
            private TextView tv_name, tv_time, tv_content, tv_num, tv_master;
            private ConstraintLayout root;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                riv_head = itemView.findViewById(R.id.riv_head);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_time = itemView.findViewById(R.id.tv_time);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_num = itemView.findViewById(R.id.tv_num);
                tv_master = itemView.findViewById(R.id.tv_master);
                root = itemView.findViewById(R.id.root);
            }
        }
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
        public MyGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new MyGridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyGridViewHolder holder, int position) {
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
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
        }
    }
}
