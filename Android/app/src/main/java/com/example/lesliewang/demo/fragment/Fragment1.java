package com.example.lesliewang.demo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.tools.VibratorUtil;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import static com.example.lesliewang.demo.activity.MainActivity.net;

/*
句子分析
*/
public class Fragment1 extends Fragment implements View.OnClickListener{

    private EditText s_value;
    private TextView s_result;


    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment1,null);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        //通过findViewById实例化组件
        Button s_tran = view.findViewById(R.id.s_tran);
        Button s_clear = view.findViewById(R.id.s_clear);
        s_value=view.findViewById(R.id.s_value);
        s_result=view.findViewById(R.id.s_result);
        s_result.setMovementMethod(ScrollingMovementMethod.getInstance());//可滚动
        //初始化控件
        ImageView s_listen = view.findViewById(R.id.s_listen);
        s_listen.setImageResource(R.drawable.voice);
        //为按钮添加事件监听器
        s_tran.setOnClickListener(this);
        s_clear.setOnClickListener(this);
        s_listen.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s_tran://分析
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                hideKeyBoard();
                nlp();
                break;
            case R.id.s_clear://清空
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                hideKeyBoard();
                clear();
                break;
            case R.id.s_listen://语音输入
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                if(net) {
                    listen();
                }
                break;
            default:
                break;
        }
    }
    //语音输入
    private void listen() {
        // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
        //听写对象
        SpeechRecognizer hearer = SpeechRecognizer.createRecognizer(getActivity(), null);
        // 交互动画
        //讯飞提示框
        RecognizerDialog dialog = new RecognizerDialog(getActivity(), null);
        // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        hearer.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        hearer.setParameter( SpeechConstant.SUBJECT, null );
        // domain:域名
        hearer.setParameter(SpeechConstant.DOMAIN, "iat");
        //设置语音输入语言，zh_cn为简体中文
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        hearer.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        hearer.setParameter(SpeechConstant.VAD_EOS, "2000");
        //设置是否显示标点0表示不显示，1表示显示
        hearer.setParameter(SpeechConstant.ASR_PTT,"1");

        //3.开始听写
        dialog.setListener(new RecognizerDialogListener() {  //设置对话框

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                // TODO 自动生成的方法存根
                Log.d("Result", results.getResultString());
                //(1) 解析 json 数据<< 一个一个分析文本 >>
                StringBuffer strBuffer = new StringBuffer();
                try {
                    JSONTokener tokener = new JSONTokener(results.getResultString());
                    Log.i("TAG", "Test"+results.getResultString());
                    Log.i("TAG", "Test"+results.toString());
                    JSONObject joResult = new JSONObject(tokener);

                    JSONArray words = joResult.getJSONArray("ws");
                    for (int i = 0; i < words.length(); i++) {
                        // 转写结果词，默认使用第一个结果
                        JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                        JSONObject obj = items.getJSONObject(0);
                        strBuffer.append(obj.getString("w"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // (2)读取json结果中的sn字段
                String sn = null;

                try {
                    JSONObject resultJson = new JSONObject(results.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //(3) 解析语音文本<< 将文本叠加成语音分析结果  >>
                hashMapTexts.put(sn, strBuffer.toString());
                StringBuffer resultBuffer = new StringBuffer();  //最后结果
                for (String key : hashMapTexts.keySet()) {
                    resultBuffer.append(hashMapTexts.get(key));
                }

                s_value.setText(resultBuffer.toString());
                s_value.requestFocus();//获取焦点
                s_value.setSelection(resultBuffer.toString().length());//将光标定位到文字最后，以便修改

            }

            @Override
            public void onError(SpeechError error) {
                // TODO 自动生成的方法存根
                error.getPlainDescription(true);
            }
        });

        dialog.show();  //显示对话框
    }
    //清空
    private void clear()
    {
        s_value.setText("");
        s_result.setText("");
    }

    //nlp自然语言处理
    private void nlp() {
        StringBuilder s= new StringBuilder();//显示内容
        final String text=s_value.getText().toString();//获取输入内容
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);//分词
                //判断是否为标点符号
                if(check(word)){
                    continue;
                }
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);//词性
                pos = partOfSpeech(pos);//词性用中文表示
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
                s.append(String.format("句中单词：%s\n词形还原：%s\n词性分析：%s\n\n", word, lema, pos));//设置显示格式
            }
        }
        s_result.setText(s.toString());
    }
    //词性用中文表示
    private String partOfSpeech(String p){
        switch (p){
            case "CC":
                p = "连接词";
                break;
            case "CD":
                p = "基数词";
                break;
            case "DT":
                p = "限定词";
                break;
            case "WP":
                p = "疑问代词";
                break;
            case "VBZ":
                p = "动词第三人称单数";
                break;
            case "NNP":
                p = "单数专有名词";
                break;
            case "IN":
                p = "介词或从属连词";
                break;
            case "RB":
                p = "副词";
                break;
            case "EX":
                p = "存在句";
                break;
            case "FW":
                p = "外来词";
                break;
            case "JJ":
                p = " 形容词或序数词";
                break;
            case "JJR":
                p = "形容词比较级";
                break;
            case "JJS":
                p = "形容词最高级";
                break;
            case "LS":
                p = "列表标记";
                break;
            case "MD":
                p = "情态词";
                break;
            case "NN":
                p = "单数名词或不可数名词";
                break;
            case "NNS":
                p = "复数名词";
                break;
            case "NNPS":
                p = "复数专有名词";
                break;
            case "PDT":
                p = "前限定词";
                break;
            case "POS":
                p = "所有格结束词";
                break;
            case "PRP":
                p = "人称代词";
                break;
            case "PRP$":
                p = "所有格代词";
                break;
            case "RBR":
                p = "副词比较级";
                break;
            case "RBS":
                p = "副词最高级";
                break;
            case "RP":
                p = "小品词";
                break;
            case "SYM":
                p = "符号";
                break;
            case "TO":
                p = "to作为介词或不定式格式";
                break;
            case "UH":
                p = "感叹词";
                break;
            case "VB":
                p = "动词";
                break;
            case "VBD":
                p = "动词过去式";
                break;
            case "VBG":
                p = "动名词和现在分词";
                break;
            case "VBN":
                p = "过去分词";
                break;
            case "VBP":
                p = "动词非第三人称单数";
                break;
            case "WDT":
                p = "疑问限定词";
                break;
            case "WP$":
                p = "所有格代词";
                break;
            case "WRB":
                p = "疑问代词";
                break;
        }
        return p;
    }
    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     */
    public boolean check(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }
        return b;
    }
    // 隐藏键盘
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}