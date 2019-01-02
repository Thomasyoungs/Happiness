package com.credit.happiness.retrofit.converter;

import android.os.Looper;
import android.text.TextUtils;


import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;

import com.credit.happiness.app.HappinessApplication;
import com.credit.happiness.retrofit.util.NetUtil;

/**
 * 缓存拦截器
 * Created by dayu-jiang on 2017/12/22.
 */

public class CacheInterceptor implements Interceptor {
    private String TAG = "CacheInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //缓存
        if (NetUtil.getNetWorkState(HappinessApplication.mContext) == NetUtil.NETWORK_NONE) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }

        okhttp3.Response response = chain.proceed(request);
        String requestUrl = response.request().url().uri().getPath();
        if (!TextUtils.isEmpty(requestUrl)) {
            if (requestUrl.contains("get_goods_type")) {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
//                createObservable("现在请求的是分类接口");
            }
        }
        //缓存响应
        if (NetUtil.getNetWorkState(HappinessApplication.mContext) != NetUtil.NETWORK_NONE) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=120")
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
