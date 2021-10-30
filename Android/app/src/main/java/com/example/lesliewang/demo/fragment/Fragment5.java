package com.example.lesliewang.demo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.note.Word;
import com.example.lesliewang.demo.note.WordViewModel;
import com.example.lesliewang.demo.query.DBManager;

import java.io.IOException;

import static com.example.lesliewang.demo.activity.LoginActivity.loginsign;
import static com.example.lesliewang.demo.activity.MainActivity.net;

/*
编程词汇
*/
public class Fragment5 extends Fragment  {




    private WordViewModel wordViewModel;
    static DBManager dbManager;
    private String english;
    private String chinese;
    private TextView tv_english;
    private TextView tv_chinese;
    private TextView tv_british;
    private TextView tv_american;
    private Button random;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment5,null);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {

        //实例化
        tv_english = view.findViewById(R.id.p_word);
        tv_chinese = view.findViewById(R.id.p_word_mean);
        tv_chinese.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_british = view.findViewById(R.id.bri);
        tv_american =view.findViewById(R.id.ame);
        random =view.findViewById(R.id.random);
        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        //获取search1按钮
        SearchView search = view.findViewById(R.id.search3);
        //收藏
        ImageView im_add = view.findViewById(R.id.add_nt);//实例化
        im_add.setOnClickListener(new View.OnClickListener() {//监听事件
            @Override
            public void onClick(View view) {
                //判断是否登录
                if(loginsign) {
                    //创建对话框对象
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    //alertDialog.setIcon(); //设置对话框的图标
                    //alertDialog.setTitle("");      //设置对话框的标题
                    alertDialog.setMessage("请问是否要加入生词本？");    //设置要显示的内容
                    //添加取消按钮
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "未成功加入生词本", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //添加确定按钮
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //只截取一部分加入生词本
                            String[] ch = chinese.split("\n|\r|\r\n");
                            String[] ch2 = ch[0].split("；");
                            wordViewModel.insertWords(new Word(english,ch2[0]));
                            Toast.makeText(getActivity(), "成功加入生词本 ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show(); //显示对话框
                }else {
                    Toast.makeText(getActivity(), "您还没有登录", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //单词发音 多次点击会卡死的bug没有解决
        ImageView vol1 = view.findViewById(R.id.vol1);
        vol1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(net){

                    String url = "http://dict.youdao.com/dictvoice?audio="+english+"&type=1"; //英音
                    MediaPlayer player=new MediaPlayer();
                    if (player == null){
                        player=new MediaPlayer();
                    }
                    //设置媒体资源的类型为音乐
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try
                    {
                        //使用URL获取媒体资源，URL对应的资源必须能逐行下载
                        //使用setDataSource可能会出现资源不存在的情况，所以必须抛出异常
                        //musicUrl:媒体资源的URL
                        player.setDataSource(url);
                        player.prepareAsync(); //异步
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    //为播放器设置监听器，用于监听prepareAsync的状态，当准备完成时播放
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            mp.start();
                        }
                    });
                }

            }
        });
        ImageView vol2 = view.findViewById(R.id.vol2);
        vol2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(net){
                    String url = "http://dict.youdao.com/dictvoice?audio="+english+"&type=2"; //美音
                    MediaPlayer player=new MediaPlayer();
                    if (player == null){
                        player=new MediaPlayer();
                    }
                    //设置媒体资源的类型为音乐
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try
                    {
                        //使用URL获取媒体资源，URL对应的资源必须能逐行下载
                        //使用setDataSource可能会出现资源不存在的情况，所以必须抛出异常
                        //musicUrl:媒体资源的URL
                        player.setDataSource(url);
                        player.prepareAsync(); //异步
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    //为播放器设置监听器，用于监听prepareAsync的状态，当准备完成时播放
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                    {
                        @Override
                        public void onPrepared(MediaPlayer mp)
                        {
                            mp.start();
                        }
                    });
                }
            }
        });
        selectEnglish("program");
        // 设置搜索文本监听
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (s.length() != 0){
                    selectEnglish(s);
                }
                return false;
            }
            // 当搜索内容改变时触发该方法，时刻监听输入搜索框的值
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //随机显示单词
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = String.valueOf((int)(Math.random()*1180)+1);//1-1180随机数
                randomEnglish(s);
            }
        });

    }



    //单词查询
    private void selectEnglish(String s) {
        dbManager = new DBManager(getActivity());//实例化
        //大小写匹配
        Cursor cursor = dbManager.select("select Chinese,British,American from JAVA where lower(English) = ?", new String[]{s.toLowerCase()});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            english=s;
            chinese = cursor.getString(cursor.getColumnIndex("Chinese"));
            String british = cursor.getString(cursor.getColumnIndex("British"));
            String american = cursor.getString(cursor.getColumnIndex("American"));

            tv_english.setText(english);
            tv_chinese.setText(chinese);
            tv_british.setText(british);
            tv_american.setText(american);

        }else{

            Toast toast = Toast.makeText(getActivity(), "单词不存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        cursor.close();
        dbManager.closeDatabase();
    }
    //随机获取单词
    private void randomEnglish(String s) {
        dbManager = new DBManager(getActivity());//实例化
        Cursor cursor = dbManager.select("select English,Chinese,British,American from JAVA where ID = ?", new String[]{s});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            english=cursor.getString(cursor.getColumnIndex("English"));
            chinese = cursor.getString(cursor.getColumnIndex("Chinese"));
            String british = cursor.getString(cursor.getColumnIndex("British"));
            String american = cursor.getString(cursor.getColumnIndex("American"));

            tv_english.setText(english);
            tv_chinese.setText(chinese);
            tv_british.setText(british);
            tv_american.setText(american);

        }else{

            Toast toast = Toast.makeText(getActivity(), "单词不存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        cursor.close();
        dbManager.closeDatabase();
    }


}
