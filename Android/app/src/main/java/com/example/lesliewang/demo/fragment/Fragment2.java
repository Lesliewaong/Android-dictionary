package com.example.lesliewang.demo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lesliewang.demo.R;
import com.example.lesliewang.demo.tools.SpeechUtils;
import com.example.lesliewang.demo.tools.VibratorUtil;
import com.example.lesliewang.demo.tran.BaiduTranslateService;
import com.example.lesliewang.demo.tran.MD5Utils;
import com.example.lesliewang.demo.tran.RespondBean;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import static com.example.lesliewang.demo.activity.MainActivity.net;

/*
句子翻译
*/
public class Fragment2 extends Fragment implements View.OnClickListener{
    private String TAG = "Fragment2";
    private EditText et_value;//输入
    private TextView et_result;//输出
    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view=inflater.inflate(R.layout.fragment2,null);
        initComponent(view);
        return view;
    }

    private void initComponent(View view)
    {
        //通过findViewById实例化组件
        Button btn_tran = view.findViewById(R.id.btn_tran);
        Button btn_clear = view.findViewById(R.id.btn_clear);
        et_value=view.findViewById(R.id.et_value);
        et_result=view.findViewById(R.id.et_result);
        //初始化控件
        ImageView img = view.findViewById(R.id.listen_img);
        //为按钮添加事件监听器
        btn_tran.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        img.setOnClickListener(this);
        et_result.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tran://翻译
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                hideKeyBoard();
                if(net){
                    translate();
                }
                break;
            case R.id.btn_clear://清空
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                hideKeyBoard();
                clear();
                break;
            case R.id.listen_img://语音输入
                VibratorUtil.Vibrate(getActivity(), 100);//振动
                if(net){
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
        //设置语音输入语言，zh_cn为简体中文 mandarin:普通话
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin");
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
                //显示识别得到的文字
                et_value.setText(resultBuffer.toString());
                et_value.requestFocus();//获取焦点
                et_value.setSelection(resultBuffer.toString().length());//将光标定位到文字最后，以便修改

            }

            @Override
            public void onError(SpeechError error) {
                // TODO 自动生成的方法存根
                error.getPlainDescription(true);
            }
        });
        dialog.show();  //显示对话框
    }

    //百度API中英翻译
    private void translate() {

        //准备请求百度翻译接口需要的参数
        String word = et_value.getText().toString();//需查询的单词 q
        String from = "auto";//源语种 en 英语 zh 中文
        //检测到输入内容后执行
        if (word.length() != 0){
            //String中英文占用一个字节，中文占用两个字节，
            //利用String的这个存储特性可以用来判断String中有没有中文。
            //目标译文 可变 zh中文 en英文
            String to;//目标语言
            if (word.length() == word.getBytes().length) {//成立则说明没有汉字，否则有汉字。
                to = "zh"; //没有汉字 英译中
            } else {
                to = "en";//含有汉字 中译英
            }
            String appid = "20200408000414410";//appid 管理控制台有
            String salt = (int) (Math.random() * 100 + 1) + "";//随机数 这里范围是[0,100]整数 无强制要求
            String key = "sBA3s50PtcV9JFqxFXGo";//密钥 管理控制台有
            String string1 = appid + word + salt + key;// string1 = appid+q+salt+密钥
            String sign = MD5Utils.getMD5Code(string1);// 签名 = string1的MD5加密 32位字母小写
            Log.d(TAG, "sign: " + sign);

            Retrofit retrofitBaidu = new Retrofit.Builder()
                    .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                    .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                    .build();
            BaiduTranslateService baiduTranslateService = retrofitBaidu.create(BaiduTranslateService.class);


            Call<RespondBean> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
            call.enqueue(new Callback<RespondBean>() {
                @Override
                public void onResponse(Call<RespondBean> call, Response<RespondBean> response) {
                    //请求成功
                    Log.d(TAG, "onResponse: 请求成功");
                    RespondBean respondBean = response.body();//返回的JSON字符串对应的对象
                    String result = respondBean.getTrans_result().get(0).getDst();//获取翻译的字符串String
                    et_result.setText(result);
                }

                @Override
                public void onFailure(Call<RespondBean> call, Throwable t) {
                    //请求失败 打印异常
                    Log.d(TAG, "onResponse: 请求失败 " + t);
                }
            });
        }

    }
    //清空
    private void clear()
    {
        et_value.setText("");
        et_result.setText("");
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

