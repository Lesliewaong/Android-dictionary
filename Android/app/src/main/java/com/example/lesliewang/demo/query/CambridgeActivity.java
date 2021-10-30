package com.example.lesliewang.demo.query;

import android.content.Context;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.tools.LanguageAnalysisTools;
import com.example.lesliewang.demo.tools.VibratorUtil;

/*
* 中英互译
* 模糊搜索框消失bug
* */
public class CambridgeActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private AutoCompleteTextView selectWord;
    private Button btn;
    DBManager dbManager;
    private TextView tv1;
    private TextView tv2;
    static Cursor cur;
    String dictionary="JianQiao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambridge);
        initComponent();
    }

    private void initComponent() {
        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //通过findViewById实例化组件
        selectWord =findViewById(R.id.input_act);
        //实例化，确认按钮
        btn =  findViewById(R.id.btn_enter);
        //实例化组件，查询的内容
        tv1 = (TextView)findViewById(R.id.input);
        //  显示查询结果，输入内容的意思
        tv2 = (TextView) findViewById(R.id.output);
        //添加自动完成文本框的监听事件
        selectWord.addTextChangedListener(this);
        selectWord.setThreshold(1); //更改触发提示的字符长度，default=2
        //添加查询按钮的监听事件
        btn.setOnClickListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        dbManager = new DBManager(this);
        //设置查询内容不可见
        tv1.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        if(s.length()>0){
            btn.setEnabled(true);//检测输入后按钮使能
            //English
            String sql = "select English as _id from "+dictionary+" where English like ?";
            cur = dbManager.fuzzySelect(sql, new String[]{s.toString() + "%"});
            if (cur.getCount() > 0) {
                DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this, cur, true);
                selectWord.setAdapter(dictionaryAdapter);
            } else {
                DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this, null, true);
                selectWord.setAdapter(dictionaryAdapter);
            }
        }
        else{
            DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this, null, true);
            selectWord.setAdapter(dictionaryAdapter);
        }

        dbManager.closeDatabase();//关闭数据库
    }

    @Override
    public void onClick(View view) {
        VibratorUtil.Vibrate(CambridgeActivity.this, 100);//振动
        hideKeyBoard();
        cur.close();
        search();//搜索
    }
    //搜索
    private void search() {
        //初始化话DBManager
        dbManager = new DBManager(this);

        //获取查询的内容
        String selword = selectWord.getText().toString();
        //获取查询内容的类型
        String sql;
        String result = "未找到该单词！";

        sql = "select Chinese from "+dictionary+" where English = ?";
        Cursor cursor = dbManager.select(sql, new String[]{selword});
        //  如果查找单词，显示其中文的意思
        if (cursor.getCount() > 0) {
            //  必须使用moveToFirst方法将记录指针移动到第1条记录的位置
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("Chinese"));
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            //设置TextView的内容
            tv2.setText(result);
            tv1.setText(selectWord.getText());
        } else {
            DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this, null, true);
            selectWord.setAdapter(dictionaryAdapter);
            Toast.makeText(CambridgeActivity.this, result, Toast.LENGTH_SHORT).show();//未找到
        }
        cursor.close();


        dbManager.closeDatabase();
    }
    //选择Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main5, menu);
        return true;
    }
    //设置监听事件（toolbar返回）
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.jianqiao:
                dictionary="JianQiao";
                break;
            case R.id.suolue:
                dictionary="SLC";
                break;

        }
        return true;
    }
    // 隐藏键盘
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
