package com.example.lesliewang.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.query.DBManager;
import com.example.lesliewang.demo.tools.SpeechUtils;

public class ArticleActivity extends AppCompatActivity {
    TextView tv_article;
    DBManager dbManager;
    String english ="";
    Integer id =1;
    private SpeechUtils speechUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        speechUtils = new SpeechUtils(this);
        tv_article = findViewById(R.id.textView4);

        selectEnglish(String.valueOf(id));
    }

    private void selectEnglish(String id) {
        //初始化话DBManager
        dbManager = new DBManager(this);

        String sql = "select English from ARTICLE where ID = ?";
        //根据数据库中的中文数据特点，找到最符合中文意思的英文单词
        Cursor cursor = dbManager.select(sql, new String[]{id});
        //  如果查找到单词，显示其英文的意思
        if (cursor.getCount() > 0) {
            //  必须使用moveToFirst方法将记录指针移动到第1条记录的位置
            cursor.moveToFirst();
            english = cursor.getString(cursor.getColumnIndex("English"));

            //设置TextView的内容
            tv_article.setText(english);
        }
        cursor.close();
        dbManager.closeDatabase();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechUtils.shutdownSpeaking();
    }
    //选择Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main4, menu);
        return true;
    }
    //设置监听事件（toolbar返回）
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.play:
                speechUtils.speakText(english);
                break;
            case R.id.stop:
                speechUtils.stopSpeaking();
                break;
            case R.id.last:
                if(id>1){
                    id=id-1;
                    selectEnglish(String.valueOf(id));
                }
                break;
            case R.id.next:
                if(id<157){
                    id=id+1;
                    selectEnglish(String.valueOf(id));
                }
                break;
        }
        return true;
    }
}
