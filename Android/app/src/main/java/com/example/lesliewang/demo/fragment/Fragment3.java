package com.example.lesliewang.demo.fragment;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lesliewang.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
影视金句
*/

public class Fragment3 extends Fragment {
    //轮播图控件
    private ViewPager mViewPaper;
    //轮播图图片集合
    private List<ImageView> images;
    //存放轮播图显示图片
    private int[] imageIds = new int[]{
            R.drawable.img1,
            R.drawable.img3,
            R.drawable.img2,
            R.drawable.img7,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6,
            R.drawable.img8
    };
    //存放图片的标题
    private String[] titles = new String[]{
            "其实他没那么喜欢你",
            "美国队长",
            "老友记",
            "泰坦尼克号",
            "无欲则刚",
            "暮光之城",
            "速度与激情",
            "万物理论"

    };
    //轮播图显示文字控件
    private TextView title;
    //轮播图点集合
    private List<View> dots;
    //记录当前跳转的轮播图ID
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;

    //轮播图适配器
    private ViewPagerAdapter adapter;
    //执行定时任务
    private ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture beeperHandle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment3,null);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {

        //声明轮播图控件
        mViewPaper = view.findViewById(R.id.vp);

        //初始化显示的图片
        images = new ArrayList<ImageView>();
        //添加图片到图片集合
        for (int imageId : imageIds) {
            //初始化控件
            ImageView imageView = new ImageView(getActivity());
            //设置图片背景
            imageView.setBackgroundResource(imageId);
            //图片添加到集合
            images.add(imageView);
        }

        //初始化轮播图文字
        title = view.findViewById(R.id.title);
        //设置轮播图显示文字
        title.setText(titles[0]);

        //小点集合
        dots = new ArrayList<View>();
        //初始化小点添加到集合
        dots.add(view.findViewById(R.id.dot_0));
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));
        dots.add(view.findViewById(R.id.dot_3));
        dots.add(view.findViewById(R.id.dot_4));
        dots.add(view.findViewById(R.id.dot_5));
        dots.add(view.findViewById(R.id.dot_6));
        dots.add(view.findViewById(R.id.dot_7));

        //初始化适配器
        adapter = new ViewPagerAdapter();
        //轮播图绑定适配器
        mViewPaper.setAdapter(adapter);
        //轮播图滑动事件监听
        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面切换后触发
            @Override
            public void onPageSelected(int position) {
                //设置轮播图文字
                title.setText(titles[position]);
                //设置当前小点图片
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                //设置前一个小点图片
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                //记录小点id
                oldPosition = position;
                //记录当前位置
                currentItem = position;
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    // 自定义Adapter
    private class ViewPagerAdapter extends PagerAdapter {
        //返回页卡的数量
        @Override
        public int getCount() {
            return images.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            // TODO Auto-generated method stub
            view.removeView(images.get(position)) ;//删除页卡
        }
        //这个方法用来实例化页卡
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            // TODO Auto-generated method stub
            //添加图片控件到轮播图控件
            view.addView(images.get(position));
            return images.get(position);
        }
    }
    /**
     * 利用线程池定时执行动画轮播
     */
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        //初始化定时线程
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 设定执行线程计划,初始1ms延迟,每次任务完成后延迟4s再执行一次任务
        beeperHandle=scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                1,
                4000,
                TimeUnit.MILLISECONDS);
    }
    private class ViewPageTask implements Runnable {
        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            //发送消息
            mHandler.sendEmptyMessage(0);
        }
    }
    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mViewPaper.setCurrentItem(currentItem);

        };
    };
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
    //切换fragment时取消任务
    @Override
    public void onPause() {
        super.onPause();
        //currentItem=0;
        new Thread(() -> {
            //取消任务
            beeperHandle.cancel(true);
        }).start();
    }


}
