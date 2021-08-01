package com.example.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChangePwd extends AppCompatActivity {
    private PasswordView pwdView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        initView();
        initEvent();
    }

    private void initView(){
        pwdView = findViewById(R.id.pwd_view);
        pwdView.setTitle("设置密码");
        preferences = getSharedPreferences("password",MODE_PRIVATE);
        editor = preferences.edit();
    }

    private void initEvent(){
        // 添加回调接口
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish() {
                // 输入完成后我们简单显示一下输入的密码
                // 也就是说——>实现你的交易逻辑什么的在这里写
                // pwdView.getStrPassword()
                editor.clear();
                editor.putBoolean("isCheck",true);
                editor.putString("password",pwdView.getStrPassword());
                editor.apply();
                finish();
            }
            //取消
            @Override
            public void outfo() {
                //关闭页面
                finish();
            }
            //忘记密码回调事件
            @Override
            public void forgetPwd() {

            }
        });
    }
}
