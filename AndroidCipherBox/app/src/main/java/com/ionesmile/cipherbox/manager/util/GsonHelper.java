package com.ionesmile.cipherbox.manager.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iOnesmile on 2016/8/15 0015.
 */
public class GsonHelper {

    public static String toJson(Object src){
        try {
            return new Gson().toJson(src);
        } catch (Exception e){
            Log.w("GsonHelper", "", e);
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return new Gson().fromJson(json, clazz);
        } catch (Exception e){
            Log.w("GsonHelper", "", e);
        }
        return null;
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            List<T> lst =  new ArrayList<T>();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for(final JsonElement elem : array){
                lst.add(new Gson().fromJson(elem, clazz));
            }
            return lst;
        } catch (Exception e){
            Log.w("GsonHelper", "", e);
        }
        return null;
    }

    public static <T> T fromJsonObject(String json, Class<T> clazz) {
        try {
            JsonElement elem = new JsonParser().parse(json).getAsJsonObject();
            return new Gson().fromJson(elem, clazz);
        } catch (Exception e){
            Log.w("GsonHelper", "", e);
        }
        return null;
    }
}
