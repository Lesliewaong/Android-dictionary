package com.example.lesliewang.demo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lesliewang.demo.R;

//登录、注册界面设计（登录后可使用生词本）
public class LoginActivity extends AppCompatActivity {
    private String username, password; //用户名和密码
    public static boolean loginsign;   //登录成功标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameET = findViewById(R.id.username);     //获取用户名编辑框
        EditText passwordET = findViewById(R.id.password);      //获取密码编辑框
        Button login = findViewById(R.id.login);               //获取登录按钮
        Button register = findViewById(R.id.register);          //获取注册按钮

        // 获得SharedPreferences,并创建文件名称为"login"
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);//打开Preferences，名称为login，如果存在则打开它，否则创建新的Preferences
        SharedPreferences.Editor editor = sp.edit();    // 获得Editor对象,用于存储用户名与密码信息

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                username = usernameET.getText().toString();            //获得输入的用户名
                password = passwordET.getText().toString();            //获得输入的密码
                //已注册
                if (sp.getString("username1", null) != null && sp.getString("password1", null) != null) {
                    Toast toast = Toast.makeText(LoginActivity.this, "您已经注册了，请直接登录", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if ((username.length()==0)|| (password.length()==0)){//提示注册
                    Toast toast = Toast.makeText(LoginActivity.this, "请注册用户名和密码", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{//注册
                    editor.putString("username1", username);           //存储初始用户名
                    editor.putString("password1", password);           //存储初始密码
                    editor.apply();                                    //提交信息
                    Toast toast = Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        //已保存登录信息
        if (sp.getString("username", null) != null && sp.getString("password", null) != null) {
            usernameET.setText(sp.getString("username1", null));
            passwordET.setText("********");
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginsign = true;//登录状态
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否注册
                    if (sp.getString("username1", null) != null && sp.getString("password1", null) != null) {
                        username = usernameET.getText().toString();            //获得输入的用户名
                        password = passwordET.getText().toString();            //获得输入的密码
                        //如果输入用户名、密码与注册相同时，登录并存储
                        if (username.equals(sp.getString("username1", null)) && password.equals(sp.getString("password1", null))) { //判断输入的用户名密码是否正确
                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                            editor.putString("username", username);           //存储用户名
                            editor.putString("password", password);           //存储密码
                            editor.apply();                                    //提交信息
                            loginsign = true;//登录成功
                            finish();
                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {//若没注册，提示注册
                        Toast toast = Toast.makeText(LoginActivity.this, "你还没有注册", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }

            });
        }
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
