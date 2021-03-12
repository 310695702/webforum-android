package com.kcbs.webforum.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.WebApplication;
import com.kcbs.webforum.activity.CategoryActivity;
import com.kcbs.webforum.activity.LoginActivity;
import com.kcbs.webforum.activity.MajorActivity;
import com.kcbs.webforum.activity.MsgActivity;
import com.kcbs.webforum.activity.MyfallowActivity;
import com.kcbs.webforum.event.RefreshEvent;
import com.kcbs.webforum.event.ToAdminEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.pojo.Category;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.Helper;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;
import com.kcbs.webforum.utils.StatusBar;
import com.zenglb.downloadinstaller.DownloadInstaller;
import com.zenglb.downloadinstaller.DownloadProgressCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class WebforumFragment extends Fragment {
    private View rootView;
    private ImageView iv_addCategory, iv_toAdmin;
    private RecyclerView rv_category;
    private List<Category> mycategoryList = new ArrayList<>();
    private MyListAdapter adapter;
    private SwipeRefreshLayout rf_refresh;
    private User user;
    private TextView tv_Webtitle, tv_title;
    private MyHandler myHandler = new MyHandler(this);
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int REQUEST_EXTERNAL_STORAGE = 1; // 不可改
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            sharedPreferences = getActivity().getSharedPreferences("User",Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            rootView = inflater.inflate(R.layout.fragment_webforum, container, false);
            checkVersion();
            try {
                user = (User) SavaDataUtils.getData(getContext(), "User", 0).get(0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }catch (Exception e){
                user = null;
            }
            init(rootView);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            selectNew();
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void checkPermission(Result<List<Double>> data) {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if ((permission != PackageManager.PERMISSION_GRANTED) || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            AppUtils.Toast.shouToast(getContext(),"开始下载更新");
            new DownloadInstaller(getContext(), WebApplication.HOST + "/download/webforum-" + data.getData().get(1) + ".apk?100", true, new DownloadProgressCallBack() {
                @Override
                public void downloadProgress(int progress) {

                }

                @Override
                public void downloadException(Exception e) {
                    AppUtils.copy(WebApplication.HOST + "/download/webforum-" + data.getData().get(1) + ".apk?100", getContext());
                    AppUtils.Toast.shouToast(getContext(), "自动更新失败，已为您复制下载连接，您可以到浏览器打开，自行下载更新");
                }

                @Override
                public void onInstallStart() {
                    checkVersion();
                }
            }).start();
        }
    }


    private void selectNew() {

        if (sharedPreferences.getString("token",null)!=null){
            OkHttpUtils.getInstance().doget("/user/state", new INetCallback<Result<Integer>>() {
                @Override
                public void onSuccess(Result<Integer> data) {
                    if (data.getStatus() == 10000) {
                        Message message = myHandler.obtainMessage();
                        message.arg1 = data.getData();
                        message.what = 1;
                        myHandler.sendMessage(message);
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
    }

    private void checkVersion() {
        OkHttpUtils.getInstance().doget("/GetVersion", new INetCallback<Result<List<Double>>>() {
            @Override
            public void onSuccess(Result<List<Double>> data) {
                if (data.getStatus() == 10000 && data.getData()!=null) {
                    if (data.getData().get(0) - AppUtils.getVersionCode(getContext()) >= 1) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setMessage("有新版本需要更新~")
                                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getActivity().finish();
                                        System.exit(0);
                                    }
                                })
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        checkPermission(data);
                                        checkVersion();
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dialog.show();
                    } else if (!data.getData().get(1).toString().equals(AppUtils.getVersionName(getContext()))) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setMessage("检测到有新版本，您是否需要更新？")
                                .setPositiveButton("取消", null)
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        checkPermission(data);
                                    }
                                })
                                .create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                    }
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<Double>>>() {
                }.getType();
            }
        });
    }

    private void init(View view) {
        iv_addCategory = view.findViewById(R.id.iv_addCategory);
        tv_title = view.findViewById(R.id.tv_title);
        iv_addCategory.setOnTouchListener((v, e) -> {
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
        tv_Webtitle = view.findViewById(R.id.tv_Webtitle);
        rv_category = view.findViewById(R.id.rv_category);
        rf_refresh = view.findViewById(R.id.rf_refresh);
        iv_toAdmin = view.findViewById(R.id.iv_toAdmin);
        if (user!=null){
            if (user.getRole() == 2) {
                iv_toAdmin.setVisibility(View.VISIBLE);
                tv_Webtitle.setPadding(0, 0, 0, 0);
                iv_toAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToAdminEvent event = new ToAdminEvent();
                        EventBus.getDefault().post(event);
                    }
                });
            }
            selectUserCategory();

        }else {
            selectRecommendCategory();
        }

        iv_addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CategoryActivity.class));
            }
        });

        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (sharedPreferences.getString("token",null)!=null){
                    selectUserCategory();
                }else {
                    selectRecommendCategory();
                }
            }
        });
    }

    private void selectRecommendCategory() {
        OkHttpUtils.getInstance().doget("/getCategory", new INetCallback<Result<List<Category>>>() {
            @Override
            public void onSuccess(Result<List<Category>> data) {
                rf_refresh.setRefreshing(false);
                if (data.getStatus() == 10000) {
                    mycategoryList.clear();
                    for (Category category:data.getData()){
                        if (category.getIsRecommend()==1){
                            mycategoryList.add(category);
                        }
                    }
                    if (adapter == null) {
                        adapter = new MyListAdapter(getContext(), mycategoryList);
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                if (user!=null&&position == 0) {
                                    startActivity(new Intent(getContext(), MyfallowActivity.class));
                                } else if (user!=null&&position == 1) {
                                    startActivity(new Intent(getContext(), MsgActivity.class));
                                } else
                                    MajorActivity.start(getContext(), mycategoryList.get(position));
                            }
                        });
                        rv_category.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rv_category.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
                AppUtils.Toast.shouToast(getContext(), ex.getMessage());
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<Category>>>() {}.getType();
            }
        });
    }

    private void selectUserCategory() {
        OkHttpUtils.getInstance().doget("/user/getCategory", new INetCallback<Result<List<Category>>>() {

            @Override
            public void onSuccess(Result<List<Category>> data) {
                rf_refresh.setRefreshing(false);
                if (data.getStatus() == 10000) {
                    mycategoryList.clear();
                    mycategoryList.addAll(Helper.initCategory());
                    mycategoryList.addAll(data.getData());
                    selectNew();
                    if (adapter == null) {
                        adapter = new MyListAdapter(getContext(), mycategoryList);
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                if (position == 0) {
                                    startActivity(new Intent(getContext(), MyfallowActivity.class));
                                } else if (position == 1) {
                                    startActivity(new Intent(getContext(), MsgActivity.class));
                                } else
                                    MajorActivity.start(getContext(), mycategoryList.get(position));
                            }
                        });
                        rv_category.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rv_category.setAdapter(adapter);
                    } else {
                        selectNew();
                    }

                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
                AppUtils.Toast.shouToast(getContext(), ex.getMessage());
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<List<Category>>>() {
                }.getType();
            }
        });
    }


    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        private Context context;
        private List<Category> datasource;
        private com.kcbs.webforum.onClickListener onClickListener;

        public void setOnClickListener(onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public MyListAdapter(Context context, List<Category> datasource) {
            this.context = context;
            this.datasource = datasource;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_web_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.numCard.setVisibility(View.GONE);
            if (datasource.get(position).getCount()!=0&&position==1) {
                holder.numCard.setVisibility(View.VISIBLE);
                holder.tv_count.setText(datasource.get(position).getCount()+"");
            }
            RequestOptions options = new RequestOptions()
                    .fallback(R.drawable.error).error(R.drawable.error);
            Glide.with(context).load(datasource.get(position).getCategoryImage()).apply(options).into(holder.iv_CategoryImage);
            holder.tv_categoryName.setText(datasource.get(position).getCategoryName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
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
            private ImageView iv_CategoryImage;
            private TextView tv_categoryName,tv_count;
            private CardView cardView,numCard;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_CategoryImage = itemView.findViewById(R.id.iv_CategoryImage);
                tv_categoryName = itemView.findViewById(R.id.tv_categoryName);
                cardView = itemView.findViewById(R.id.rootView);
                numCard = itemView.findViewById(R.id.numCard);
                tv_count = itemView.findViewById(R.id.tv_count);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void refreshEvent(RefreshEvent event) {
        if (user!=null){
            selectUserCategory();
        }else {
            selectRecommendCategory();
        }
    }


    //弱引用 防止内存泄漏
    private static class MyHandler extends Handler {
        private WeakReference<WebforumFragment> weakReference;

        public MyHandler(WebforumFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            WebforumFragment fragment = weakReference.get();
            switch (msg.what) {
                case 1:
                    if (fragment.mycategoryList.size()>=2&&fragment.adapter!=null&&msg.arg1!=fragment.mycategoryList.get(1).getCount()){
                        fragment.mycategoryList.get(1).setCount(msg.arg1);
                        fragment.adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

}
