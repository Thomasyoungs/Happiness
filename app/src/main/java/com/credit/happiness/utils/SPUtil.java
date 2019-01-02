package com.credit.happiness.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.credit.happiness.app.HappinessApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

public class SPUtil {
    private static final String TAG = "SPUtil";
    public static final String HOSTNAME_KEY = "HostName";//新版文件下载域名
    public static final String H5_VERSION_KEY = "h5version";//h5文件版本
    public static final String H5_DEFAULT_VERSION = "1.1.1";//h5文件默认版本
    public static final String H5_BASEPATH = "basepath";//h5版本路径
    public static final String APP_VERSION_NAME = "appversion_name";//app版本
    public static final String APP_VERSION_CODE = "appversion_code";//app版本号


    public static final String ZIP_VERSION_INFO = "zipversioninfos";//新zip版本号


    public static final String DAFY_DATA = "dafy_data";
    private static SharedPreferences shared;
    private static Editor editor;


    /**
     * 获取SharedPreferences实例对象
     *
     * @return
     */
    private static SharedPreferences getShared() {
        isNull();
        return shared;
    }

    /**
     * 是空的就创建
     */
    private static void isNull() {
        if (shared == null) {
            shared = HappinessApplication.mContext.getSharedPreferences(DAFY_DATA, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getShared(Context context) {
        isNull(context);
        return shared;
    }

    private static void isNull(Context context) {
        if (shared == null) {
            shared = context.getSharedPreferences(DAFY_DATA, Context.MODE_PRIVATE);
        }
    }


    /**
     * 获得Editor实例
     *
     * @return
     */
    @SuppressLint("CommitPrefEdits")
    public static Editor getEdit() {
        if (editor == null) {
            editor = getShared().edit();
        }

        return editor;
    }

    /**
     * 往shared中添加String类型值
     *
     * @param key
     * @param stringValue
     * @return
     */
    public static synchronized boolean putString(String key, String stringValue) {
        isNull();
//        key = AppSafeUtils.getInstance().encodeString(key);
//        stringValue = AppSafeUtils.getInstance().encodeString(stringValue);
        return getEdit().putString(key, stringValue).commit();
    }

    /**
     * 往shared中添加int类型值
     *
     * @param key
     * @param intValue
     * @return
     */
    public static synchronized boolean putInt(String key, int intValue) {
        isNull();
        return getEdit().putInt(key, intValue).commit();
    }

    /**
     * 往shared中添加long类型值
     *
     * @param key
     * @param longValue
     * @return
     */
    public static synchronized boolean putLong(String key, long longValue) {
        isNull();
        return getEdit().putLong(key, longValue).commit();
    }

    /**
     * 往shared中添加boolean类型值
     *
     * @param key
     * @param booleanValue
     * @return
     */
    public static synchronized boolean putBoolean(String key, boolean booleanValue) {
        isNull();
        return getEdit().putBoolean(key, booleanValue).commit();
    }

    /**
     * 往shared中添加float类型值
     *
     * @param key
     * @param floatValue
     * @return
     */
    public static synchronized boolean putFloat(String key, float floatValue) {
        isNull();
        return getEdit().putFloat(key, floatValue).commit();
    }

    /**
     * 往shared中添加Set<String>类型值 (需要API11)
     *
     * @param key
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean putStringSet(String key, Set<String> stringSetValue) {
        isNull();
        key = AppSafeUtils.getInstance().encodeString(key);
        return getEdit().putStringSet(key, stringSetValue).commit();
    }

    /**
     * 删除指定持续化储存的数据
     *
     * @param keys 键值数组
     */
    @SuppressLint("NewApi")
    public static boolean clearArray(String[] keys) {
        isNull();
        for (String key : keys) {
            String enkey = AppSafeUtils.getInstance().encodeString(key);
            if (shared.contains(enkey)) {
                getEdit().remove(enkey).commit();
            } else {
                if (shared.contains(key)) {
                    getEdit().remove(key).commit();
                }
            }
        }
        return true;
    }

    /**
     * 删除所有持续化数据
     *
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean clearAll() {
        isNull();
        return getEdit().clear().commit();
    }

    public static String getString(String key, String defValue) {
        isNull();
        String value = null;
        if (!TextUtils.isEmpty(key)) {
//            String tempkey = AppSafeUtils.getInstance().encodeString(key);
            value = shared.getString(key, defValue);
//            if (TextUtils.isEmpty(value)) {
//                value = shared.getString(key, defValue);
//            } else {
//                value = AppSafeUtils.getInstance().decodeString(value);
//            }
        }
        return value;
    }

    public static Float getFloat(String key, float defValue) {
        isNull();
        return shared.getFloat(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        isNull();
        return shared.getInt(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        isNull();
        return shared.getBoolean(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        isNull();
        return shared.getLong(key, defValue);
    }


    /**
     * 保存序列化对象到本地
     *
     * @param key
     * @param object
     */
    public static void saveSerializableObject(String key, Object object) {

        //先将序列化结果写到byte缓存中，其实就分配一个内存空间
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(object);//将对象序列化写入byte缓存
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            if (editor == null) {
                editor = shared.edit();
            }
            editor.putString(key, bytesToHexString);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * 从本地反序列化获取对象
     *
     * @param key
     * @return
     */
    public static Object getSerializableObject(String key) {
        if (shared.contains(key)) {
            String string = shared.getString(key, "");
            if (TextUtils.isEmpty(string)) {
                return null;
            } else {
                //将16进制的数据转为数组，准备反序列化
                byte[] stringToBytes = StringToBytes(string);
                ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                ObjectInputStream is = null;
                try {
                    is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); //两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); // 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }
}
