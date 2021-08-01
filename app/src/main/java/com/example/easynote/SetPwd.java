package com.example.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class SetPwd extends AppCompatActivity {
    private Switch lock;
    private LinearLayout changePwd;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);

        initView();
        initData();
        initEvent();
    }

    private void initView(){
        lock = findViewById(R.id.lock);
        changePwd = findViewById(R.id.set_pwd);
        preferences = getSharedPreferences("password",MODE_PRIVATE);
        editor = preferences.edit();
        Toolbar toolbar = findViewById(R.id.pwd_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initData(){
//      初始化开关
        boolean check = preferences.getBoolean("isCheck",false);
        lock.setChecked(check);

//      更改密码是否显示
        if(check==true){
            changePwd.setVisibility(View.VISIBLE);
        }
        else{
            changePwd.setVisibility(View.GONE);
        }
    }

    private void initEvent(){
        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked==true){
//                    前往设置密码页面
                    Intent intent = new Intent(SetPwd.this,ChangePwd.class);
                    startActivityForResult(intent,1);
                }
                else{
//                   清除
                    changePwd.setVisibility(View.GONE);
                    editor.clear();
                    editor.apply();
                }
            }
        });
        changePwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                前往设置密码替换旧的
                Intent intent = new Intent(SetPwd.this,ChangePwd.class);
                startActivityForResult(intent,1);
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        initData();
    }
}
