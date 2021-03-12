package com.kcbs.webforum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.dialog.LoadingDialog;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.event.PostSuccessEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.pojo.Category;
import com.kcbs.webforum.pojo.PostResult;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.Helper;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.kcbs.webforum.utils.TimeUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanglu.photoviewerlibrary.PhotoViewer;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MajorActivity extends AppCompatActivity {
    private TextView tv_title, tv_categoryName, tv_num, tv_speak;
    private Category category;
    private ImageView iv_category, iv_search;
    private RecyclerView rv_majoycategory, rv_title;
    private List<PostResult.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private SwipeRefreshLayout rf_refresh;
    private int i = 1, lastposition = 0;
    private User user;
    private boolean onCreate;
    private View lastView;
    private String orderBy;
    private TextView tv_null, lastTv;
    private int lasti;
    private FloatingActionButton fab;
    private List<String> titleList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major);
        onCreate = true;
        StatusBar.fitSystemBar(this);
        StatusBar.lightStatusBar(this, false);
        category = (Category) getIntent().getSerializableExtra("Category");
        if (category != null) {
            try {
                user = (User) SavaDataUtils.getData(this, "User", 0).get(0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                user = null;
            }
            init();
        }
    }


    //OnClick事件
    public void MajoyOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void init() {
        fab = findViewById(R.id.fab);
        fab.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private int startX;
            private int move_bigX, move_bigY;
            private long startTime;
            private boolean isLongClick;
            Display display = getWindowManager().getDefaultDisplay();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取当前按下的坐标
                        isLongClick = false;
                        startTime = System.currentTimeMillis();
                        fab.setAlpha(0.6f);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取移动后的坐标
                        if (!isLongClick && System.currentTimeMillis() - startTime > 600) {
                            Vibrator vibrator = (Vibrator) MajorActivity.this.getSystemService(MajorActivity.this.VIBRATOR_SERVICE);
                            vibrator.vibrate(50);
                            isLongClick = true;
                        }
                        if (isLongClick) {
                            int moveX = (int) event.getRawX();
                            int moveY = (int) event.getRawY();
                            //拿到手指移动距离的大小
                            move_bigX = moveX - startX;
                            move_bigY = moveY - startY;
                            //拿到当前控件未移动的坐标
                            int left = fab.getLeft();
                            int top = fab.getTop();
                            left += move_bigX;
                            left = left < 0 ? 0 : left;
                            top += move_bigY;
                            top = top < fab.getHeight() ? fab.getHeight() : top;
                            int right = left + fab.getWidth();
                            right = right > display.getWidth() ? display.getWidth() : right;
                            left = right - fab.getWidth();
                            int bottom = top + fab.getHeight();
                            bottom = bottom > display.getHeight() ? display.getHeight() : bottom;
                            top = bottom - fab.getHeight();
                            fab.layout(left, top, right, bottom);
                            startX = moveX;
                            startY = moveY;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        fab.setAlpha(1f);
                        if (move_bigX == 0 && move_bigY == 0 && !isLongClick) {
                            if (user != null) {
                                NewPostActivity.start(MajorActivity.this, category.getCategoryId());
                            } else {
                                startActivity(new Intent(MajorActivity.this, LoginActivity.class));
                            }
                        }
                        break;
                }
                return true;//此处一定要返回true，否则监听不生效
            }
        });

        tv_title = findViewById(R.id.tv_title);
        tv_null = findViewById(R.id.tv_null);
        tv_title.setText(category.getCategoryName());
        iv_category = findViewById(R.id.iv_category);
        iv_category.setOnClickListener(v -> {
            PhotoViewer.INSTANCE
                    .setClickSingleImg(category.getCategoryImage(), iv_category)
                    .setShowImageViewInterface((imageView, url) -> {
                        RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                        //使用Glide显示图片
                        Glide.with(MajorActivity.this)
                                .load(url)

                                .apply(options).into(imageView);
                    }).start(MajorActivity.this);
        });
        rv_title = findViewById(R.id.rv_title);
        rv_majoycategory = findViewById(R.id.rv_majoycategory);
        initTitle();
        RequestOptions options = new RequestOptions()
                .fallback(R.drawable.error).error(R.drawable.error);
        Glide.with(this).load(Uri.parse(category.getCategoryImage())).apply(options).into(iv_category);
        tv_categoryName = findViewById(R.id.tv_categoryName);
        tv_categoryName.setText(category.getCategoryName());
        tv_num = findViewById(R.id.tv_num);
        iv_search = findViewById(R.id.iv_search);
        tv_speak = findViewById(R.id.tv_speak);
        tv_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TcpActivity.start(MajorActivity.this, category.getCategoryName());
            }
        });
        iv_search.setOnTouchListener((v, e) -> {
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
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    SearchActivity.start(MajorActivity.this, user.getRole());
                } else {
                    SearchActivity.start(MajorActivity.this, 1);
                }
            }
        });
        selectPostByCategory(category.getCategoryId(), 1, 20);
        rf_refresh = findViewById(R.id.rf_refresh);
        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
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
    }

    //初始化标签
    private void initTitle() {
        rv_title.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        titleList.clear();
        titleList.addAll(Helper.StringList());
        MyTitleAdapter adapter = new MyTitleAdapter(titleList, this);
        rv_title.setAdapter(adapter);
        adapter.setOnClickListener((v, p, tv) -> {
            showLoading();
            if (lastposition != p) {
                ((CardView) lastView).setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                lastTv.setTextColor(Color.BLACK);
            }
            ((CardView) v).setCardBackgroundColor(getResources().getColor(R.color.colorRed));
            tv.setTextColor(Color.WHITE);
            lastposition = p;
            lastView = v;
            lastTv = tv;
            i = 1;
            this.list.clear();
            selectPostByCategory(category.getCategoryId(), 1, 20);
        });

    }

    //刷新
    private void refresh() {
        i = 1;
        list.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        selectPostByCategory(category.getCategoryId(), i, 20);
    }

    //查帖子
    private void selectPostByCategory(Long categoryId, int pageNum, int pageSize) {
        switch (lastposition) {
            case 0:
                orderBy = "";
                break;
            case 1:
                orderBy = "&orderBy=2";
                break;
            case 2:
                orderBy = "&orderBy=4";
                break;
        }
        OkHttpUtils.getInstance().doget("/getPost?categoryId=" + categoryId + "&pageNum=" + pageNum + "&pageSize=" + pageSize + orderBy
                , new INetCallback<Result<PostResult>>() {
                    @Override
                    public void onSuccess(Result<PostResult> data) {
                        dismissLoading();
                        rf_refresh.setRefreshing(false);
                        if (data.getStatus() == 10000) {
                            list.addAll(data.getData().getList());
                            if (list.size() == 0) {
                                tv_null.setVisibility(View.VISIBLE);
                                tv_null.setText("没有更多内容了~");
                            } else {
                                tv_null.setVisibility(View.GONE);
                            }
                            tv_num.setText(category.getPostNum() * 1234 + "");
                            if (adapter == null) {
                                adapter = new MyListAdapter(MajorActivity.this, list);
                                adapter.setOnClickListener(new onClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        ContentActivity.start(MajorActivity.this, (long) list.get(position).getPostId());
                                    }
                                });
                                rv_majoycategory.setLayoutManager(new LinearLayoutManager(MajorActivity.this));
                                rv_majoycategory.setAdapter(adapter);
                                rv_majoycategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                                            if (data.getData().getPages() > i) {
                                                showLoading();
                                                i++;
                                                selectPostByCategory(categoryId, i, 20);
                                            } else {
                                                AppUtils.Toast.shouToast(MajorActivity.this, "到底了~");
                                            }
                                        }
                                    }
                                });
                                ;
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else if (data.getStatus() == 10020) {
                            LoginExpiredEvent event = new LoginExpiredEvent();
                            EventBus.getDefault().post(event);
                            MajorActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailed(Throwable ex) {
                        rf_refresh.setRefreshing(false);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(1);
                                    dismissLoading();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }

                    @Override
                    public Type getType() {
                        return new TypeToken<Result<PostResult>>() {
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

    public static void start(Context context, Category category) {
        Intent intent = new Intent(context, MajorActivity.class);
        intent.putExtra("Category", category);
        context.startActivity(intent);
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
            holder.tv_commentnum.setText(datasource.get(position).getCommentNum() + "");
            holder.tv_time.setText("最后更新于" + TimeUtils.calculate(TimeUtils.timeToStamp(datasource.get(position).getUpdateTime())));
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position);
                }
            });
            RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                    .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
            Glide.with(MajorActivity.this).load(datasource.get(position).getHeadSculpture()).apply(options).into(holder.riv_head);
            if (datasource.get(position).getCommentNum() > 10) {
                holder.iv_hot.setVisibility(View.VISIBLE);
            } else {
                holder.iv_hot.setVisibility(View.GONE);
            }
            if (datasource.get(position).getIsEssences() == 1) {
                holder.iv_jing.setVisibility(View.VISIBLE);
            } else {
                holder.iv_jing.setVisibility(View.GONE);
            }
            //setBackGround(holder.root);
        }

        @Override
        public int getItemCount() {
            return datasource == null ? 0 : datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_title, tv_content, tv_name, tv_commentnum, tv_time;
            private ConstraintLayout root;
            private RoundedImageView riv_head;
            private RecyclerView rv_post;
            private ImageView iv_jing, iv_hot;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_commentnum = itemView.findViewById(R.id.tv_commentnum);
                tv_time = itemView.findViewById(R.id.tv_time);
                root = itemView.findViewById(R.id.root);
                riv_head = itemView.findViewById(R.id.riv_head);
                rv_post = itemView.findViewById(R.id.rv_post);
                iv_jing = itemView.findViewById(R.id.iv_jing);
                iv_hot = itemView.findViewById(R.id.iv_hot);
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
                    MyGridViewAdapter adapter = new MyGridViewAdapter(data.getData(), MajorActivity.this);
                    adapter.setOnClickListener(new onClickListener() {
                        @Override
                        public void onClick(int position) {
                                PhotoViewer.INSTANCE
                                        //设置当前位置
                                        //设置图片控件容器
                                        //他需要容器的目的是
                                        //显示缩放动画
                                        .setClickSingleImg(data.getData().get(position), rv_post)
                                        //设置图片加载回调
                                        .setShowImageViewInterface((imageView, url) -> {
                                            RequestOptions options = new RequestOptions()//图片加载出来前，显示的图片
                                                    .fallback(R.drawable.error).error(R.drawable.error);//图片加载失败后，显示的图片
                                            //使用Glide显示图片
                                            Glide.with(MajorActivity.this)
                                                    .load(url)
                                                    .apply(options).into(imageView);
                                        })
                                        //启动界面
                                        .start(MajorActivity.this);
                        }
                    });
                    rv_post.setLayoutManager(new GridLayoutManager(MajorActivity.this, 3));
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

    public interface MyTitleOnClickListener {
        void onClick(View v, int position, TextView tv);
    }

    //标签适配器
    private class MyTitleAdapter extends RecyclerView.Adapter<MyTitleAdapter.ViewHolder> {
        private List<String> datesource;
        private Context context;
        private MyTitleOnClickListener onClickListener;

        public void setOnClickListener(MyTitleOnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public MyTitleAdapter(List<String> datesource, Context context) {
            this.datesource = datesource;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_major_title, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tv_name.setText(datesource.get(position));
            holder.cardview.setOnClickListener(v -> {
                onClickListener.onClick(holder.cardview, position, holder.tv_name);
            });
            if (onCreate) {
                //onCreate用来判断是否为第一次启动Activity
                onCreate = false;
                holder.cardview.performClick();
            }
        }

        @Override
        public int getItemCount() {
            return datesource == null ? 0 : datesource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_name;
            private CardView cardview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                cardview = itemView.findViewById(R.id.cardview);
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
            holder.iv_image.setOnClickListener(v -> {
                onClickListener.onClick(position);
            });
        }

        @Override
        public int getItemCount() {
            return urls == null ? 0 : urls.size();
        }

        class MyGridViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv_image, iv_imagecancel;
            private RelativeLayout root;

            public MyGridViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_image = itemView.findViewById(R.id.iv_image);
                iv_imagecancel = itemView.findViewById(R.id.iv_imagecancel);
                root = itemView.findViewById(R.id.root);
            }
        }
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

    @Subscribe(sticky = true)
    public void onPostSuccessEvent(PostSuccessEvent event) {
        refresh();
    }
}
