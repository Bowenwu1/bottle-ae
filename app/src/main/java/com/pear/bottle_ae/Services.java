package com.pear.bottle_ae;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by hp on 2017/12/25.
 */

public interface Services {
    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("users")
    Observable<User> postUser(@Body RequestBody route);

    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("sessions")
    Observable<User> loadUser(@Body RequestBody route);

    @GET("users/self")
    Observable<User> get();

    /**
     * Added by Bowen Wu in 2018/01/04
     * Used when create bottle
     */
    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("bottles")
    Observable<ResponseBottle> postBottle(@Body RequestBody route);

    /**
     * Added by Bowen Wu in 2018/01/04
     * Used when get nearby bottle
     */
    @Headers({"Content-type:application/json","Accept: application/json"})
    @GET("bottles/nearby")
    Observable<ResponseBottlesList> getNearbyBottle(@QueryMap Map<String, String> options);


    /**
     * Added by Young in 2018/01/04
     */
    @GET("bottles/{type}")
    Observable<List<Bottle>> getBottle(@Path("type") String type);

}
