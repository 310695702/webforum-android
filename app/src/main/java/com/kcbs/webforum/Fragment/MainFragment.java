package com.kcbs.webforum.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kcbs.webforum.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_main,container,false);
            init();
            initViewpage();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent!=null){
            parent.removeView(view);
        }
        return view;
    }

    private void init() {
        viewPager = view.findViewById(R.id.fragment_vp);
        tabLayout = view.findViewById(R.id.tabs_rg);
    }

    private void initViewpage() {
        fragments = new ArrayList<>();
        WebforumFragment webforumFragment = new WebforumFragment();
        MyFragement myFragement = new MyFragement();

        fragments.add(webforumFragment);
        fragments.add(myFragement);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.setFragments(fragments);//添加fragment
        viewPager.setAdapter(viewPagerAdapter);//添加适配器

        //添加Tab页
        tabLayout.addTab(tabLayout.newTab());


        tabLayout.setupWithViewPager(viewPager);//实现TabLayout与ViewPager互相变换

        tabLayout.getTabAt(0).setIcon(R.drawable.webforum_selector);
        tabLayout.getTabAt(1).setIcon(R.drawable.my_selector);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position==0){
                return "社区";
            }else if (position==1){
                return "我的";
            }
            return null;
        }
    }


}
