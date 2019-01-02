package com.credit.happiness.retrofit.cookie;

import android.text.TextUtils;


import com.alipay.share.sdk.Constant;
import com.credit.happiness.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;


public class OkCookieUtils {
    private static final String TAG = "OkCookieUtils";
    private static StringBuilder stringBuilder = new StringBuilder();

    /**
     * 保存本地CookieStore
     *
     * @return
     */
    public static void saveLocalCookie(final List<Cookie> cookies) {
        new Thread() {
            @Override
            public void run() {
                String saveStr;
                try {
                    saveStr = "";
                    if (cookies != null) {
                        stringBuilder.delete(0, stringBuilder.length());

                        synchronized (cookies) {
                            for (Cookie cookie : cookies) {
                                String domain = cookie.domain();
                                String name = cookie.name();
                                String path = cookie.path();
                                String value = cookie.value();
                                stringBuilder.append(domain + "##" + name + "##" + path
                                        + "##" + value + "&&");
                                //  前端需要的
                                if ("token".equals(name)) {
                                    SPUtil.putString("token", value);
                                }
                                if ("appToken".equals(name)) {
                                    SPUtil.putString("appToken", value);
                                }
                            }
                        }
                        saveStr = stringBuilder.toString().substring(0,
                                stringBuilder.toString().length() - 2);
                    }
                    if (!TextUtils.isEmpty(saveStr)) {
                        SPUtil.putString("cookie", saveStr);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 获取本地的CookieStore
     *
     * @return
     */
    public static List<Cookie> getLocalCookie() {
        String strCookieStore = SPUtil.getString("cookie", null);
        List<Cookie> cookies = new ArrayList<>();
        if (TextUtils.isEmpty(strCookieStore)) {
            return cookies;
        }
        try {
            String[] strCookies = strCookieStore.split("&&");

            for (String strCookie : strCookies) {
                String[] keys = strCookie.split("##");
                if (keys.length > 3) {
                    Cookie.Builder builder = new Cookie.Builder();
                    Cookie cookie = builder.domain(keys[0]).name(keys[1]).path(keys[2]).value(keys[3]).build();
                    cookies.add(cookie);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookies;

    }

}
