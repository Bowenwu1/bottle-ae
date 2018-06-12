package com.pear.bottle_ae.Service;

import com.pear.bottle_ae.Model.ResponseBottle;
import com.pear.bottle_ae.Model.ResponseBottlesList;
import com.pear.bottle_ae.Model.ResponseUser;
import com.pear.bottle_ae.Model.ResponseReadersList;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by jiayin on 2017/12/25.
 */

public interface Services {
    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("users")
    Observable<ResponseUser> postUser(@Body RequestBody route);

    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("sessions")
    Observable<ResponseUser> loadUser(@Body RequestBody route);

    @GET("users/self")
    Observable<ResponseUser> get();

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
    Observable<ResponseBottlesList> getBottle(@Path("type") String type);

    /**
     * Added by Bowen Wu in 2018/01/05
     * Used to open bottle
     */
    @Headers({"Content-type:application/json","Accept: application/json"})
    @POST("bottles/{bottle_id}/open")
    Observable<ResponseBottle> openBottle(@Path("bottle_id") int bottle_id);

    /**
     *  Added by zhuojun in 2018/06/12
     */
    @Headers({"Content-type:application/json","Accept: application/json"})
    @GET("bottles/{bottle_id}/openers")
    Observable<ResponseReadersList> getBottleOpeners(@Path("bottle_id") int bottle_id);
}
