package com.credit.happiness.retrofit;

import android.content.Context;

import com.credit.happiness.app.HappinessApplication;
import com.credit.happiness.retrofit.converter.CacheInterceptor;
import com.credit.happiness.retrofit.converter.HttpCommonInterceptor;
import com.credit.happiness.retrofit.converter.MyGsonConverFactory;
import com.credit.happiness.retrofit.converter.ProgressInterceptor;
import com.credit.happiness.retrofit.converter.ProgressListener;
import com.credit.happiness.retrofit.converter.Retry;
import com.credit.happiness.retrofit.converter.StringConverterFactory;
import com.credit.happiness.retrofit.cookie.CookieJarImpl;
import com.credit.happiness.retrofit.util.NetUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haipay.credit.retrofit.listener.ProgressLisenter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 *
 */

public class RetrofitNetHelper implements HttpLoggingInterceptor.Logger, ProgressListener {
    public static RetrofitNetHelper mInstance;
    private final Cache cache;
    public Retrofit mRetrofit;
    public OkHttpClient mOkHttpClient;
    public HttpLoggingInterceptor mHttpLogInterceptor;
    private HttpCommonInterceptor commonInterceptor;
    private Interceptor mUrlInterceptor;
    private Context mContext;
    public Gson mGson;
    public ProgressLisenter progressLisenter = null;

    private RetrofitNetHelper(Context context) {
        this.mContext = context;
        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        mHttpLogInterceptor = new HttpLoggingInterceptor(this);
        mHttpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 添加公共参数拦截器
        commonInterceptor = new HttpCommonInterceptor.Builder()
                .addHeaderParams("paltform", "android")
                .build();

        mUrlInterceptor = new CacheInterceptor();

//        InputStream in = null;
//        HttpsUtils.SSLParams sslParams = null;
//        try {
//            in = DFQApp.mContext.getAssets().open("dafy.cer");
//            if (in != null) {
//                sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{in}, null, null);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                in.close();
//            } catch (Exception e) {
//
//            } an'zhuo
//        }
        //添加缓存cookie
//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
        //添加缓存
        File cacheFile = new File(mContext.getCacheDir(), "HttpCache");
        cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb


        NetUtil.SSLParams sslParams = NetUtil.getSSLSocketFactory();
        assert sslParams != null;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.cookieJar(new CookieJarImpl());
        if (sslParams != null) {
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        }

        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(new Retry(1));
        builder.addInterceptor(mHttpLogInterceptor);
        builder.addInterceptor(commonInterceptor);
        builder.addInterceptor(mUrlInterceptor);
        builder.addNetworkInterceptor(new ProgressInterceptor(this));

//        builder.cache(cache);

        mOkHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetUtil.BASE_URL)
                .addConverterFactory(StringConverterFactory.create())//解析成字符串
                .addConverterFactory(MyGsonConverFactory.create(mGson))//加载自定义解析器 解析加密数据
                .client(mOkHttpClient)
                .build();
    }

    /**
     * 获取网络请求实例
     *
     * @param flag 是否注销当前实例 用于解决两个框架登录cookie问题 后续可删除
     * @return
     */
    public static RetrofitNetHelper getInstance(boolean flag) {
        if (flag) {
            mInstance = null;
        }

        if (mInstance == null) {
            synchronized (RetrofitNetHelper.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitNetHelper(HappinessApplication.mContext);
                }
            }
        }
        return mInstance;
    }

    public <T> T getAPIService(Class<T> service) {
        return mRetrofit.create(service);
    }


    public Cache getCache() {
        return cache;
    }

    public void clearCache() throws IOException {
        cache.delete();
    }

    @Override
    public void log(String message) {
        if (message.startsWith("Content-Type") ||
                message.startsWith("Content-Length") ||
                message.startsWith("Server") ||
                message.startsWith("Date") ||
                message.startsWith("Vary") ||
                message.startsWith("X-UA-Compatible") ||
                message.startsWith("Transfer-Encoding") ||
                message.startsWith("Connection") ||
                message.startsWith("Cache-Control") ||
                message.startsWith("PK") ||
                message.startsWith("x-oss") ||
                message.startsWith("Content-MD5") ||
                message.startsWith("Via") ||
                message.startsWith("X-Cache") ||
                message.startsWith("Timing") ||
                message.startsWith("EagleId") ||
                message.startsWith("Proxy") ||
                message.startsWith("Content-Disposition")) {


        } else {
        }

    }

    @Override
    public void onProgress(String url, long progress, long total, boolean done) {
        if (progressLisenter != null) {
            progressLisenter.progressUpdate(url, total, progress, done);
        }
    }

    public void setProgressLisenter(ProgressLisenter progressLisenter) {
        this.progressLisenter = progressLisenter;
    }
}
