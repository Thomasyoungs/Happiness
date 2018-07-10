package com.credit.happiness.app;

import android.app.Application;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * @author yangzhikuan
 */

public class HappinessApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        AppInfo.initApp(this);
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3444193375", "fef4b8a0a523e45130c053e450c89c75", "http://sns.whalecloud.com");
    }

    /**
     * @return
     */
    public synchronized static Context getApplication() {
        return mContext;
    }
}
