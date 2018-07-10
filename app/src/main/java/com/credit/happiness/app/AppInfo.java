package com.credit.happiness.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * application信息保存类, 需要在第一时间初始化该类, 公共库的所有功能都
 *
 * @author yangzhikuan
 */
public class AppInfo {
    private static AppInfo instance = null;

    private Application app; // 当前app实例
    private Thread uiThread; // 当前app启动的ui线程
    private boolean appInBackground = true; // app处于后台
    private Handler uiHandler = null; // 全局ui线程handler
    private Toast globalToast = null; // 全局的toast实例

    public static String IMEI = ""; // 设备标示IMEI
    public static String mac = ""; // mac地址
    public static String operator = ""; // 移动网络操作码
    public static String versionName = ""; // 外部版本标识
    public static String innerVersion = ""; // 手机外部版本标识
    public static int versionCode = 0; // 内部版本号
    public static String screen = ""; // 屏幕信息
    public static float density;// 屏幕密度（0.75 / 1.0 / 1.5）
    public static int screenResolution; // 屏幕分辨率
    public static int screenWidthForPortrait; // 屏幕宽度(竖屏模式下)
    public static int screenHeightForPortrait; // 屏幕高度(竖屏模式下)
    public static String APP_CHANNEL = "UMENG_CHANNEL";


    /**
     * 初始化函数, 必须在application.onCreate()的第一时间进行初始化操作
     *
     * @param app
     */
    public synchronized static void initApp(Application app) {
        if (instance == null) {
            instance = new AppInfo(app);
            instance.initDeviceInfo();
        }
    }


    /**
     * 初始化屏幕信息, 必须在activity.onCreate()第一时间进行初始化
     * 此函数真实只会执行一次, 所有在每个Activity.onCreate()都调用此函数不会有多大开销
     *
     * @param
     */

    private AppInfo(Application app) {
        this.app = app;
        this.uiThread = Thread.currentThread();
        this.uiHandler = new Handler();
        this.globalToast = Toast.makeText(app, "", Toast.LENGTH_SHORT);
    }

    /**
     * @return application context
     */
    public static Context getAppContext() {
        return instance.app;
    }

    /**
     * 当前app是否处于后台
     *
     * @return
     */
    public static boolean isAppInBackground() {
        return instance.appInBackground;
    }

    /**
     * 设置当前app的前后台状态
     *
     * @param backgroundEnable
     */
    public static void setAppInBackground(boolean backgroundEnable) {
        instance.appInBackground = backgroundEnable;
    }


    /**
     * @return ui thread
     */
    public static Thread getUIThread() {
        return instance.uiThread;
    }

    /**
     * @return ui handler
     */
    public static Handler getUIHandler() {
        return instance.uiHandler;
    }

    /**
     * @return global toast
     */
    public static Toast getGlobalToast() {
        return instance.globalToast;
    }

    /**
     * 初始化一些设备信息
     */
    private void initDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
//        IMEI = tm.getDeviceId();
//        innerVersion = tm.getDeviceSoftwareVersion();
//        innerVersion = tm.getDeviceSoftwareVersion();
//        mac = Methods.getLocalMacAddress();
//        if (TextUtils.isEmpty(IMEI)) {
//            IMEI = mac;
//        }
//        operator = tm.getSimOperator();
//        try {
//            PackageManager packageManager = app.getPackageManager();
//            PackageInfo packInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
//            versionName = packInfo.versionName;
//            versionCode = packInfo.versionCode;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 初始化屏幕信息, 必须在activity.onCreate()第一时间进行初始化
     * 此函数真实只会执行一次, 所有在每个Activity.onCreate()都调用此函数不会有多大开销
     *
     * @param activity
     */
    public static void initScreenInfo(Activity activity) {
        if (density != 0) {
            return;
        }
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5 / 2.0）
        screen = "" + metric.widthPixels + "*" + metric.heightPixels;
        screenResolution = metric.widthPixels * metric.heightPixels;
        if (metric.heightPixels >= metric.widthPixels) {
            screenWidthForPortrait = metric.widthPixels;
            screenHeightForPortrait = metric.heightPixels;
        } else {
            screenWidthForPortrait = metric.heightPixels;
            screenHeightForPortrait = metric.widthPixels;
        }
    }

    /**
     * @return application context
     */
    public static Context getContext() {
        return instance.app;
    }

    /**
     * add by jdy
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

}
