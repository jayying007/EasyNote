package com.example.easynote;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TextDect {
    public static String[] detect(String text,String access_token){
        // 请求url
        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("text", text);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = access_token;

            final String result = HttpUtil.post(url, accessToken, "application/json", param);

//          解析json
            JSONObject jsonObject = new JSONObject(result).getJSONArray("items").getJSONObject(0);
            String sentiment = String.valueOf(jsonObject.getInt("sentiment"));
            String positive_prob = String.valueOf(jsonObject.getDouble("positive_prob"));

//          返回数据 分类结果、概率
            String[] data = new String[2];
            data[0] = sentiment;
            data[1] = positive_prob;
            Log.d("MainActivity", data[0]);
            Log.d("MainActivity", data[1]);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
