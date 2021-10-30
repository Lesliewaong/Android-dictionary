package com.example.lesliewang.demo.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class NetStateListener {
    public interface OnNetStateListener{//接口
        void netMMonitor(boolean isAvailable);
    }
    OnNetStateListener onNetStateListener=null;
    public void setOnNetStateListener(OnNetStateListener onNetStateListener){//回调方法
        this.onNetStateListener=onNetStateListener;
    }

    Timer timer=new Timer();
    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            Message msg=new Message();
            handler.sendMessage(msg);
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(onNetStateListener!=null){
                //这里就实现了每2秒监听网络状态
                onNetStateListener.netMMonitor(getNetState());
            }
        }
    };
    public void start(){//开始监听
        timer.schedule(task,1000,2000);//1秒后执行，每2秒监听一次接口
    }
    public void stop(){//停止所有定时任务
        timer.cancel();
    }

    private Context context;
    public NetStateListener(Context context){
        super();
        this.context=context;
    }
    //监听网络状态的接口
    public boolean getNetState(){
        ConnectivityManager connectivity=(ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null){
            NetworkInfo info=connectivity.getActiveNetworkInfo();
            if(info!=null&&info.isConnected()){
                if(info.getState()==NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

}
