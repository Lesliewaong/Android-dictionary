package com.example.lesliewang.demo.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.query.DBManager;
import com.example.lesliewang.demo.query.DictionaryAdapter;
import com.example.lesliewang.demo.tools.VibratorUtil;

/*
* 单词应用
* */

public class Fragment4 extends Fragment implements TextWatcher, View.OnClickListener, RadioGroup.OnCheckedChangeListener{
    private AutoCompleteTextView selectWord;
    private Button btn;
    static DBManager dbManager;

    private TextView tv2;
    static Cursor cur;
    private String use="WordGroup";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment4,null);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        //通过findViewById实例化组件
        selectWord =view.findViewById(R.id.input_word);
        //实例化，确认按钮
        btn =  view.findViewById(R.id.btn_confirm);
        //实例化组件，查询的内容
        //tv1 = view.findViewById(R.id.word);
        //  显示查询结果，输入内容的意思
        tv2 = view.findViewById(R.id.use);
        tv2.setMovementMethod(ScrollingMovementMethod.getInstance());
        //添加自动完成文本框的监听事件
        selectWord.addTextChangedListener(this);
        selectWord.setThreshold(1); //更改触发提示的字符长度，default=2
        //添加查询按钮的监听事件
        btn.setOnClickListener(this);
        RadioGroup openDrawerGroup = view.findViewById(R.id.use_choice);
        openDrawerGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        VibratorUtil.Vibrate(getActivity(), 100);//振动
        cur.close();
        search();//搜索
    }

    private void search() {
        //初始化话DBManager
        dbManager = new DBManager(getActivity());

        //获取查询的内容
        String selword = selectWord.getText().toString();
        //获取查询内容的类型


        String sql = "select " + use + " from USE where English = ?";
        Cursor cursor = dbManager.select(sql, new String[]{selword});
        //  如果查找单词，显示其中文的意思
        if (cursor.getCount() > 0) {
            //  必须使用moveToFirst方法将记录指针移动到第1条记录的位置
            cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex(use));
            //设置TextView的内容
            if(result.length()!=0) {
                tv2.setVisibility(View.VISIBLE);
                tv2.setText(result);
            }else {
                DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(), null, true);
                selectWord.setAdapter(dictionaryAdapter);
                Toast.makeText(getActivity(), "该单词无此用法！", Toast.LENGTH_SHORT).show();//未找到
            }
            //tv1.setText(selword);
        } else {
            DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(), null, true);
            selectWord.setAdapter(dictionaryAdapter);
            Toast.makeText(getActivity(), "未找到该单词！", Toast.LENGTH_SHORT).show();//未找到
        }
        cursor.close();


        dbManager.closeDatabase();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        dbManager = new DBManager(getActivity());
        if(s.length()>0){
            btn.setEnabled(true);//检测输入后按钮可用
            String sql = "select English as _id from USE where English like ?";
            cur = dbManager.fuzzySelect(sql, new String[]{ s.toString() + "%" });
            if(cur.getCount() > 0){
                DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(), cur, true);
                selectWord.setAdapter(dictionaryAdapter);
            }
            else{
                DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(), null, true);
                selectWord.setAdapter(dictionaryAdapter);
            }
        }else{
            DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(getActivity(), null, true);
            selectWord.setAdapter(dictionaryAdapter);
        }

        dbManager.closeDatabase();//关闭数据库
    }
    //单词应用选择
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.wordGroup) {
            use="WordGroup";
        } else if (checkedId == R.id.synonyms) {
            use ="Synonyms";
        } else if (checkedId == R.id.discriminate) {
            use ="Discriminate";
        }
    }

}
