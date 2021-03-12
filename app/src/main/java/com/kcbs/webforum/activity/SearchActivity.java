package com.kcbs.webforum.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.kcbs.webforum.event.DelEvent;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.onLongClickListener;
import com.kcbs.webforum.pojo.PageInfoUser;
import com.kcbs.webforum.pojo.PostResult;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.kcbs.webforum.utils.TimeUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanglu.photoviewerlibrary.PhotoViewer;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private int role;
    private SearchView searchView;
    private TextView cancel, tv_post, tv_people;
    private boolean ispost = true;
    private List<PostResult.ListBean> list = new ArrayList<>();
    private List<PageInfoUser.DataBean.ListBean> users = new ArrayList<>();
    private MyListAdapter adapter;
    private MyUserListAdapter userAdapter;
    private RecyclerView result_list, resultUser_list;
    private MyHandler handler = new MyHandler(this);
    private Message message;
    private int position = -1;
    private String nows = "";

    @Subscribe(sticky = true)
    public void delEvent(DelEvent event) {
        list.remove(position);
        adapter.notifyDataSetChanged();
    }

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusBar.fitSystemBar(this);
        role = getIntent().getIntExtra("role", 1);
        init();
    }

    private void init() {
        tv_post = findViewById(R.id.tv_post);
        tv_people = findViewById(R.id.tv_people);
        tv_post.setTextColor(getResources().getColor(R.color.colorTextBlue));
        tv_people.setTextColor(getResources().getColor(R.color.colorBlack));
        result_list = findViewById(R.id.result_list);
        resultUser_list = findViewById(R.id.resultUser_list);
        tv_post.setOnClickListener(v -> {
            if (!ispost) {
                ispost = true;
                tv_post.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tv_people.setTextColor(getResources().getColor(R.color.colorBlack));
                search(nows, 1, 20);
            }
        });
        tv_people.setOnClickListener(v -> {
            if (ispost) {
                ispost = false;
                tv_post.setTextColor(getResources().getColor(R.color.colorBlack));
                tv_people.setTextColor(getResources().getColor(R.color.colorTextBlue));
                searchUser(nows, 1, 20);
            }
        });
        searchView = findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        cancel = findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                nows = s;
                if (ispost) {
                    search(s, 1, 20);
                } else {
                    searchUser(s, 1, 20);
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                nows = s;
                if (!s.equals("")) {
                    handler.removeMessages(10);
                    message = new Message();
                    message.what = 10;
                    message.obj = s;
                    handler.sendMessageDelayed(message, 500);
                } else {
                    handler.removeMessages(10);
                    if (ispost) {
                        if (adapter != null) {
                            list.clear();
                            users.clear();
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if (userAdapter != null) {
                            users.clear();
                            list.clear();
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                }
                return false;
            }
        });
    }

    private void search(String s, int pageNum, int pageSize) {
        String url = String.format("/searchByString?pageNum=%d&pageSize=%d&content=%s", pageNum, pageSize, URLEncoder.encode(s));
        OkHttpUtils.getInstance().doget(url, new INetCallback<Result<PostResult>>() {
            @Override
            public void onSuccess(Result<PostResult> data) {
                if (data.getStatus() == 10000) {
                    list.clear();
                    if (data.getData().getList() != null) {
                        list.addAll(data.getData().getList());
                    }
                    if (list.size() == 0) {
                        AppUtils.Toast.shouToast(SearchActivity.this, "暂时没有更多内容了~");
                    }
                    if (adapter == null) {
                        result_list.setVisibility(View.VISIBLE);
                        resultUser_list.setVisibility(View.GONE);
                        adapter = new MyListAdapter(SearchActivity.this, list);
                        result_list.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        result_list.setAdapter(adapter);
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (role == 2) {
                                    del(position);
                                }
                            }
                        });
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                SearchActivity.this.position = position;
                                ContentActivity.start(SearchActivity.this, (long) list.get(position).getPostId());
                            }
                        });
                    } else {
                        result_list.setVisibility(View.VISIBLE);
                        resultUser_list.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    SearchActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult>>() {
                }.getType();
            }
        });
    }

    private void searchUser(String s, int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/searchUserByString?pageNum=" + pageNum + "&pageSize=" + pageSize + "&content=" + URLEncoder.encode(s), new INetCallback<PageInfoUser>() {
            @Override
            public void onSuccess(PageInfoUser data) {
                if (data.getStatus() == 10000) {
                    System.out.println(data);
                    users.clear();
                    if (data.getData().getList() != null) {
                        users.addAll(data.getData().getList());
                    }
                    if (users.size() == 0) {
                        AppUtils.Toast.shouToast(SearchActivity.this, "暂时没有更多内容了~");
                    }
                    if (userAdapter == null) {
                        result_list.setVisibility(View.GONE);
                        resultUser_list.setVisibility(View.VISIBLE);
                        userAdapter = new MyUserListAdapter(SearchActivity.this, users);
                        userAdapter.setOnClickListener(p -> {
                            SearchActivity.this.position = p;
                            UserActivity.start(SearchActivity.this, (long) users.get(position).getUserId());
                        });
                        resultUser_list.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        resultUser_list.setAdapter(userAdapter);
                    } else {
                        result_list.setVisibility(View.GONE);
                        resultUser_list.setVisibility(View.VISIBLE);
                        userAdapter.notifyDataSetChanged();
                    }
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    SearchActivity.this.finish();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<PageInfoUser>() {
                }.getType();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void del(int position) {
        AlertDialog dialog = new AlertDialog.Builder(SearchActivity.this)
                .setMessage("您确定要删除\"PostId:" + list.get(position).getPostId() + ",标题为:" + list.get(position).getTitle() + "\"吗?")
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

    private void deletepost(int position) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("postId", list.get(position).getPostId());
        OkHttpUtils.getInstance().dopost("/deletePost", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    AppUtils.Toast.shouToast(SearchActivity.this, "删除成功");
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    SearchActivity.this.finish();
                } else {
                    AppUtils.Toast.shouToast(SearchActivity.this, "删除失败,请重试");
                }

            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(SearchActivity.this, "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    public static void start(Context context, int role) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("role", role);
        context.startActivity(intent);
    }

    private class MyUserListAdapter extends RecyclerView.Adapter<MyUserListAdapter.ViewHolder> {
        private Context context;
        private List<PageInfoUser.DataBean.ListBean> datasource;
        private onClickListener onClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public MyUserListAdapter(Context context, List<PageInfoUser.DataBean.ListBean> list) {
            this.context = context;
            this.datasource = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_myfallow, parent, false);
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
            holder.tv_btn.setVisibility(View.GONE);

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
                tv_btn = itemView.findViewById(R.id.tv_btn);
                root = itemView.findViewById(R.id.root);
            }
        }
    }

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<PostResult.ListBean> datasource;
        private onLongClickListener onLongClickListener;
        private onClickListener onClickListener;

        public void setOnClickListener(onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnLongClickListener(onLongClickListener onLongClickListener) {
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
            return new MyListAdapter.ViewHolder(view);
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
            selectCommentNum(datasource.get(position).getPostId(), holder.tv_commentnum);
            holder.tv_time.setText("更新于"+ TimeUtils.timeTocalculate(datasource.get(position).getUpdateTime()));
            holder.root.setOnLongClickListener(view -> {
                onLongClickListener.onLongClick(position);
                return true;
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout root;
            private TextView tv_title, tv_content, tv_name, tv_commentnum, tv_time;
            private RecyclerView rv_post;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_commentnum = itemView.findViewById(R.id.tv_commentnum);
                tv_time = itemView.findViewById(R.id.tv_time);
                root = itemView.findViewById(R.id.root);
                rv_post = itemView.findViewById(R.id.rv_post);
            }
        }
    }

    private void selectImage(int postId, RecyclerView rv_post) {
        OkHttpUtils.getInstance().doget("/getPostImages?postId=" + postId, new INetCallback<Result<List<String>>>() {
            @Override
            public void onSuccess(Result<List<String>> data) {
                if (data.getStatus() == 10000) {
                    MyGridViewAdapter adapter = new MyGridViewAdapter(data.getData(), SearchActivity.this);
                    adapter.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(int position) {
                            PhotoViewer.INSTANCE
                                    .setClickSingleImg(data.getData().get(position), rv_post)
                                    .setShowImageViewInterface((imageView, url) -> {
                                        RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                                .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                        //使用Glide显示图片
                                        Glide.with(SearchActivity.this)
                                                .load(url)

                                                .apply(options).into(imageView);
                                    }).start(SearchActivity.this);
                        }
                    });
                    rv_post.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
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
        public MyGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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


    private void selectCommentNum(int postId, TextView tv_commentnum) {
        OkHttpUtils.getInstance().doget("/getPostCommentNum?postId=" + postId, new INetCallback<Result<Integer>>() {
            @Override
            public void onSuccess(Result<Integer> data) {
                if (data.getStatus() == 10000) {
                    tv_commentnum.setText(data.getData() + "");
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    SearchActivity.this.finish();
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

    //弱引用 防止内存泄漏
    private static class MyHandler extends Handler {
        private WeakReference<SearchActivity> weakReference;

        public MyHandler(SearchActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SearchActivity activity = weakReference.get();
            switch (msg.what) {
                case 10:
                    if (activity.ispost) {
                        activity.search((String) msg.obj, 1, 20);
                    } else {
                        activity.searchUser((String) msg.obj, 1, 20);
                    }
                    break;
            }
        }
    }


}
