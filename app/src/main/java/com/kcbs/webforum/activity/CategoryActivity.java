package com.kcbs.webforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.event.LoginExpiredEvent;
import com.kcbs.webforum.event.RefreshEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.pojo.Category;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private ImageView iv_back;
    private RecyclerView rv_category;
    private List<Category> userCategoryList = new ArrayList<>();
    private RefreshEvent event;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.fitSystemBar(this);
        setContentView(R.layout.activity_category);
        try {
            user = (User) SavaDataUtils.getData(CategoryActivity.this, "User", 0).get(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            user = null;
        }
        init();
    }

    private void init() {
        iv_back = findViewById(R.id.iv_back);
        rv_category = findViewById(R.id.rv_category);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取用户关注分类列表
        if (user != null) {
            getUserCategory();
        } else {
            selectCategory();
        }
    }

    private void getUserCategory() {
        OkHttpUtils.getInstance().doget("/user/getCategory", new INetCallback<Result<List<Category>>>() {
            @Override
            public void onSuccess(Result<List<Category>> data) {
                if (data.getStatus() == 10000) {
                    userCategoryList.clear();
                    userCategoryList.addAll(data.getData());
                    selectCategory();
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    CategoryActivity.this.finish();
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

    private void selectCategory() {
        OkHttpUtils.getInstance().doget("/getCategory", new INetCallback<Result<List<Category>>>() {
            @Override
            public void onSuccess(Result<List<Category>> data) {
                if (data.getStatus() == 10000) {
                    rv_category.setLayoutManager(new LinearLayoutManager(WebApplication.getContext()));
                    rv_category.setAdapter(new MyListAdapter(WebApplication.getContext(), data.getData()));
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

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {
        private List<Category> categories;
        private Context context;

        public MyListAdapter(Context context, List<Category> categoryList) {
            this.context = context;
            this.categories = categoryList;
        }

        @NonNull
        @Override
        public MyListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            MyListAdapter.MyViewHolder holder = new MyListAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyListAdapter.MyViewHolder holder, int position) {

            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(categories.get(position).getCategoryImage()
            )

                    .apply(options).into(holder.iv_CategoryImage);
            holder.tv_categoryName.setText(categories.get(position).getCategoryName());
            if (userCategoryList.size() != 0) {
                for (Category category : userCategoryList) {
                    if (category.getCategoryId() == categories.get(position).getCategoryId()) {
                        holder.action_fallow.setText("取消");
                        holder.action_fallow.setTextColor(WebApplication.getContext().getResources().getColor(R.color.colorGrey));
                    }
                }
            } else {
                holder.action_fallow.setText("关注");
                holder.action_fallow.setTextColor(WebApplication.getContext().getResources().getColor(R.color.colorYellow));
            }
            holder.action_fallow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.action_fallow.getText().toString().equals("关注")) {
                        fallow(categories.get(position).getCategoryId(), holder.action_fallow);
                    }
                    if (holder.action_fallow.getText().toString().equals("取消")) {
                        delfallow(categories.get(position).getCategoryId(), holder.action_fallow);
                    }
                }
            });
            holder.root.setOnClickListener(v->{
                MajorActivity.start(CategoryActivity.this,categories.get(position));
            });
        }

        @Override
        public int getItemCount() {
            return categories == null ? 0 : categories.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv_CategoryImage;
            private TextView tv_categoryName;
            private TextView action_fallow;
            private CardView root;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_CategoryImage = itemView.findViewById(R.id.iv_CategoryImage);
                tv_categoryName = itemView.findViewById(R.id.tv_categoryName);
                action_fallow = itemView.findViewById(R.id.action_fallow);
                root = itemView.findViewById(R.id.root);
            }
        }
    }

    private void delfallow(Long categoryId, TextView view) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("categoryId", categoryId);
        OkHttpUtils.getInstance().dodelete("/user/subscribe", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    view.setText("关注");
                    view.setTextColor(WebApplication.getContext().getResources().getColor(R.color.colorYellow));
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    CategoryActivity.this.finish();
                }
                AppUtils.Toast.shouToast(WebApplication.getContext(), data.getMsg());
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(WebApplication.getContext(), "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    private void fallow(Long categoryId, TextView view) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("categoryId", categoryId);
        OkHttpUtils.getInstance().doput("/user/subscribe", map, new INetCallback<Result<String>>() {

            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus() == 10000) {
                    view.setText("取消");
                    view.setTextColor(WebApplication.getContext().getResources().getColor(R.color.colorGrey));
                    return;
                } else if (data.getStatus() == 10020) {
                    LoginExpiredEvent event = new LoginExpiredEvent();
                    EventBus.getDefault().post(event);
                    CategoryActivity.this.finish();
                } else if (data.getStatus() == 10009) {
                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                }
                AppUtils.Toast.shouToast(WebApplication.getContext(), data.getMsg());
            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(WebApplication.getContext(), "网络异常");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>() {
                }.getType();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        event = new RefreshEvent();
        EventBus.getDefault().postSticky(event);
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
        }
    }
}
