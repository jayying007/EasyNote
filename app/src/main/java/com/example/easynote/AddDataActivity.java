package com.example.easynote;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class AddDataActivity extends AppCompatActivity {
    //  服务
    private DetectService.DetectBinder detectBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            detectBinder = (DetectService.DetectBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    //  接口
    private String access_token;//改
    /** 首先默认个文件保存路径 */
    private static final String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/easynote/image";//保存的确切位置
//    视图
    private ImageView imageView;
    private TextView ageText,beautyText,faceShapeText;
    private EditText titleEdit,passageEdit;
    private String imgPath;
    private ProgressDialog progressDialog;//改
    private TextView time;
//    数据
    private int id;
    private String age,beauty,faceShape;
    private String title,passage;
    private String year,month,day;
    //    相机数据
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    private Bitmap bitmap;
//    接受数据并显示，用于解决（直接创建、浏览原有信息后修改两种方式的跳转）
    public static void actionStart(Context context,String title,
                                   String passage,String imgPath,
                                   String age,String beauty,
                                   String faceShape,int id){
        Intent intent = new Intent(context, AddDataActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("passage",passage);
        intent.putExtra("imgPath",imgPath);
        intent.putExtra("age",age);
        intent.putExtra("beauty",beauty);
        intent.putExtra("faceShape",faceShape);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
//      启动服务
        Intent intent = new Intent(this,DetectService.class);
        startService(intent);
//      绑定服务
        bindService(intent,connection,BIND_AUTO_CREATE);

        // 获取access_token
        new Thread(new Runnable() {
            @Override
            public void run() {
                access_token = Access_token.getAuth();
            }
        }).start();
        initView();
        initData();
        initEvent();
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.toolbar_save) {
//            检查表单
            if(bitmap==null){
                Toast.makeText(AddDataActivity.this,"帅帅的照片呢？",Toast.LENGTH_SHORT).show();
                return false;
            }
            title = titleEdit.getText().toString();
            passage = passageEdit.getText().toString();
            if(title.equals("")){
                Toast.makeText(AddDataActivity.this,"记得写标题哦",Toast.LENGTH_SHORT).show();
                return false;
            }
            if(passage.equals("")){
                Toast.makeText(AddDataActivity.this,"记录一下今天的内容吧",Toast.LENGTH_SHORT).show();
                return false;
            }
//            判断是新创建还是修改已有
            Diary diary;
            if (id == -1) diary = new Diary();
            else diary = LitePal.find(Diary.class, id);
//                先更新数据
            try {
                String fileName = System.currentTimeMillis() + ".jpg";
                saveFile(bitmap, fileName);
                imgPath = SAVE_REAL_PATH + '/' + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }

            age = ageText.getText().toString();
            beauty = beautyText.getText().toString();
            faceShape = faceShapeText.getText().toString();
            //              文本预测-》保存数据
            detectBinder.startDetect(progressDialog,diary,this,passage,access_token);
//                还需获取文本情感分析的结果
            diary.setYear(year);
            diary.setMonth(month);
            diary.setDay(day);
            diary.setTitle(title);
            diary.setPassage(passage);
            diary.setAge(age);
            diary.setBeauty(beauty);
            diary.setFaceShape(faceShape);
            diary.setImgPath(imgPath);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    private void initView(){
        imageView = findViewById(R.id.add_img);
        ageText = findViewById(R.id.add_age);
        beautyText = findViewById(R.id.add_beauty);
        faceShapeText = findViewById(R.id.add_faceShape);
        titleEdit = findViewById(R.id.add_title);
        passageEdit = findViewById(R.id.add_passage);
        time = findViewById(R.id.add_time);
        progressDialog = new ProgressDialog(AddDataActivity.this);
        progressDialog.setMessage("Waiting...");
        progressDialog.setCancelable(false);
        Toolbar toolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initData(){
//        先接受数据
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        passage = intent.getStringExtra("passage");
        faceShape = intent.getStringExtra("faceShape");
        age = intent.getStringExtra("age");
        beauty = intent.getStringExtra("beauty");
        imgPath = intent.getStringExtra("imgPath");
        id = intent.getIntExtra("id",-1);
//        再设置数据
        titleEdit.setText(title);
        passageEdit.setText(passage);
        ageText.setText(String.format("%s%s", ageText.getText(), age));
        beautyText.setText(String.format("%s%s", beautyText.getText(), beauty));
        faceShapeText.setText(String.format("%s%s", faceShapeText.getText(), faceShape));
        bitmap = BitmapFactory.decodeFile(imgPath);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }
//        获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        day = String.valueOf(calendar.get(Calendar.DATE));

        time.setText(year+"/"+month+"/"+day);
    }

    private void initEvent(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] choices = {"使用相机","从相册中选取"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(AddDataActivity.this);
                listDialog.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
//                            使用相机
                            case 0:
                                takePhoto();
                                break;
//                                从相册中选取
                            case 1:
                                chooseFromAlbum();
                                break;
                            default:
                                break;
                        }

                    }
                });
                listDialog.show();
            }
        });
    }


    /*
     *  图片处理部分
     */
    private void takePhoto(){
        if (ContextCompat.checkSelfPermission(AddDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddDataActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }
        else {
            File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //判断sdk版本
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(AddDataActivity.this, "com.example.easynote.fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
            Log.d("url", imageUri.toString());
            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }
    }
    private void chooseFromAlbum(){
        if (ContextCompat.checkSelfPermission(AddDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddDataActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }
    //拍完将图片显示出来
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        if(!imgPath.equals("")){
                            File file = new File(imgPath);
                            file.delete();
                        }
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                        //改
                        String imgBase64 = BmpToBase64.bitmapToBase64(bitmap);
                        //对话框
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddDataActivity.this);
                        alertDialog.setTitle("检测不到人脸,请重新选择哦");
                        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        detectBinder.startDetect(progressDialog,alertDialog,imageView
                                ,ageText,beautyText,faceShapeText,imgBase64,access_token);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
//    打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
//    读取相册授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    显示相册图片
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if(uri != null) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // 如果是document类型的Uri，则通过document id处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1]; // 解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            displayImage(imagePath); // 根据图片路径显示图片
        }
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            if(!imgPath.equals("")){
                File file = new File(imgPath);
                file.delete();
            }
            bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            String imgBase64 = BmpToBase64.bitmapToBase64(bitmap);
            //
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddDataActivity.this);
            alertDialog.setTitle("检测不到人脸,请重新选择哦");
            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            detectBinder.startDetect(progressDialog,alertDialog,imageView,ageText,beautyText,faceShapeText,imgBase64,access_token);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
/*
//    将图片转为字节
    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return baos.toByteArray();
    }*/
//    存储图片
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        String subFolder = SAVE_REAL_PATH;
        File folder = new File(subFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File myCaptureFile = new File(subFolder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
//        Toast.makeText(AddDataActivity.this,subFolder,Toast.LENGTH_SHORT).show();
    }
}
