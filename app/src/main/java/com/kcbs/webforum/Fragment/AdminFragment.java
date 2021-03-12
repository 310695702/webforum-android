package com.kcbs.webforum.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;
import com.kcbs.webforum.R;
import com.kcbs.webforum.activity.AddCategoryActivity;
import com.kcbs.webforum.activity.ContentActivity;
import com.kcbs.webforum.activity.MyfallowActivity;
import com.kcbs.webforum.activity.SearchActivity;
import com.kcbs.webforum.event.LogoutEvent;
import com.kcbs.webforum.event.ToUserEvent;
import com.kcbs.webforum.net.INetCallback;
import com.kcbs.webforum.onClickListener;
import com.kcbs.webforum.onLongClickListener;
import com.kcbs.webforum.pojo.PostResult;
import com.kcbs.webforum.pojo.Result;
import com.kcbs.webforum.pojo.User;
import com.kcbs.webforum.utils.AppUtils;
import com.kcbs.webforum.utils.OkHttpUtils;
import com.kcbs.webforum.utils.SavaDataUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminFragment extends Fragment {
    private View view;
    private RecyclerView rv_post;
    private List<PostResult.ListBean> list = new ArrayList<>();
    private MyListAdapter adapter;
    private int i = 1;
    private SwipeRefreshLayout rf_refresh;
    private ImageView iv_logout,iv_rollback,iv_search,iv_relese;
    private EditText et_essences;
    private TextView tv_tool,tv_addCategory,tv_addEssences,tv_cancelEssences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_admin,container,false);
            init(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent!=null){
            parent.removeView(view);
        }
        return view;
    }

    private void init(View view) {
        rv_post = view.findViewById(R.id.rv_post);
        rf_refresh = view.findViewById(R.id.rf_refresh);
        iv_logout = view.findViewById(R.id.iv_logout);
        iv_relese = view.findViewById(R.id.iv_relese);
        tv_addCategory = view.findViewById(R.id.tv_addCategory);
        tv_addEssences = view.findViewById(R.id.tv_addEssences);
        tv_cancelEssences = view.findViewById(R.id.tv_cancelEssences);
        et_essences = view.findViewById(R.id.et_essences);
        tv_addEssences.setOnClickListener(v->{
            addEssences();
        });
        tv_cancelEssences.setOnClickListener(v->{
            cancelEssences();
        });
        tv_addCategory.setOnClickListener(v->{
            startActivity(new Intent(getContext(), AddCategoryActivity.class));
        });
        iv_relese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToUserEvent event = new ToUserEvent();
                EventBus.getDefault().post(event);
            }
        });
        iv_rollback = view.findViewById(R.id.iv_rollback);
        iv_search = view.findViewById(R.id.iv_search);
        iv_search.setOnTouchListener((v,e)->{
            if (e.getAction() == MotionEvent.ACTION_DOWN){
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_CANCEL){
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;});
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SearchActivity.start(getContext(), ((User)SavaDataUtils.getData(getContext(),"User",0).get(0)).getRole());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                }
            }
        });
        selectPost(1,20);
        iv_logout.setOnTouchListener((v,e)->{
            if (e.getAction() == MotionEvent.ACTION_DOWN){
                v.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_CANCEL){
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
            return false;});
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setMessage("你要退出登录嘛?")
                        .setPositiveButton("不", null)
                        .setNegativeButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        })
                        .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color40Blue));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color40Blue));
            }
        });

        rf_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                i=1;
                list.clear();
                if (tv_tool.getText().toString().contains("未")){
                    selectPost(1,20);
                    return;
                }
                if (tv_tool.getText().toString().contains("回")){
                    selectrollbackport(1,20);
                    return;
                }
            }
        });
        rv_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)&&recyclerView.canScrollVertically(-1)) {
                    i++;
                    selectPost(i,20);
                }
            }
        });
        tv_tool = view.findViewById(R.id.tv_tool);
        iv_rollback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=1;
                list.clear();
                System.out.println(tv_tool.getText());
                if (tv_tool.getText().equals("管理员(未被删除帖子)")){
                    tv_tool.setText("管理员(回收站)");
                    selectrollbackport(1,20);
                    return;
                }
                if (tv_tool.getText().equals("管理员(回收站)")){
                    tv_tool.setText("管理员(未被删除帖子)");
                    selectPost(1,20);
                    return;
                }
            }
        });

    }

    private void cancelEssences() {
        if (!et_essences.getText().toString().equals("")){
            HashMap<Object,Object> map = new HashMap<>();
            map.put("postId",et_essences.getText().toString());
            map.put("type",0);
            OkHttpUtils.getInstance().dopost("/admin/addEssences", map, new INetCallback<Result<String>>() {
                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus()==10000){
                        AppUtils.Toast.shouToast(getContext(),"取消加精成功!");
                    }else {
                        AppUtils.Toast.shouToast(getContext(),"取消加精失败!");
                    }
                }

                @Override
                public void onFailed(Throwable ex) {
                    AppUtils.Toast.shouToast(getContext(),"取消加精失败!");
                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<String>>(){}.getType();
                }
            });
        }else {
            AppUtils.Toast.shouToast(getContext(),"id不能为空");
        }
    }

    private void addEssences() {
        if (!et_essences.getText().toString().equals("")){
            HashMap<Object,Object> map = new HashMap<>();
            map.put("postId",et_essences.getText().toString());
            map.put("type",1);
            OkHttpUtils.getInstance().dopost("/admin/addEssences", map, new INetCallback<Result<String>>() {
                @Override
                public void onSuccess(Result<String> data) {
                    if (data.getStatus()==10000){
                        AppUtils.Toast.shouToast(getContext(),"加精成功!");
                    }else {
                        AppUtils.Toast.shouToast(getContext(),"加精失败!");
                    }
                }

                @Override
                public void onFailed(Throwable ex) {
                    AppUtils.Toast.shouToast(getContext(),"加精失败!");
                }

                @Override
                public Type getType() {
                    return new TypeToken<Result<String>>(){}.getType();
                }
            });
        }else {
            AppUtils.Toast.shouToast(getContext(),"id不能为空");
       }
    }

    private void selectrollbackport(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/admin/getPost?pageNum=" + pageNum + "&pageSize=" + pageSize, new INetCallback<Result<PostResult>>() {
            @Override
            public void onSuccess(Result<PostResult> data) {
                rf_refresh.setRefreshing(false);
                if (data.getStatus()==10000){
                    list.addAll(data.getData().getList());
                    if (adapter==null){
                        adapter = new MyListAdapter(list,getContext());
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                delrollback(position);
                            }
                        });
                        rv_post.setLayoutManager(new LinearLayoutManager(getContext()));
                        rv_post.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult>>(){}.getType();
            }
        });
    }

    private void logout() {
        OkHttpUtils.getInstance().dopost("/logout", null, new INetCallback<Result<String>>() {

            @Override
            public void onSuccess(Result<String> data) {

            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>(){}.getType();
            }
        });
        EventBus.getDefault().post(new LogoutEvent());
    }


    private void selectPost(int pageNum, int pageSize) {
        OkHttpUtils.getInstance().doget("/getPost?pageNum=" + pageNum + "&pageSize=" + pageSize, new INetCallback<Result<PostResult>>() {
            @Override
            public void onSuccess(Result<PostResult> data) {
                rf_refresh.setRefreshing(false);
                if (data.getStatus()==10000){
                    list.addAll(data.getData().getList());
                    if (adapter==null){
                        adapter = new MyListAdapter(list,getContext());
                        adapter.setOnLongClickListener(new onLongClickListener() {
                            @Override
                            public void onLongClick(int position) {
                                if (list.get(position).getVisibility()==1){
                                    ContentActivity.start(getContext(),(long)list.get(position).getPostId());
                                }
                            }
                        });
                        adapter.setOnClickListener(new onClickListener() {
                            @Override
                            public void onClick(int position) {
                                delrollback(position);
                            }
                        });
                        rv_post.setLayoutManager(new LinearLayoutManager(getContext()));
                        rv_post.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(Throwable ex) {
                rf_refresh.setRefreshing(false);
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<PostResult>>(){}.getType();
            }
        });
    }

    private void delrollback(int position) {
        String ss = null;
        if (list.get(position).getVisibility()==1){
            ss = "删除";
        }else{
            ss = "恢复";
        }
        String finalSs = ss;
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("您确定要"+ss+"\"PostId:"+list.get(position).getPostId()+",标题为:"+list.get(position).getTitle()+"\"吗?")
                .setPositiveButton("取消", null)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (finalSs.equals("删除")){
                            deletepost(position);
                        }else {
                            rollback(position);
                        }

                    }
                })
                .create();
        dialog.show();
    }

    private void rollback(int position) {
        HashMap<Object,Object> map = new HashMap<>();
        map.put("postId",list.get(position).getPostId());
        OkHttpUtils.getInstance().dopost("/admin/rollBackPost", map, new INetCallback<Result<String>>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus()==10000){
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Throwable ex) {

            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String>>(){}.getType();
            }
        });
    }


    private void deletepost(int position) {
        HashMap<Object,Object> map = new HashMap<>();
        map.put("postId",list.get(position).getPostId());
        OkHttpUtils.getInstance().dopost("/deletePost", map, new INetCallback<Result<String >>() {
            @Override
            public void onSuccess(Result<String> data) {
                if (data.getStatus()==10000){
                    AppUtils.Toast.shouToast(getContext(),"删除成功");
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                }else {
                    AppUtils.Toast.shouToast(getContext(),"删除失败,请重试");
                }

            }

            @Override
            public void onFailed(Throwable ex) {
                AppUtils.Toast.shouToast(getContext(),"删除失败,请重试");
            }

            @Override
            public Type getType() {
                return new TypeToken<Result<String >>(){}.getType();
            }
        });
    }

    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
        private List<PostResult.ListBean> datasource;
        private Context context;
        private com.kcbs.webforum.onLongClickListener onLongClickListener;
        private com.kcbs.webforum.onClickListener onClickListener;

        public void setOnClickListener(com.kcbs.webforum.onClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnLongClickListener(com.kcbs.webforum.onLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
        }

        public MyListAdapter(List<PostResult.ListBean> datasource, Context context) {
            this.datasource = datasource;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_admin,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tv_id.setText(datasource.get(position).getPostId()+"");
            holder.tv_categoryName.setText(datasource.get(position).getCategoryName());
            holder.tv_content.setText(datasource.get(position).getContent());
            holder.tv_title.setText(datasource.get(position).getTitle());
            holder.tv_name.setText(datasource.get(position).getUserName());
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
                    onClickListener.onClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datasource==null?0:datasource.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_id,tv_categoryName,tv_title,tv_content,tv_name;
            private LinearLayout root;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_id = itemView.findViewById(R.id.tv_id);
                tv_categoryName = itemView.findViewById(R.id.tv_categoryName);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_name = itemView.findViewById(R.id.tv_name);
                root = itemView.findViewById(R.id.root);
            }
        }
    }

}
