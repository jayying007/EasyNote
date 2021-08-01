package com.example.easynote;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FaceDect {
    public static String[] detect(String image ,String access_token) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", image);
            map.put("face_field", "age,faceshape,beauty");
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = access_token;

            final String result = HttpUtil.post(url, accessToken, "application/json", param);

//          解析json（默认只提取第一条数据，若多张人脸则循环）
            JSONArray jsonArray = new JSONObject(result).getJSONObject("result").getJSONArray("face_list");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String age = String.valueOf(jsonObject.getDouble("age"));
            String beauty = String.valueOf(jsonObject.getInt("beauty"));
            String face_shape = jsonObject.getJSONObject("face_shape").getString("type");
            Log.d("MainActivity", age);
            Log.d("MainActivity", beauty);
            Log.d("MainActivity", face_shape);

//          返回年龄、颜值、脸型
            String [] data = new String[3];
            data[0]=age;
            data[1]=beauty;
            data[2]=face_shape;

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
