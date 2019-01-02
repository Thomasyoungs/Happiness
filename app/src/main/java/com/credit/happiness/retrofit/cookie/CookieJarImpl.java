package com.credit.happiness.retrofit.cookie;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class CookieJarImpl implements CookieJar {
    private final String TAG = this.getClass().getSimpleName();
    private List<Cookie> cookieList = new ArrayList<>();

    public CookieJarImpl() {
    }


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        try {
            if (cookies != null && cookies.size() > 0) {
                synchronized (CookieJarImpl.class) {
                    List<Cookie> tempcookies = new ArrayList<>();
                    long currenttime = System.currentTimeMillis();
                    for (Cookie item : cookies) {
                        if (item.expiresAt() != 0 && item.expiresAt() < currenttime) {
                            continue;
                        }
                        tempcookies.add(item);
                    }
                    if (tempcookies.size() > 0) {
                        if (tempcookies.size() == 1 && tempcookies.get(0).name().equals("JSESSIONID")) {
                            //不做任何处理
                        } else {
                            if (!tempcookies.toString().equals(cookieList.toString())) {
                                cookieList.clear();
                                cookieList.addAll(tempcookies);
                                OkCookieUtils.saveLocalCookie(cookieList);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
//        Log.i("loadForRequest+++++++url=", "cookie=" + OkCookieUtils.getLocalCookie().toString());
        setLocakCookie();
        return cookieList;
//        return OkCookieUtils.getLocalCookie() == null ? cookieList : OkCookieUtils.getLocalCookie();
    }


    /**
     * 设置本地COOkie
     */
    public void setLocakCookie() {
        List<Cookie> cookies = OkCookieUtils.getLocalCookie();
        if (cookies != null && cookies.size() > 0) {
            cookieList = cookies;
        } else {
            cookieList.clear();
        }
    }

    public void setCookies(List<Cookie> cookies) {
        synchronized (CookieJarImpl.class) {
            if (cookies != null && !cookies.isEmpty()) {
                cookieList.addAll(cookies);
            }
        }
    }

    public String getCookie(String strName) {

        for (Cookie cookie : cookieList) {
            if (cookie.name().equals(strName)) {
                return cookie.value();
            }
        }

        return null;
    }

    public synchronized void setCookie(String strDomain, String strName, String strValue, String strPath, int nLifeTime) {
        Cookie.Builder builder = new Cookie.Builder();
        builder.name(strName);
        builder.value(strValue);
        builder.path(strPath);
        builder.domain(strDomain);
        Cookie cookie = builder.build();
        cookieList.add(cookie);
    }
}
