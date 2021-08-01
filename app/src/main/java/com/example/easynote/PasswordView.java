package com.example.easynote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PasswordView extends RelativeLayout {

    Context context;
    private String strPassword; // 输入的密码
    private TextView[] tvList; // 就6个输入框不会变了，用数组内存申请固定空间，比List省空间
    private GridView gridView; // 用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能
    private ArrayList<Map<String, String>> valueList; // 要用Adapter中适配，用数组不能往adapter中填充
    private ImageView tvCancel;//取消按钮
    private TextView tvForget;//忘记密码按钮
    private int currentIndex = -1; // 用于记录当前输入密码格位置
    private TextView title;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //view布局
        View view = View.inflate(context, R.layout.layout_popup_bottom, null);
        valueList = new ArrayList<>();
        tvList = new TextView[6];
        //初始化控件
        tvCancel = (ImageView) view.findViewById(R.id.tvCancel);
        tvList[0] = (TextView) view.findViewById(R.id.tv_pass1);
        tvList[1] = (TextView) view.findViewById(R.id.tv_pass2);
        tvList[2] = (TextView) view.findViewById(R.id.tv_pass3);
        tvList[3] = (TextView) view.findViewById(R.id.tv_pass4);
        tvList[4] = (TextView) view.findViewById(R.id.tv_pass5);
        tvList[5] = (TextView) view.findViewById(R.id.tv_pass6);
        title = (TextView) view.findViewById(R.id.title);

        //初始化键盘
        gridView = (GridView) view.findViewById(R.id.gv_keybord);
        //设置键盘显示按钮到集合
        setView();

        // 必须要，不然不显示控件
        addView(view);
    }

    //设置按钮显示内容
    private void setView() {

        // 初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 12) {
                map.put("name", "<<-");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            }
            valueList.add(map);
        }

        //为键盘gridview设置适配器
        gridView.setAdapter(adapter);

        //为键盘按键添加点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击0~9按钮
                if (position < 11 && position != 9) {
                    // 判断输入位置————要小心数组越界
                    if (currentIndex >= -1 && currentIndex < 5) {
                        tvList[++currentIndex].setText(valueList.get(position).get("name"));
                    }
                } else {
                    // 点击退格键
                    if (position == 11) {
                        // 判断是否删除完毕————要小心数组越界
                        if (currentIndex - 1 >= -1) {
                            tvList[currentIndex--].setText("");
                        }
                    }
                }
            }
        });
    }

    // 设置监听方法，在第6位输入完成后触发
    public void setOnFinishInput(final OnPasswordInputFinish pass) {

        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    // 每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    strPassword = "";
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }
                    // 接口中要实现的方法，完成密码输入完成后的响应逻辑
                    pass.inputFinish();
                }
            }
        });

        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击取消调用接口
                pass.outfo();
            }
        });

    }

    //获取输入的密码
    public String getStrPassword() {
        return strPassword;
    }

    // GrideView的适配器
    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                //装载数字键盘布局
                convertView = View.inflate(context, R.layout.item_gride, null);
                viewHolder = new ViewHolder();
                //初始化键盘按钮
                viewHolder.btnKey = convertView.findViewById(R.id.btn_keys);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //设置按钮显示数字
            viewHolder.btnKey.setText(valueList.get(position).get("name"));
            if (position == 9) {
                //设置按钮背景
                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
                //设置按钮不可点击
                viewHolder.btnKey.setEnabled(false);
            }
            if (position == 11) {
                //设置按钮背景
                viewHolder.btnKey.setBackgroundResource(R.drawable.selector_key_del);
            }
            return convertView;
        }

    };

    public final class ViewHolder {
        public TextView btnKey;
    }

    public void setTitle(String str){
        title.setText(str);
    }
}