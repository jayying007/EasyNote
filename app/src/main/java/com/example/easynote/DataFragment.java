package com.example.easynote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DataFragment extends Fragment {
    private List<Diary> diaryList = new ArrayList<>();
    private TextView tips;
    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private String[] months = new String[]{"一","二","三","四","五","六","七","八","九","十","十一","十二"};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diaryList = LitePal.findAll(Diary.class);


    }
    @Override
    public void onStart() {
        super.onStart();
        diaryList = LitePal.findAll(Diary.class);
        diaryAdapter.setDiaryList(diaryList);
        recyclerView.setAdapter(diaryAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_data, container, false);
//      开头时间
        TextView time = view.findViewById(R.id.Time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH);
        time.setText(months[month]+"月 "+year);

//      无内容提示
        tips = view.findViewById(R.id.tips);
        tips.setText("空空如也,"+"\n快来记录有意义的事情吧~");

        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        diaryAdapter = new DiaryAdapter(diaryList);
        recyclerView.setAdapter(diaryAdapter);

        return view;
    }
}
