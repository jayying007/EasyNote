package com.example.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class InputPwd extends AppCompatActivity {
    private PasswordView pwdView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pwd);

        initView();
        initEvent();
    }

    private void initView(){
        pwdView = findViewById(R.id.pwd_view);
        pwdView.setTitle("输入密码");
        preferences = getSharedPreferences("password",MODE_PRIVATE);
        editor = preferences.edit();
        Intent intent = getIntent();
        target = intent.getStringExtra("target");
    }

    private void initEvent(){
        // 添加回调接口
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish() {
                // 输入完成后我们简单显示一下输入的密码
                // 也就是说——>实现你的交易逻辑什么的在这里写
                // pwdView.getStrPassword()
                String password = preferences.getString("password","");
                if(pwdView.getStrPassword().equals(password))
                {
                    if(target.equals("SetPwd")){
                        Intent intent = new Intent(InputPwd.this,SetPwd.class);
                        startActivity(intent);
                    }
                    finish();
                }
                else{
                    Toast.makeText(InputPwd.this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            }
            //取消
            @Override
            public void outfo() {
                //关闭页面
                if (target.equals("MainActivity")){
                    MainActivity.mainActivity.finish();
                }
                finish();
            }
            //忘记密码回调事件
            @Override
            public void forgetPwd() {

            }
        });
    }
    public void onBackPressed(){
        super.onBackPressed();
        MainActivity.mainActivity.finish();
    }
}
