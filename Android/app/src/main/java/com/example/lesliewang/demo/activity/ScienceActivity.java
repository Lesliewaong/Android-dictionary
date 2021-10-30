package com.example.lesliewang.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.lesliewang.demo.R;
/*
Webview
学术翻译,句子分析
*/
public class ScienceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_science);
        //toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        web("http://dict.cnki.net/");
    }
    //选择Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }
    //设置监听事件（toolbar返回）
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cnki:
                web("http://dict.cnki.net/");
                break;
            case R.id.nlp:
                web("http://nlp.stanford.edu:8080/corenlp/");
                break;
        }
        return true;
    }
    //webview
    private void web(String s) {
        WebView webView =findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        // webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        //设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);//设定缩放控件隐藏
        webView.setWebViewClient(new WebViewClient(){});
        webView.loadUrl(s);//输入网址
    }
}
