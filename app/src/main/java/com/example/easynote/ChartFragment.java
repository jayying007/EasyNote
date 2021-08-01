package com.example.easynote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


public class ChartFragment extends Fragment implements OnChartValueSelectedListener {
    private List<Diary> diaryList = new ArrayList<>();
    private LineChart chart;
    private View view;
    private Context context;
    private TextView timeText,titleText,passageText;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private String time,imgPath,title,passage;//页面显示内容
    private Diary diary;//用于传送到详情页面
    private IAxisValueFormatter formatter;
    private List<String> quarters;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        quarters = new ArrayList<>();
        formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if(index>=0&&index<quarters.size()){
                    return quarters.get(index);
                }
                else if(index==quarters.size()&&index!=0&&index!=1){
                    return quarters.get(index-1);
                }
                else{
                    return "";
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        diaryList = LitePal.findAll(Diary.class);
        if(diaryList.size()==0)return;
        //更新横坐标
        quarters.clear();
        for(int i=0;i<diaryList.size();i++){
            String year = diaryList.get(i).getYear();
            String month = diaryList.get(i).getMonth();
            String day = diaryList.get(i).getDay();
            quarters.add(year.substring(2,4)+"年"+month+"月"+day+"日");
        }
        Log.d("1234", "onStart: "+quarters.size());
        //颜值曲线上的点
        List<Entry> beautyEntries = new ArrayList<>();
        for(int i=0;i<diaryList.size();i++){
            Entry entry = new Entry();
            entry.setX(i);
            entry.setY(Integer.parseInt(diaryList.get(i).getBeauty()));
            entry.setData(diaryList.get(i));
            beautyEntries.add(entry);
        }
        //曲线的属性设置
        LineDataSet beautyDataSet = new LineDataSet(beautyEntries,"颜值");
        beautyDataSet.setCubicIntensity(0.1f);//曲率，1为折线
        beautyDataSet.setColor(Color.RED);//线的颜色
        beautyDataSet.setCircleRadius(5f);//圆圈大小
        beautyDataSet.setCircleColor(Color.RED);//圆圈颜色
        //心情曲线上的点
        List<Entry> moodEntries = new ArrayList<>();
        for(int i=0;i<diaryList.size();i++){
            Entry entry = new Entry();
            //entry.setX(Integer.parseInt(diaryList.get(i).getDay()));
            entry.setX(i);
            String[] moods = diaryList.get(i).getMood().split(",");
            entry.setY(Float.parseFloat(moods[1])*100);
            entry.setData(diaryList.get(i));
            moodEntries.add(entry);
        }
        //心情曲线的属性设置
        LineDataSet moodDataSet = new LineDataSet(moodEntries,"心情");
        moodDataSet.setCubicIntensity(0.1f);//曲率，1为折线
        moodDataSet.setColor(Color.GREEN);
        moodDataSet.setCircleRadius(5f);
        moodDataSet.setCircleColor(Color.GREEN);
        //加这两条线加入其中
        List<ILineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetList.add(beautyDataSet);
        lineDataSetList.add(moodDataSet);
        LineData lineData = new LineData(lineDataSetList);

        YAxis yAxisR = chart.getAxisRight();//取消右侧Y轴
        yAxisR.setEnabled(false);
        YAxis yAxisL = chart.getAxisLeft();
        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxisL.setAxisMinimum(0f);
        yAxisL.setAxisMaximum(100f);
        yAxisL.setTextSize(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        xAxis.setDrawGridLines(false);//竖线
        xAxis.setGranularity(1f);//间隔
        xAxis.setTextSize(10f);


        chart.setData(lineData);
        //动画效果
//        chart.animateXY(1000,1000);
//        描述
        chart.setDescription(null);
//        背景颜色
        chart.setBackgroundColor(getResources().getColor(R.color.background));
        chart.setDragEnabled(true);//可以拖动
        chart.setScaleEnabled(false);//无法缩小
        chart.setPinchZoom(false);//无法放大
        chart.setVisibleXRangeMaximum(3);//最大容纳

        chart.setOnChartValueSelectedListener(this);
        chart.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_chart, container, false);
        chart = view.findViewById(R.id.chart);
        timeText = view.findViewById(R.id.chart_time);
        titleText = view.findViewById(R.id.chart_title);
        passageText = view.findViewById(R.id.chart_passage);
        imageView = view.findViewById(R.id.chart_image);
        linearLayout = view.findViewById(R.id.chart_item);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(diary!=null){
                    DetailActivity.actionStart(context,diary.getTitle(),
                            diary.getPassage(),diary.getYear(),diary.getMonth(),
                            diary.getDay(),diary.getImgPath(),diary.getAge(),
                            diary.getBeauty(),diary.getFaceShape(),
                            diary.getMood(),diary.getId());
                    try {
                        Thread.sleep(500);
                        timeText.setText(null);
                        titleText.setText(null);
                        passageText.setText(null);
                        imageView.setImageBitmap(null);
                        diary=null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        return view;
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        diary = (Diary)e.getData();
        title = diary.getTitle();
        passage = diary.getPassage();
        time = diary.getMonth()+'/'+diary.getDay()+'\n'+diary.getYear();
        imgPath = diary.getImgPath();

        titleText.setText(title);
        passageText.setText(passage);
        timeText.setText(time);
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        imageView.setImageBitmap(bitmap);
    }
    @Override
    public void onNothingSelected() {

    }
}
