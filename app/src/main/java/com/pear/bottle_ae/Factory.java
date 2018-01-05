package com.pear.bottle_ae;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 2017/12/26.
 */

public class Factory {

    private final static String BASE_URL = "https://bottle.resetbypear.com/api/";
    private static Services servicesInstance = null;

    public   static OkHttpClient createOkhttp(Context context) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                //.addInterceptor(httpLoggingInterceptor)
                .cookieJar(cookieJar)
                .build();
        return okHttpClient;
    }
    public  static Retrofit createRetrofit(String baseUrl , Context context) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkhttp(context))
                .build();

    }

    public static Services getServices(Context context) {
//        if (null == servicesInstance) {
//            Retrofit retrofit = createRetrofit(BASE_URL, context);
//            servicesInstance = retrofit.create(Services.class);
//        }
        Retrofit retrofit = createRetrofit(BASE_URL, context);
        servicesInstance = retrofit.create(Services.class);
       return servicesInstance;
    }
}
