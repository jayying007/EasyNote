package com.example.easynote;

import org.litepal.crud.LitePalSupport;

public class Diary extends LitePalSupport {
    private int id;
    private String year,month,day;
    private String title,passage;
    private String mood;
    private String imgPath;
    private String age,beauty,faceShape;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    String getImgPath() {
        return imgPath;
    }
    void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    String getYear() {
        return year;
    }
    void setYear(String year) {
        this.year = year;
    }

    String getMonth() {
        return month;
    }
    void setMonth(String month) {
        this.month = month;
    }

    String getDay() {
        return day;
    }
    void setDay(String day) {
        this.day = day;
    }

    String getTitle() {
        return title;
    }
    void setTitle(String title) {
        this.title = title;
    }

    String getPassage() {
        return passage;
    }
    void setPassage(String passage) {
        this.passage = passage;
    }

    String getMood() {
        return mood;
    }
    void setMood(String mood) {
        this.mood = mood;
    }

    String getAge() {
        return age;
    }
    void setAge(String age) {
        this.age = age;
    }

    String getBeauty() {
        return beauty;
    }
    void setBeauty(String beauty) {
        this.beauty = beauty;
    }

    String getFaceShape() {
        return faceShape;
    }
    void setFaceShape(String faceShape) {
        this.faceShape = faceShape;
    }
}
