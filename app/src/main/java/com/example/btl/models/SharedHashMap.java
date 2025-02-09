package com.example.btl.models;

import java.util.HashMap;

public class SharedHashMap {
    private HashMap<String, Integer> hashMap;

    public SharedHashMap() {
        hashMap = new HashMap<>();
    }

    public HashMap<String, Integer> getHashMap() {
        return hashMap;
    }
}
