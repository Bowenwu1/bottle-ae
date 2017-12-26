package com.pear.bottle_ae;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by hp on 2017/12/25.
 */

public interface Services {
    @GET("users/{user}/")
    Observable<User> getUser(@Path("user") String user);
}
