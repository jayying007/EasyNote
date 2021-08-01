package com.example.easynote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder>{
    private Context mContext;
    private List<Diary> diaryList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView dataImage;
        TextView dataTime,dataTitle,dataPassage;
        ViewHolder(View view){
            super(view);
            dataTime = view.findViewById(R.id.data_time);
            dataImage = view.findViewById(R.id.data_image);
            dataTitle = view.findViewById(R.id.data_title);
            dataPassage = view.findViewById(R.id.data_passage);
        }
    }
    
    DiaryAdapter(List<Diary> DiaryList){
        diaryList = DiaryList;
    }
    void setDiaryList(List<Diary> DiaryList){
        diaryList = DiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.data_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Diary diary = diaryList.get(position);
                DetailActivity.actionStart(mContext,diary.getTitle(),diary.getPassage(),
                        diary.getYear(),diary.getMonth(),diary.getDay(),diary.getImgPath(),
                        diary.getAge(),diary.getBeauty(),diary.getFaceShape(),diary.getMood(),
                        diary.getId());
//                Toast.makeText(mContext,"You click "+position,Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Diary diary = diaryList.get(position);
        holder.dataTime.setText(String.format("%s/%s\n%s", diary.getMonth(), diary.getDay(),diary.getYear()));
        holder.dataTitle.setText(diary.getTitle());
        //      设置内容
        String passage = diary.getPassage();
        if(passage.length()>50)
            passage = passage.substring(0,50)+"......";
        holder.dataPassage.setText(passage);
        //Glide一行代码及实现复杂图片的加载，需在build.gradle中引用
        String imgPath = diary.getImgPath();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        //byte[] imgBytes = diary.getImgBytes();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        //Glide.with(mContext).load(bitmap).into(holder.dataImage);
        holder.dataImage.setImageBitmap(bitmap);
    }
    @Override
    public int getItemCount(){
        return diaryList.size();
    }
}
