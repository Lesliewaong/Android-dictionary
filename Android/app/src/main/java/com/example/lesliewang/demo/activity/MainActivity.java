package com.example.lesliewang.demo.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.fragment.Fragment1;
import com.example.lesliewang.demo.fragment.Fragment2;
import com.example.lesliewang.demo.fragment.Fragment3;
import com.example.lesliewang.demo.fragment.Fragment4;
import com.example.lesliewang.demo.fragment.Fragment5;
import com.example.lesliewang.demo.note.NoteActivity;
import com.example.lesliewang.demo.query.CambridgeActivity;
import com.example.lesliewang.demo.search.SearchActivity;
import com.example.lesliewang.demo.tools.NetStateListener;
import com.example.lesliewang.demo.tools.SpeechUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;

import static com.example.lesliewang.demo.activity.LoginActivity.loginsign;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static boolean net;//网络连接判断，部分功能未连接网络点击会卡死
    public int flag;//记录点击按钮
    private long exitTime = 0; //退出时间变量值
    //申请录音权限
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //DrawerLayout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //判断网络连接
        final NetStateListener netStateListener = new NetStateListener(this);
        netStateListener.start();//开始监听
        netStateListener.setOnNetStateListener(new NetStateListener.OnNetStateListener() {
            @Override
            public void netMMonitor(boolean isAvailable) {
                //这里就可以根据返回的状态值执行一些想要的操作，像执行UI操作
                //比如我在这边实现了当网络监听接口getNetState返回false，便在前台提示
                if (!isAvailable) {
                    net = false;
                    Toast.makeText(MainActivity.this, "网络未连接，在线功能将无法使用", Toast.LENGTH_SHORT).show();
                } else {
                    net = true;
                }
            }
        });

        //申请录音权限
        verifyAudioPermissions(this);
        // 语音配置对象初始化
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e7a1a99");
        //在navigationView中切换Activity Fragment 卡顿优化
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //获得保存登录信息的SharedPreferences
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                switch (flag) {
                    case R.id.nav_note:
                        if (loginsign) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, NoteActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "您还没有登录", Toast.LENGTH_SHORT).show();
                        }
                        flag = R.id.nav_home;
                        break;
                    case R.id.nav_cnki://网页
                        if (net) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, ScienceActivity.class);
                            startActivity(intent);
                        }
                        flag = R.id.nav_home;
                        break;
                    case R.id.nav_logout://退出登录
                        new Thread(() -> {
                            editor.remove("username");
                            editor.remove("password");
                            editor.apply();
                        }).start();
                        loginsign = false;
                        Toast.makeText(getApplicationContext(), "您已退出登录", Toast.LENGTH_SHORT).show();
                        flag = R.id.nav_home;
                        break;
                    case R.id.nav_cancel://注销账号
                        editor.clear();//清空数据
                        loginsign = false;
                        Toast.makeText(getApplicationContext(), "您已注销账号", Toast.LENGTH_SHORT).show();
                        editor.apply();
                        flag = R.id.nav_home;
                        break;

                }


            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //NavigationView的头部的事件监听
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View drawerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView account = drawerView.findViewById(R.id.imageView);
        account.setImageResource(R.mipmap.ic_luff);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);  //通过Intent跳转登录后界面
                startActivity(intent);                              //启动跳转界面
            }
        });
        //选项卡
        TabLayout mytab = findViewById(R.id.tab);
        ViewPager mViewPager = findViewById(R.id.mViewPager);
        //每个选项卡对应的标题
        final List<String> mTitle = new ArrayList<>();
        mTitle.add("句子分析");
        mTitle.add("句子翻译");
        mTitle.add("影视金句");
        mTitle.add("单词应用");
        mTitle.add("编程词汇");
        //每个选项卡对应的Fragment
        final List<Fragment> mFragment = new ArrayList<>();
        mFragment.add(new Fragment1());
        mFragment.add(new Fragment2());
        mFragment.add(new Fragment3());
        mFragment.add(new Fragment4());
        mFragment.add(new Fragment5());
        //给ViewPager创建适配器，将Title和Fragment添加进ViewPager中
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        mytab.setupWithViewPager(mViewPager);//将ViewPager和TabLayout关联
        mViewPager.setOffscreenPageLimit(2);//预加载


    }

    //navigation view点击赋值id
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //关闭drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            flag = id;
        } else if (id == R.id.nav_note) {
            flag = id;
        } else if (id == R.id.nav_cnki) {
            flag = id;
        } else if (id == R.id.nav_logout) {
            flag = id;
        } else if (id == R.id.nav_cancel) {
            flag = id;
        }
        return true;
    }

    /*
     * 申请录音权限
     * */
    public static void verifyAudioPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //选择Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //设置toolbar上的按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {//进入单词搜索页
            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, SearchActivity.class);
            startActivity(intent1);
            return true;
        } else if (id == R.id.dictionary) {//选择剑桥词典（含中英解释）
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, CambridgeActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.google) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GoogleActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.article) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ArticleActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //退出应用时提醒
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;  //拦截返回键
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) { //计算按键时间差是否大于两秒
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

}
