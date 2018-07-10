package com.credit.happiness.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.credit.happiness.app.AppInfo;
import com.credit.happiness.app.HappinessApplication;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * 公共方法承载类
 *
 * @author zhikuan
 */
public class Methods {


    private final static long updateDuration = 302400000; //同步需要的间隔时间（一周)
    public final static long MILLISECOND_OF_DAY = 86400000;
    public static final String REGEX_HTML = "<[.[^<]]*>";  // <[^>]+>


    /**
     * 当前网络是否可用
     *
     * @param
     * @return
     */
    public static boolean isNetworkAvaiable() {
        ConnectivityManager manager = (ConnectivityManager) AppInfo.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            if (!fitApiLevel(11)) {
                // 在一款2.3的手机上，明明wifi连接着，但getActiveNetworkInfo()返回空, 所以api小于11就不做判断
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return if api level >= level
     */
    @SuppressWarnings("deprecation")
    public static boolean fitApiLevel(int level) {
        try {
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sdkVersion >= level) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 当前fragment是否有效
     *
     * @param fragment
     */
    public static boolean isFragmentAviable(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        Activity hostActivity = fragment.getActivity();
        if (hostActivity == null || hostActivity.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 添加fragment顶部点击事件监听
     */
    public static boolean addFragmentTopClickListener(Fragment fragment, View.OnClickListener clickListener) {
        if (fragment == null || !isFragmentAviable(fragment)) {
            return false;
        }
        FragmentActivity activity = fragment.getActivity();
        if (activity == null || !(activity instanceof ActionBarActivity)) {
            return false;
        }
        ActionBar actionBar = ((ActionBarActivity) activity).getSupportActionBar();
        if (actionBar == null) {
            return false;
        }
        View customTopView = new View(activity);
        /*ImageView customTopView = new ImageView(activity);
        customTopView.setImageResource(R.drawable.feed_ab_icon);
		customTopView.setScaleType(ScaleType.FIT_START);
		int padding = (int) activity.getResources().getDimension(R.dimen.feed_action_padding);
		customTopView.setPadding(padding, padding, padding, padding);*/
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.LEFT;
        actionBar.setCustomView(customTopView, layoutParams);
        actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
        customTopView.setOnClickListener(clickListener);
        return true;
    }

    /**
     * 动画滑动到顶部
     *
     * @param listview
     */
    public static void smoothScrollToTop(final ListView listview) {
        if (listview != null) {
            //mListView.setSelection(0);
            // 防止条目太多, 导致无法回到顶部
            if (listview.getLastVisiblePosition() > 5) {
                listview.setSelection(5);
            }
            listview.post(new Runnable() {
                @Override
                public void run() {
                    listview.smoothScrollToPosition(0);
                    //mListView.requestLayout();
                }
            });
        }
    }


    public static void disableOverScrollMode(ListView list) {
        list.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    /*
    * 判断当前应用是不是在前台
    */
    public static boolean isAppOnForceground(Context context) {
        String pkgName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // Returns a list of application processes that are running on the device
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            // importance:
            // The relative importance level that the system places
            // on this process.
            // May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
            // IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
            // These constants are numbered so that "more important" values are
            // always smaller than "less important" values.
            // processName:
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(pkgName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param count
     * @param decimal fraction digit count
     * @return
     * @author jason
     */
    public static String getFormatNumber(int count, int decimal) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(decimal);
        if (count == 0)
            return "";
        if (count < 1000)
            return String.valueOf(count);
        if (count >= 1000 && count < 10000) {
            String str = format.format(count / 1000f) + "k";
            if (str.endsWith(".0k")) {
                str = str.replace(".0", "");
            }
            return str;
        }
        if (count >= 10000 && count < 1000000) {
            String str = format.format(count / 10000f) + "w";
            if (str.endsWith(".0w")) {
                str = str.replace(".0", "");
            }
            return str;
        }
        if (count >= 1000000 && count < 10000000) {
            String str = format.format(count / 1000000f) + "bw";
            if (str.endsWith(".0bw")) {
                str = str.replace(".0", "");
            }
            return str;
        }
        if (count >= 10000000) {
            return "kw+";
        }
        return String.valueOf(count);
    }


    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String mac = "000000";
        WifiManager wifi = (WifiManager) HappinessApplication.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
            }
        }
        return mac;
    }

    /**
     * 从crash log名称中解析出uid
     *
     * @param logName
     * @return
     */
    public static String getUidFromCrashlogName(String logName) {
        if (!TextUtils.isEmpty(logName)) {
            String[] nameSplits = logName.split("_");
            if (nameSplits.length >= 4) {
                return nameSplits[1];
            }
        }
        return "0";
    }

    /**
     * 从crash log名称中解析出时间
     *
     * @param logName
     * @return
     */
    public static String getTimeFromCrashlogName(String logName) {
        if (!TextUtils.isEmpty(logName)) {
            String[] nameSplits = logName.split("_");
            if (nameSplits.length >= 4) {
                return nameSplits[2];
            }
        }
        return "";
    }


    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftInputMethods(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showSoftInputMethods(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * 显示Toast
     *
     * @param text
     */
    public static void showToast(final CharSequence text) {
        showToast(text, false, true);
    }

    /**
     * @param text
     * @param lengthLong 是否较长时间显示
     */
    public static void showToast(final CharSequence text, final boolean lengthLong) {
        if (text != null) {
            showToast(text, lengthLong, true);
        }
    }

    public static void showToast(final CharSequence text, final boolean lengthLong, boolean show) {
        if (!show) {
            return;
        }
        Runnable update = new Runnable() {
            public void run() {
                AppInfo.getGlobalToast().setText(text);
                AppInfo.getGlobalToast().setDuration(lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                AppInfo.getGlobalToast().show();
            }
        };
        AppInfo.getUIHandler().post(update);
    }

    /**
     * 直接应用资源Id显示Toast
     *
     * @param resId
     */
    public static void showToast(final int resId) {
        showToast(resId, false, true);
    }

    public static void showToast(int resId, boolean lengthLong) {
        String text = AppInfo.getAppContext().getString(resId);
        if (TextUtils.isEmpty(text))
            showToast(text, lengthLong);
    }

    /**
     * @param resId
     * @param lengthLong
     * @param show
     */

    public static void showToast(final int resId, final boolean lengthLong, boolean show) {
        if (show == false) {
            return;
        }
        Runnable update = new Runnable() {
            public void run() {
                AppInfo.getGlobalToast().setText(resId);
                AppInfo.getGlobalToast().setDuration(lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                AppInfo.getGlobalToast().show();
            }
        };
        AppInfo.getUIHandler().post(update);
    }


    public static int computePixelsWithDensity(int dp) {

        DisplayMetrics displayMetrics = HappinessApplication.getApplication().getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density + 0.5);
    }

    public static float computePixels(int dp) {

        DisplayMetrics displayMetrics = HappinessApplication.getApplication().getResources().getDisplayMetrics();
        return (float) (dp * displayMetrics.density + 0.5);
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
        if (text != null && text.length() != 0 && searchList != null && searchList.length != 0 && replacementList != null && replacementList.length != 0) {
            if (timeToLive < 0) {
                throw new IllegalStateException("TimeToLive of " + timeToLive + " is less than 0: " + text);
            } else {
                int searchLength = searchList.length;
                int replacementLength = replacementList.length;
                if (searchLength != replacementLength) {
                    throw new IllegalArgumentException("Search and Replace array lengths don\'t match: " + searchLength + " vs " + replacementLength);
                } else {
                    boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
                    int textIndex = -1;
                    int replaceIndex = -1;
                    boolean tempIndex = true;

                    int start;
                    int var16;
                    for (start = 0; start < searchLength; ++start) {
                        if (!noMoreMatchesForReplIndex[start] && searchList[start] != null && searchList[start].length() != 0 && replacementList[start] != null) {
                            var16 = text.indexOf(searchList[start]);
                            if (var16 == -1) {
                                noMoreMatchesForReplIndex[start] = true;
                            } else if (textIndex == -1 || var16 < textIndex) {
                                textIndex = var16;
                                replaceIndex = start;
                            }
                        }
                    }

                    if (textIndex == -1) {
                        return text;
                    } else {
                        start = 0;
                        int increase = 0;

                        int textLength;
                        for (int buf = 0; buf < searchList.length; ++buf) {
                            if (searchList[buf] != null && replacementList[buf] != null) {
                                textLength = replacementList[buf].length() - searchList[buf].length();
                                if (textLength > 0) {
                                    increase += 3 * textLength;
                                }
                            }
                        }

                        increase = Math.min(increase, text.length() / 5);
                        StringBuilder var17 = new StringBuilder(text.length() + increase);

                        while (textIndex != -1) {
                            for (textLength = start; textLength < textIndex; ++textLength) {
                                var17.append(text.charAt(textLength));
                            }

                            var17.append(replacementList[replaceIndex]);
                            start = textIndex + searchList[replaceIndex].length();
                            textIndex = -1;
                            replaceIndex = -1;
                            tempIndex = true;

                            for (textLength = 0; textLength < searchLength; ++textLength) {
                                if (!noMoreMatchesForReplIndex[textLength] && searchList[textLength] != null && searchList[textLength].length() != 0 && replacementList[textLength] != null) {
                                    var16 = text.indexOf(searchList[textLength], start);
                                    if (var16 == -1) {
                                        noMoreMatchesForReplIndex[textLength] = true;
                                    } else if (textIndex == -1 || var16 < textIndex) {
                                        textIndex = var16;
                                        replaceIndex = textLength;
                                    }
                                }
                            }
                        }

                        textLength = text.length();

                        for (int result = start; result < textLength; ++result) {
                            var17.append(text.charAt(result));
                        }

                        String var18 = var17.toString();
                        if (!repeat) {
                            return var18;
                        } else {
                            return replaceEach(var18, searchList, replacementList, repeat, timeToLive - 1);
                        }
                    }
                }
            }
        } else {
            return text;
        }
    }


    /**
     * 货币数量千分位分隔符添加
     *
     * @param count
     * @return
     */
    public static String currencyFormat(double count) {
        if (count >= 0 && count < 0.01)
            return "0.00";
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");
        return decimalFormat.format(count);

        /*String countStr = Double.toString(count);

        String[] doubleSplit = countStr.split("\\.");
        StringBuffer bs = new StringBuffer(doubleSplit[0]);
        bs = bs.reverse();
        int integerPartLenght = bs.length();
        int separatorCount = integerPartLenght / 3;

        if (integerPartLenght % 3 == 0) {
            separatorCount--;
        }

        for (int i = 0; i < separatorCount; i++) {
            bs.insert((i + 1) * 3, ',');
        }

        bs = bs.reverse();

        return String.format("%s.%s", bs.toString(), doubleSplit[1]);*/
    }

    public static String fundNetFormat(double count) {
        if (count >= 0 && count < 0.01)
            return "0.0000";
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        return decimalFormat.format(count);
    }

    /*
     * double数字转换成0.00的形式
     */
    public static String doubleFormat(double count) {
        if (count >= 0 && count < 0.01)
            return "0.00";
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(count);
    }

    /**
     * double转换成百分数形式
     *
     * @param count
     * @return
     */
    public static String convertToPercentage(double count) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);

        return format.format(count);
    }

    public static double getBiggerDouble(double num1, double num2) {
        BigDecimal decimal1 = new BigDecimal(num1);
        BigDecimal decimal2 = new BigDecimal(num2);
        int result = decimal1.compareTo(decimal2);

        if (result == -1) {
            return num2;
        } else {
            return num1;
        }
    }

    public static double getSmallerDouble(double num1, double num2) {
        BigDecimal decimal1 = new BigDecimal(num1);
        BigDecimal decimal2 = new BigDecimal(num2);
        int result = decimal1.compareTo(decimal2);

        if (result == -1) {
            return num1;
        } else {
            return num2;
        }
    }


    /**
     * 将长时间格式字符串转换为字符串 yyyy-MM-dd HH:mm
     *
     * @return
     */
    public static String timeLong2String(long data) {
        Date date = new Date(data);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);
    }

    /**
     * 将长时间格式字符串转换为字符串 yyyy-MM-dd
     *
     * @return
     */
    public static String timeLong2String2(long data) {
        Date date = new Date(data);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 将长时间格式字符串转换为字符串 MM月dd日
     *
     * @return
     */
    public static String timeLong2String3(long data) {
        Date date = new Date(data);
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        return formatter.format(date);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * @param sdate
     * @return
     */
    public static String getWeekStr(String sdate) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        if (strToDate(sdate) == null) {
            return "";
        }
        calendar.setTime(strToDate(sdate));
        str = calendar.get(Calendar.DAY_OF_WEEK) + "";
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        if (isTheSameDay(sdate))
            str += " (今天)";
        return str;
    }

    /**
     * 分组时间格式化工具
     *
     * @param s
     * @return
     */
    public static String getFormatterDate(String s) {
        String ss = s;
        if (TextUtils.isEmpty(s))
            return s;// 返回元字符
        try {
            // 先格式化
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(s); // 字符串转时间
            // HH:mm:ss
            long millis = d.getTime();// 获得毫秒数
            Calendar cal = Calendar.getInstance();
            // 注：在使用set方法之前，必须先clear一下，否则很多信息会继承自系统当前时间
            cal.clear();
            cal.setTimeInMillis(millis);
            Formatter ft = new Formatter(Locale.CHINA); // 通过中国时区获取时间
            ss = ft.format("%1$tm-%1$td %1$tA", cal).toString();// 格式化为
            if (isTheSameDay(s))
                ss += " (今天)";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ss;
    }

    public static Date stringToDate(String strTime, String formatType)

            throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat(formatType);

        Date date = null;

        date = formatter.parse(strTime);

        return date;

    }

    public static long stringToLong(String strTime, String formatType)

            throws ParseException {

        Date date = stringToDate(strTime, formatType); // String类型转成date类型

        if (date == null) {

            return 0;

        } else {

            long currentTime = dateToLong(date); // date类型转成long类型

            return currentTime;

        }

    }


    // date类型转换为long类型

    // date要转换的date类型的时间

    public static long dateToLong(Date date) {

        return date.getTime();

    }

    /**
     * @param
     * @param
     * @return
     */
    public static boolean isTheSameDay(String data) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            Date mDate1 = new SimpleDateFormat("yyyy-MM-dd")
                    .parse(data);
            Date mDate2 = new Date();
            c1.setTime(mDate1);
            c2.setTime(mDate2);
            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                    && (c1.get(Calendar.DAY_OF_MONTH) == c2
                    .get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }


    public static double parseDouble(String strNum) {
        double num = 0.0f;
        if (TextUtils.isEmpty(strNum)) {
            return num;
        }
        try {
            num = Double.parseDouble(strNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 获取距当前时刻的时间戳
     *
     * @param createTime
     * @return
     */
    public static String getTimestamp(long createTime) {
        long currrentMillis = System.currentTimeMillis();
//        if (createTime < 0 || currrentMillis < createTime) {
//            return null;
//        }
        StringBuffer sb = new StringBuffer();
        Date date = new Date(createTime);
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf_hms = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf_full = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long standardMillis = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));
        long diffMillis = currrentMillis - createTime;
        if (diffMillis < 60 * 1000) {
            sb.append("刚刚");
        } else if (diffMillis < 3600 * 1000) {
            sb.append(diffMillis / 60000 + "分钟前");
        } else if (diffMillis < standardMillis) {
            sb.append("今天 " + sdf_hms.format(date));
        } else if (diffMillis < standardMillis + MILLISECOND_OF_DAY) {
            sb.append("昨天 " + sdf_hms.format(date));
        } else {
            sb.append(sdf_full.format(date));
        }

        return sb.toString();
    }

    /**
     * 获取html中的纯文本
     *
     * @param html
     * @return
     */
    public static String filterHtmlTag(String html) {
        return html.replaceAll(REGEX_HTML, "");
    }

    /**
     * 字符串添加html标签，实现关键字高亮显示
     *
     * @param key
     * @return
     */
    public static String setHighLightTag(String content, String key) {
        key = key.toUpperCase();
        for (int i = 0; i < key.length(); i++) {
            String newRegions = "<font color='red'>" + String.valueOf(key.charAt(i)) + "<\\/font>";
            content = content.replaceAll(String.valueOf(key.charAt(i)), newRegions);
        }
        return content;
    }

    /**
     * 一个字符串是否包含全部关键字的内容
     *
     * @param content
     * @param query
     * @return
     */
    public static boolean queryScorer(String content, String query) {
        int score = 0;
        query = query.toLowerCase();
        char firstCharacter = query.charAt(0);
        if (firstCharacter >= 'a' && firstCharacter <= 'z') {
            if (content.startsWith(query)) {
                return true;
            } else {
                return false;
            }
        }
        query = query.toUpperCase();
        for (int i = 0; i < query.length(); i++) {
            if (content.contains(String.valueOf(query.charAt(i)))) {
                score++;
            }
        }
        return score == query.length();
    }


    /**
     * 截取字符串前十位 汉字算两位
     * Java判断一个字符串是否有中文一般情况是利用Unicode编码(CJK统一汉字的编码区间：0x4e00–0x9fbb)的正则来做判断，
     * 但是其实这个区间来判断中文不是非常精确，因为有些中文的标点符号比如：，。等等是不能识别的。
     *
     * @param s
     * @return
     */
    public static String getString(String s) {
        if (s == null) {
            return "";
        }
        int count = 0;
        String subString = "";
        for (int i = 0; i < s.length(); i++) {
            String ss = String.valueOf(s.charAt(i));
            subString += ss;
            if (ss.matches("[\u4e00-\u9fa5]+")) {
                count += 2;
            } else {
                count++;
            }
            if (count >= 10) {
                return subString;
            }
        }

        return subString;
    }

    /**
     * 计算包含汉字的字符串长度
     *
     * @param s
     * @return
     */
    public static int getStringCount(String s) {
        if (s == null) {
            return 0;
        }
        int count = 0;
        String subString = "";
        for (int i = 0; i < s.length(); i++) {
            String ss = String.valueOf(s.charAt(i));
            subString += ss;
            if (ss.matches("[\u4e00-\u9fa5]+")) {
                count += 2;
            } else {
                count++;
            }

        }

        return count;
    }


}
