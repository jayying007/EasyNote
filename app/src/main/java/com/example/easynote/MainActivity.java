package com.example.easynote;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //  主活动单例
    public static MainActivity mainActivity;
    private BottomNavigationViewEx bnve;
    private VpAdapter adapter;
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView diary_num;
    private TextView mood_avg;
    //  密码存储
    private SharedPreferences preferences;
    //  网络广播
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;


        preferences = getSharedPreferences("password",MODE_PRIVATE);
//      是否需要密码
        boolean check = preferences.getBoolean("isCheck",false);
        if(check){
            Intent intent = new Intent(MainActivity.this,InputPwd.class);
            intent.putExtra("target","MainActivity");
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);
        //创建数据库
        LitePal.getDatabase();
        initView();
        initData();
        initBNVE();
        initEvent();

        //网络广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

    }
    //   该程序销毁时取消注册
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
    /**
     * init BottomNavigationViewEx envent
     */
    private void initEvent() {
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.menu_main:
                        position = 0;
                        break;
                    case R.id.menu_chart:
                        position = 2;
                        break;
                    case R.id.menu_add: {
                        //position = 1;
                        //此处return false且在FloatingActionButton没有自定义点击事件时 会屏蔽点击事件
                        return false;
                    }
                    default:
                        break;
                }
                if (previousPosition != position) {
                    viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                /*if (1 == position) {
                    floatingActionButton.setImageResource(R.drawable.ic_add);

                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_add);

                }*/
               /* // 1 is center 此段结合屏蔽FloatingActionButton点击事件的情况使用
                  //在viewPage滑动的时候 跳过最中间的page也
                if (position >= 1) {
                    position++;
                }*/
                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
/*
 * fab 点击事件结合OnNavigationItemSelectedListener中return false使用
 */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDataActivity.actionStart(MainActivity.this,"",
                        "","","","","",-1);
            }
        });


//      抽屉导航栏
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_pwd:
                        boolean ischeck = preferences.getBoolean("isCheck",false);
                        Intent intent;
                        if(ischeck)
                        {
                            intent= new Intent(MainActivity.this,InputPwd.class);
                            intent.putExtra("target","SetPwd");
                        }
                        else
                            intent = new Intent(MainActivity.this,SetPwd.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }



    private void initView() {
        floatingActionButton = findViewById(R.id.fab);
        viewPager = findViewById(R.id.vp);
        bnve = findViewById(R.id.bnve);
        toolbar = findViewById(R.id.index_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        mood_avg = navView.getHeaderView(0).findViewById(R.id.mood_avg);
        diary_num = navView.getHeaderView(0).findViewById(R.id.diary_num);
    }

    /**
     * create fragments
     */
    private void initData() {
        //第一个页面
        fragments = new ArrayList<>(2);
        DataFragment dataFragment = new DataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "记录");
        dataFragment.setArguments(bundle);
        //第二个页面
        ChartFragment chartFragment = new ChartFragment();
        bundle = new Bundle();
        bundle.putString("title", "统计");
        chartFragment.setArguments(bundle);

        fragments.add(dataFragment);
        fragments.add(chartFragment);
    }

    /**
     * init BottomNavigationViewEx
     */
    private void initBNVE() {
        //取消动画效果
//        bnve.enableAnimation(false);
//        bnve.enableShiftingMode(false);
//        bnve.enableItemShiftingMode(false);

        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                //DrawerLayout
                List<Diary> diaryList = new ArrayList<>();
                diaryList =  LitePal.findAll(Diary.class);
                if (diaryList==null)
                    diary_num.setText(0);
                else
                    diary_num.setText(" "+String.valueOf(diaryList.size())+" ");
                long sum=0;
                for(Diary diary:diaryList){
                    sum+=Math.round(Double.valueOf(diary.getMood().split(",")[1])*100);
                }
                long avg = Math.round((double)sum/(double)diaryList.size());
                mood_avg.setText(" "+String.valueOf(avg)+" ");

                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }
    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }


    //  监听网络（广播）
    class NetworkChangeReceiver extends BroadcastReceiver {
        public void onReceive(Context context , Intent intent){

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null&&networkInfo.isAvailable()){
                Toast.makeText(context, "network is available", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"network is unavailable",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
