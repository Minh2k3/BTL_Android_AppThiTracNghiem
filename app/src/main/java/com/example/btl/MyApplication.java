package com.example.btl;

import android.app.Application;
import android.content.Context;

import com.example.btl.models.SharedHashMap;

public class MyApplication extends Application {
    private SharedHashMap sharedHashMap;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedHashMap = new SharedHashMap();
    }

    public SharedHashMap getSharedHashMap() {
        return sharedHashMap;
    }
}
