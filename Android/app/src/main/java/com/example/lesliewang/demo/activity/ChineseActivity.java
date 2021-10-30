package com.example.lesliewang.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.note.Word;
import com.example.lesliewang.demo.query.DBManager;

import static com.example.lesliewang.demo.activity.LoginActivity.loginsign;

public class ChineseActivity extends AppCompatActivity {
    DBManager dbManager;
    TextView tv_english,tv_chinese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese);
        tv_chinese =findViewById(R.id.textView2);
        tv_english =findViewById(R.id.textView3);
        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        //接收传递数据
        Intent intent =getIntent();
        Bundle bundle =intent.getExtras();
        final String word =bundle.getString("word");
        selectChinese(word);//单词查询

    }

    private void selectChinese(String word) {
        //初始化话DBManager
        dbManager = new DBManager(this);

        String sql = "select English from DICT where Chinese like ?";
        //根据数据库中的中文数据特点，找到最符合中文意思的英文单词
        Cursor cursor = dbManager.fuzzySelect(sql, new String[]{word +"\n"+ "%"});//精确匹配 ". " + word  +"；"
        if (cursor.getCount() <= 0) {
            cursor = dbManager.fuzzySelect(sql, new String[]{"%" + ". " + word  + "%"});//多种可能
        }
        //  如果查找到单词，显示其英文的意思
        if (cursor.getCount() > 0) {
            //  必须使用moveToFirst方法将记录指针移动到第1条记录的位置
            cursor.moveToFirst();
            String english ="";
            do{
                english = english+cursor.getString(cursor.getColumnIndex("English"))+"\n";
            }while (cursor.moveToNext());
            //设置TextView的内容
            tv_chinese.setText(word);
            tv_english.setText(english);



        }else{
            finish();
            Toast toast = Toast.makeText(ChineseActivity.this, "单词不存在", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();


        }
        cursor.close();
        dbManager.closeDatabase();
    }

    //设置监听事件（toolbar返回）
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
