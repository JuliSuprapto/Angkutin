package com.example.angkut_v01.utils;

import com.google.gson.Gson;

public class GsonHelper {

    public static Object parseGson(String content, Object obj){
        obj = obj.getClass();
        obj = new Gson().fromJson(content, (Class<Object>) obj);
        return obj;
    }

}
