package com.example.lesliewang.demo.search;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lesliewang.demo.activity.ChineseActivity;
import com.example.lesliewang.demo.activity.WordActivity;
import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.tools.LanguageAnalysisTools;


public class SearchActivity extends AppCompatActivity{
    private SearchView search;
    private RecyclerView mRecyclerView;
    private TextView mtv_deleteAll;
    private TextView mtv_cancel;
    private SeachRecordAdapter mAdapter;
    private DbDao mDbDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);//布局
        initViews();
    }

    private void initViews() {
        //获取search1按钮
        search =(SearchView)findViewById(R.id.search1);

        //取消按钮
        mtv_cancel =findViewById(R.id.tv_cancel);
        mtv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDbDao =new DbDao(this);
        //清空按钮
        mtv_deleteAll = (TextView) findViewById(R.id.tv_deleteAll);
        mtv_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDbDao.deleteData();//清空数据
                mAdapter.updata(mDbDao.queryData(""));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter =new SeachRecordAdapter(mDbDao.queryData(""),this,this);

        mAdapter.setRvItemOnclickListener(new BaseRecycleAdapter.RvItemOnclickListener() {
            @Override
            public void RvItemOnclick(int position) {
                mDbDao.delete(mDbDao.queryData("").get(position));
                mAdapter.updata(mDbDao.queryData(""));
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // 设置搜索文本监听
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (s.length() != 0){
                    //保存历史记录
                    boolean hasData = mDbDao.hasData(s);
                    if (!hasData){
                        mDbDao.insertData(s);
                    }else {
                        Toast.makeText(SearchActivity.this, "该内容已在历史记录中", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.updata(mDbDao.queryData(""));
                    //根据输入语言传数据
                    int language = LanguageAnalysisTools.getLanguage(s);
                    if (language == 0) {//Chinese
                        transmitData2(s);
                    }else if(language == 1){
                        transmitData(s);
                    }

                }else{
                    //toast显示在中间
                    Toast toast = Toast.makeText(SearchActivity.this, "请输入单词", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                return false;
            }
            // 当搜索内容改变时触发该方法，时刻监听输入搜索框的值
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }
    //向WordActivvity传数据
    public void transmitData(String s){
        Intent intent = new Intent(SearchActivity.this, WordActivity.class);
        Bundle bundle =new Bundle();
        bundle.putCharSequence("word",s);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    //向ChineseActivity传数据
    public void transmitData2(String s){
        Intent intent = new Intent(SearchActivity.this, ChineseActivity.class);
        Bundle bundle =new Bundle();
        bundle.putCharSequence("word",s);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
