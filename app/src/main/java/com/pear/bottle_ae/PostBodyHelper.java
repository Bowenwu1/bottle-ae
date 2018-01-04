package com.pear.bottle_ae;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by wubowen on 2018/1/4.
 */

public class PostBodyHelper {


    public RequestBody mapToRequestBody(Map<String, String> map) {
        String strEntity = new Gson().toJson(map);
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
    }

    public RequestBody RequestBottleModelToRequestBody(Bottle bottle) {
        String data = new Gson().toJson(bottle);
        Log.d("JSON", data);
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data);
    }

    public RequestBody ObjToRequestBody(Object obj) {
        String data = new Gson().toJson(obj);
        Log.d("JSON", data);
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data);
    }

}
