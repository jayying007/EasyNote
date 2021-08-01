package com.example.easynote;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class DetectTask extends AsyncTask<String,String,String[]> {
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;
//  人脸预测
    private TextView ageText, beautyText, faceShapeText;
    private ImageView imageView;
    //  人脸脸型对应关系
    private HashMap<String,String> map = new HashMap<>();

    private String detect_type;
    private Diary diary;
    private AddDataActivity addDataActivity;

    public DetectTask(ProgressDialog pD, AlertDialog.Builder aD, ImageView img, TextView age, TextView beauty, TextView faceShape){
        progressDialog = pD;
        imageView = img;
        alertDialog = aD;
        ageText = age;
        beautyText = beauty;
        faceShapeText = faceShape;
        map.put("square","正方形");
        map.put("triangle","三角形");
        map.put("oval","椭圆");
        map.put("heart","心形");
        map.put("round","圆形");
    }
    public DetectTask(ProgressDialog pD,Diary d ,AddDataActivity add){
        progressDialog = pD;
        diary = d;
        addDataActivity = add;
        map.put("square","正方形");
        map.put("triangle","三角形");
        map.put("oval","椭圆");
        map.put("heart","心形");
        map.put("round","圆形");
    }

    protected void onPreExecute(){
        progressDialog.show();
    }

    protected String[] doInBackground(String... params){
        if (params[0].equals("face")){
            detect_type = params[0];
            return FaceDect.detect(params[1], params[2]);
        }
        else{
            detect_type = params[0];
            return TextDect.detect(params[1],params[2]);
        }
    }

    protected void onProgressUpdate(String... values){

    }

    protected void onPostExecute(String[] result){
        progressDialog.dismiss();
        if(detect_type.equals("face"))
        {
            if(result!=null){
                ageText.setText(result[0]);
                beautyText.setText(result[1]);
                faceShapeText.setText(map.get(result[2]));


            }
            else{
                ageText.setText("");
                beautyText.setText("");
                faceShapeText.setText("");
                Log.d("face error", "no result" );
                alertDialog.show();

                //imageView.setImageBitmap(BitmapFactory.decodeResource(addDataActivity.getResources(),R.drawable.camera));
            }
        }
        else{
            if(result!=null){
                String data = result[0]+","+result[1];
                diary.setMood(data);
            }
            diary.save();
            addDataActivity.finish();
        }

    }
}
