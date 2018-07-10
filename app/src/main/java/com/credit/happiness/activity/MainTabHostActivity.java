package com.credit.happiness.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.credit.happiness.R;
import com.credit.happiness.app.AppInfo;
import com.credit.happiness.fragment.base.BaseFragment;
import com.credit.happiness.fragment.home.DafyTabHost;
import com.credit.happiness.fragment.home.HomeFragmentTabHost;
import com.credit.happiness.fragment.home.HomeMainFragment;
import com.credit.happiness.fragment.personal.PersonalCenterFragmentV7;
import com.credit.happiness.fragment.scan.ScanFragment;
import com.credit.happiness.utils.ActivityStack;


public class MainTabHostActivity extends BaseFragmentActivity implements
        TabHost.OnTabChangeListener, View.OnClickListener, DafyTabHost.OnCurrenttabclick {

    private static final String TAG = "MainActivity";
    public static final String ARG_ACTIVITY_ARGS = "arg_activity_args";
    public static final String MORE_TAB_INIT_INDEX = "more_tab_init_index";
    public static final String PLAYTYPE = "PlayType";


    private enum TAB {
        NEWS("首页"), SCAN("扫一扫"), PERSON("我的");

        TAB(String cName) {
            this.cName = cName;
        }

        public String cName;

        public String getName() {
            return cName;
        }
    }

    private String sCurrentTab = TAB.SCAN.getName();
    private String lastTab = "";

    private HomeFragmentTabHost mTabHost;

    // Tab 图片View
    private View hotView;
    private View newsView;
    private View personView;

    private ImageView hotImag;
    private ImageView newsImag;
    private ImageView personImag;
    // Tab 文字TextView
    private TextView eventText;
    private TextView hotText;
    private TextView newsText;
    private TextView personText;

    private boolean doubleBackToExitPressedOnce = false;

    private int mDefaultTabIndex = 0;

    public static void openHomeActivity(Context context) {
        openHomeActivity(context, 0, null);
    }

    public static void openHomeActivity(Context context, int defaultIndex, Bundle args) {
        if (context instanceof MainTabHostActivity) {
            ((MainTabHostActivity) context).setCurrentTab(defaultIndex);
            return;
        }
        // 登陆成功, 打开首页
        Intent intent = new Intent(context, MainTabHostActivity.class);
        if (args != null) {
            intent.putExtras(args);
        }
        intent.putExtra(MORE_TAB_INIT_INDEX, defaultIndex);
        if (context instanceof Activity) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ActivityStack.getInstance().finishAllActivityExcept(MainTabHostActivity.class);
        context.startActivity(intent);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    // 4.4 - 5.0版本
    private void setStatusBarUpperAPI19() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View statusBarView = mContentView.getChildAt(0);
        //移除假的 View
        if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                statusBarView.getLayoutParams().height == getStatusBarHeight()) {
            mContentView.removeView(statusBarView);
        }
        //不预留空间
        if (mContentView.getChildAt(0) != null) {
            ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
        }
    }

    // 5.0版本以上
    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        //设置透明状态栏,这样才能让 ContentView 向上
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabhost);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //设置 paddingTop
//        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView().findViewById(R.id.root);
//        rootView.setBackgroundResource(R.color.transparent);
//        rootView.setPadding(0, getStatusBarHeight(), 0, 0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //5.0 以上直接设置状态栏颜色
//            this.getWindow().setStatusBarColor(Color.parseColor("#00000000"));
//        } else {
//            //根布局添加占位状态栏
//            ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView();
//            View statusBarView = new View(this);
//            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    getStatusBarHeight());
//            statusBarView.setBackgroundColor(Color.parseColor("#0000000000"));
//            decorView.addView(statusBarView, lp);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarUpperAPI21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarUpperAPI19();

        }
        mTabHost = (HomeFragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.setOnCurrenttabclickListener(this);
        getArgs();
        onInitView();
        addTabFragment();
    }

    private void getArgs() {
        mDefaultTabIndex = 0;
        sCurrentTab = TAB.SCAN.getName();
        Intent intent = getIntent();
        if (intent != null) {
            mDefaultTabIndex = intent.getIntExtra(MORE_TAB_INIT_INDEX, 0);
//            Bundle args = intent.getBundleExtra(ARG_ACTIVITY_ARGS);
            if (mDefaultTabIndex == 1) {
                sCurrentTab = TAB.SCAN.getName();
            } else if (mDefaultTabIndex == 0) {
                sCurrentTab = TAB.NEWS.getName();
            } else if (mDefaultTabIndex == 2) {
                sCurrentTab = TAB.PERSON.getName();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void onInitView() {
        Intent intent = getIntent();

        hotView = View.inflate(MainTabHostActivity.this, R.layout.activity_main_tab, null);
        hotImag = ((ImageView) hotView.findViewById(R.id.tab_imageview_icon));
        hotImag.setImageResource(R.drawable.icon_launcher_round);
        hotText = (TextView) hotView.findViewById(R.id.tab_textview_title);
        hotText.setText(TAB.SCAN.getName());


        newsView = View.inflate(MainTabHostActivity.this, R.layout.activity_main_tab, null);
        ((ImageView) newsView.findViewById(R.id.tab_imageview_icon))
                .setImageResource(R.drawable.icon_launcher_round);
        newsText = (TextView) newsView.findViewById(R.id.tab_textview_title);
        newsText.setText(TAB.NEWS.getName());

        personView = View.inflate(MainTabHostActivity.this, R.layout.activity_main_tab, null);
        ((ImageView) personView.findViewById(R.id.tab_imageview_icon))
                .setImageResource(R.drawable.icon_launcher_round);
        personText = (TextView) personView.findViewById(R.id.tab_textview_title);
        personText.setText(TAB.PERSON.getName());
    }

    private void addTabFragment() {
        mTabHost.addTab(
                mTabHost.newTabSpec(TAB.NEWS.getName()).setIndicator(newsView), HomeMainFragment.class
                , null);
        mTabHost.addTab(
                mTabHost.newTabSpec(TAB.SCAN.getName()).setIndicator(hotView), ScanFragment.class
                , null);
        mTabHost.addTab(mTabHost.newTabSpec(TAB.PERSON.getName()).setIndicator(personView),
                PersonalCenterFragmentV7.class, null);


        mTabHost.getTabWidget().setVisibility(View.VISIBLE);
        mTabHost.setOnTabChangedListener(this);
        setCurrentTab(mDefaultTabIndex);
    }

    public void setCurrentTab(int index) {
        mTabHost.setCurrentTab(index);
    }

    public BaseFragment getCurrentFragment() {
        return mTabHost.getCurrentFragment();
    }

    /**
     * TabHost.OnTabChangeListener
     */

    @Override
    public void onTabChanged(String tabTag) {

        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        Fragment fgm = mTabHost.getFragment(tabTag);
        if (fgm != null) {

        }
        Log.i("onTabChanged", sCurrentTab + "    " + tabTag);
        sCurrentTab = tabTag;
    }

    @Override
    public void ontabClick(int index) {
        Fragment fgm = mTabHost.getCurrentFragment();
        if (fgm != null) {

        }
/*        if (index == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("scanBack", "scanBack");
            TerminalActivity.showFragmentForResult(this, ScanFragment.class, bundle, CODE);
        }*/
        Log.i("ontabClick", "current index = " + index);
    }

    public void startAnimation(View view) {
        if (view == null) {
            return;
        }
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);//设置动画持续时间
        view.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        if (AppInfo.isAppInBackground() && mTabHost != null) {
            Fragment fgm = mTabHost.getCurrentFragment();
            if (fgm != null) {
            }
        }
        super.onStart();


    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce
                || this.getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainTabHostActivity.this, getString(R.string.double_click_quit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }

        }, 2000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

    /*    if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                getCurrentFragment().onKeyUp();
                return true;
            }
        }*/

        return super.onKeyUp(keyCode, event);
    }

    /* View.OnClickListener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getCurrentFragment() != null)
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        if ("首页".equals(lastTab)) {
            setCurrentTab(0);
        } else if ("我的".equals(lastTab)) {
            setCurrentTab(2);
        }
    }

    private final int CODE = 1000;

}
