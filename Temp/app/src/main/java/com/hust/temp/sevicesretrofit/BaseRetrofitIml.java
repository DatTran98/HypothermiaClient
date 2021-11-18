package com.hust.temp.sevicesretrofit;

import com.hust.temp.Common.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseRetrofitIml {
    private Retrofit retrofit;

    protected Retrofit getRetrofit() {
        if (retrofit == null)
            return new Retrofit.Builder()
                    .baseUrl(Constant.ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        else return retrofit;
    }

}