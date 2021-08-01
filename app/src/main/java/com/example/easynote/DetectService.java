package com.example.easynote;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetectService extends Service {
    private DetectBinder mBinder = new DetectBinder();

    public DetectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    class DetectBinder extends Binder{
        public void startDetect(ProgressDialog pD, AlertDialog.Builder aD, ImageView imageView, TextView age, TextView beauty, TextView faceShape, String imgBase64, String access_token){
            new DetectTask(pD,aD,imageView,age,beauty,faceShape).execute("face",imgBase64,access_token);
            Log.d("AddDataActivity", "service");
        }
        public void startDetect(ProgressDialog pD, Diary d , AddDataActivity add, String passage, String access_token){
            new DetectTask(pD,d,add).execute("text",passage,access_token);
            Log.d("AddDataActivity", "service");
        }
    }
}
