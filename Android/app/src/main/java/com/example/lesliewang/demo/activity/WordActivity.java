package com.example.lesliewang.demo.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;


import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.query.DBManager;
import com.example.lesliewang.demo.note.Word;
import com.example.lesliewang.demo.note.WordViewModel;

import java.io.IOException;

import static com.example.lesliewang.demo.activity.LoginActivity.loginsign;
import static com.example.lesliewang.demo.activity.MainActivity.net;


public class WordActivity extends AppCompatActivity {
   //定义变量
    DBManager dbManager;
    String english,chinese,british,american,sentence;
    TextView tv_english,tv_chinese,tv_british,tv_american,tv_sentence;
    private WordViewModel wordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);//布局

        tv_english = findViewById(R.id.select_word);
        tv_chinese = findViewById(R.id.select_word_mean);
        tv_british = findViewById(R.id.british);
        tv_american =findViewById(R.id.american);
        tv_sentence=findViewById(R.id.select_word_sentence);
        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);//实例化



        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        //接收传递数据
        Intent intent =getIntent();
        Bundle bundle =intent.getExtras();
        final String word;
        if (bundle != null) {
            word = bundle.getString("word");
            selectEnglish(word);//单词查询
        }

        //收藏
        ImageView im_add = findViewById(R.id.add_note);//实例化
        im_add.setOnClickListener(new View.OnClickListener() {//监听事件
            @Override
            public void onClick(View view) {
                //判断是否登录
                if(loginsign) {
                    //创建对话框对象
                    AlertDialog alertDialog = new AlertDialog.Builder(WordActivity.this).create();
                    //alertDialog.setIcon(); //设置对话框的图标
                    //alertDialog.setTitle("");      //设置对话框的标题
                    alertDialog.setMessage("请问是否要加入生词本？");    //设置要显示的内容
                    //添加取消按钮
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(WordActivity.this, "未成功加入生词本", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //添加确定按钮
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //只截取一部分加入生词本
                            String[] ch = chinese.split("\n|\r|\r\n");
                            String[] ch2 = ch[0].split("；");
                            wordViewModel.insertWords(new Word(english,ch2[0]));//添加单词
                            Toast.makeText(WordActivity.this, "成功加入生词本 ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show(); //显示对话框
                }else {
                    Toast.makeText(WordActivity.this, "您还没有登录", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //单词发音  MediaPlayer使用有道的发音
        ImageView vol1 = findViewById(R.id.volume1);
        vol1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(net){
                    String url = "http://dict.youdao.com/dictvoice?type=1&audio="+english; //英音
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }


            }
        });
        ImageView vol2 = findViewById(R.id.volume2);
        vol2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(net){
                    String url = "http://dict.youdao.com/dictvoice?type=2&audio="+english; //美音
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }

            }
        });

    }



    //设置监听事件（toolbar返回）
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
   //单词查询
    public void selectEnglish(String s) {
        dbManager = new DBManager(this);//实例化
        Cursor cursor = dbManager.select("select Chinese,British,American,Sentence from DICT where English = ?", new String[]{s});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            english=s;
            chinese = cursor.getString(cursor.getColumnIndex("Chinese"));
            british = cursor.getString(cursor.getColumnIndex("British"));
            american = cursor.getString(cursor.getColumnIndex("American"));
            sentence =cursor.getString(cursor.getColumnIndex("Sentence"));

            tv_english.setText(english);
            tv_chinese.setText(chinese);
            tv_british.setText(british);
            tv_american.setText(american);
            tv_sentence.setText(sentence);
        }else{
            finish();
            Toast toast = Toast.makeText(WordActivity.this, "单词不存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            //非离线词库的单词可加入生词本线上查询
            //判断是否登录
            if(loginsign) {
                wordViewModel.insertWords(new Word(s,"无离线释义"));
                Toast.makeText(WordActivity.this, "成功加入生词本 ", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(WordActivity.this, "登录后可将该词加入生词本", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        dbManager.closeDatabase();
    }
}
