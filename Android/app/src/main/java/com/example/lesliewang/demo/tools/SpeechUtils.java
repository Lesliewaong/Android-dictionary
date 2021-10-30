package com.example.lesliewang.demo.tools;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
/*
android 自带语音合成
*/
public class SpeechUtils {
    private Context mcontext;
    private static final String TAG = "SpeechUtils";
    private static TextToSpeech textToSpeech; // TTS对象

    public SpeechUtils(Context context) {
        mcontext = context;
        textToSpeech = new TextToSpeech(mcontext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    //textToSpeech.setLanguage(Locale.US);
                    //textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    //textToSpeech.setSpeechRate(0.8f);
                    textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
    //语音合成
    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }
   //停止但不关闭
    public void stopSpeaking() {
        // 对象非空并且正在说话
        if (null != textToSpeech && textToSpeech.isSpeaking()) {
            // 停止说话
            textToSpeech.stop();
        }
    }
    //停止并关闭
    public void shutdownSpeaking() {
        // 对象非空并且正在说话
        if (null != textToSpeech && textToSpeech.isSpeaking()) {
            // 停止说话
            textToSpeech.stop();
            textToSpeech.shutdown(); // 关闭，释放资源
        }
    }

}