package com.example.easynote;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ImageView imageView,mood_ico;
    private TextView ageText,beautyText,faceShapeText;
    private TextView timeText,moodText;
    private TextView titleText,passageText;
//    数据
    private int id;
    private String title,passage;
    private String imgPath;
    private String age,beauty,faceShape;

    public static void actionStart(Context context, String title,
                                   String passage, String year,
                                   String month, String day,
                                   String imgPath, String age,
                                   String beauty, String faceShape,
                                   String mood,int id){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("passage",passage);
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        intent.putExtra("imgPath",imgPath);
        intent.putExtra("age",age);
        intent.putExtra("beauty",beauty);
        intent.putExtra("faceShape",faceShape);
        intent.putExtra("mood",mood);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        initData();
//        List<String> permission = new ArrayList<>();
//        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        permission.add(Manifest.permission.INTERNET);
//        permission.add(Manifest.permission.ACCESS_NETWORK_STATE);
//        permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        permission.add(Manifest.permission.ACCESS_WIFI_STATE);
//        permission.add(Manifest.permission.CHANGE_WIFI_STATE);
//        permission.add(Manifest.permission.READ_PHONE_STATE);
//        permission.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
//        permission.add(Manifest.permission.BLUETOOTH);
//        permission.add(Manifest.permission.BLUETOOTH_ADMIN);
//
//        String[] permissions = permission.toArray(new String[permission.size()]);
//        ActivityCompat.requestPermissions(DetailActivity.this,permissions,1);


    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.toolbar_delete:
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(DetailActivity.this);
                deleteDialog.setTitle("确认删除该记录吗？");
                deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LitePal.delete(Diary.class,id);
                        if(!imgPath.equals("")){
                            File file = new File(imgPath);
                            file.delete();
                        }
                        Toast.makeText(DetailActivity.this,"删除成功"+id,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                deleteDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                deleteDialog.show();
                break;
            case R.id.toolbar_update:
                AlertDialog.Builder updateDialog = new AlertDialog.Builder(DetailActivity.this);
                updateDialog.setTitle("是否修改该记录？");
                updateDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddDataActivity.actionStart(DetailActivity.this,title,
                                passage,imgPath,age,beauty,faceShape,id);
                        finish();
                    }
                });
                updateDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                updateDialog.show();
                break;
            default:
                break;
        }
        return true;
    }
    private void initView(){
        //    组件
        imageView = findViewById(R.id.detail_img);
        mood_ico = findViewById(R.id.mood_ico);
        ageText = findViewById(R.id.detail_age);
        beautyText = findViewById(R.id.detail_beauty);
        faceShapeText = findViewById(R.id.detail_faceShape);
        timeText = findViewById(R.id.detail_time);
        moodText = findViewById(R.id.detail_mood);
        titleText = findViewById(R.id.detail_title);
        passageText = findViewById(R.id.detail_passage);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
    }
    private void initData(){
//        接收数据
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        passage = intent.getStringExtra("passage");
//        无需传递，设为局部变量
        String year = intent.getStringExtra("year");
        String month = intent.getStringExtra("month");
        String day = intent.getStringExtra("day");
        imgPath = intent.getStringExtra("imgPath");
        age = intent.getStringExtra("age");
        beauty = intent.getStringExtra("beauty");
        faceShape = intent.getStringExtra("faceShape");
        long mood_value = Math.round(Double.valueOf(intent.getStringExtra("mood").split(",")[1])*100);
        String mood = String.valueOf(mood_value);
        id = intent.getIntExtra("id",-1);
//        设置数据
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        //Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        imageView.setImageBitmap(bitmap);
        ageText.setText(String.format("%s%s", ageText.getText(), age));
        beautyText.setText(String.format("%s%s", beautyText.getText(), beauty));
        faceShapeText.setText(String.format("%s%s", faceShapeText.getText(), faceShape));
        titleText.setText(String.format("%s%s", titleText.getText(), title));
        passageText.setText(String.format("%s%s", passageText.getText(), passage));
        timeText.setText(String.format("%s%s/%s/%s", timeText.getText(), year, month, day));
        moodText.setText(String.format("%s%s", moodText.getText(), mood));

        if (mood_value<=25)
            mood_ico.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.cry));
        else if(mood_value<=50)
            mood_ico.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.sad));
        else if (mood_value<=75)
            mood_ico.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.smile));
        else
            mood_ico.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.happy));
    }
}
