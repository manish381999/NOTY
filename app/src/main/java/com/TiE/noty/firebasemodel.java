package com.TiE.noty;

public class firebasemodel {


    private String title;
    private String content;
    private String date;
    private String time;


    public firebasemodel(){

    }

    public firebasemodel(String title, String content, String date, String time) {
        this.title = title;
        this.content = content;
        this.date=date;
        this.time=time;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



}
