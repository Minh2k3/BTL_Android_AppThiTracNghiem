package com.example.btl.models;

public class Detail {
    private String title;
    private int image;
    public Detail(String title,int image){
        this.title=title;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
