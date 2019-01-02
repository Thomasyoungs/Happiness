package com.credit.happiness.retrofit.converter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by dayu-jiang on 2017/12/22.
 */

public class ProgressInterceptor implements Interceptor {
    ProgressListener progressListener = null;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Response response = chain.proceed(chain.request());
        String requestUrl = response.request().url().uri().getPath();
        return response.newBuilder()
                .body(new ProgressResponseBody(response.body(), requestUrl, progressListener))
                .build();
    }
}
