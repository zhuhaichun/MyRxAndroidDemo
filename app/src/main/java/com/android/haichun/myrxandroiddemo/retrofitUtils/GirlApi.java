package com.android.haichun.myrxandroiddemo.retrofitUtils;

import com.android.haichun.myrxandroiddemo.model.GirlData;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface GirlApi {
    @GET("data/福利/10/{page}")
    Observable<Result<GirlData>> fetchPrettyGirl(@Path("page") int page);
}
