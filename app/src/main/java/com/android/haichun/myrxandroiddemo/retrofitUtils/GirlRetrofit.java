package com.android.haichun.myrxandroiddemo.retrofitUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GirlRetrofit {
    private static final String GANK_URL = "http://gank.io/api/";
    private final GirlApi mGirlApi;
    public GirlRetrofit(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(15,TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GANK_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
        mGirlApi = retrofit.create(GirlApi.class);
    }

    public GirlApi getGirlApi(){
        return mGirlApi;
    }
}
