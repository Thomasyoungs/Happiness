package com.credit.happiness.utils;

public class AppSafeUtils {
    private static AppSafeUtils mAppSafeUtils = null;
   private CodeUtil codeUtil=null;
    public static AppSafeUtils getInstance() {
        if (mAppSafeUtils == null) {
            synchronized (AppSafeUtils.class) {
                if (mAppSafeUtils == null) {
                    mAppSafeUtils = new AppSafeUtils();
                }
            }
        }
        return mAppSafeUtils;
    }

    public AppSafeUtils() {
        codeUtil=new CodeUtil("dafyyundai2.0kehuduan","dafyyundai2.0kehuduan");
    }

    public String encodeString(String str){
       return codeUtil.encode(str);
    }

    public String decodeString(String str){
        String result=str;
        try {
            result=codeUtil.decode(str);
        } catch (Exception e) {
            e.printStackTrace();
            result=str;
        }
        return result;
    }
}
