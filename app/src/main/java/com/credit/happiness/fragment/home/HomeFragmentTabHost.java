package com.credit.happiness.fragment.home;

/*
* Copyright (C) 2012 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.credit.happiness.fragment.base.BaseFragment;

import java.util.ArrayList;

/**
 * Special TabHost that allows the use of {@link Fragment} objects for
 * its tab content. When placing this in a view hierarchy, after inflating
 * the hierarchy you must call {@link #setup(Context, FragmentManager, int)}
 * to complete the initialization of the tab host.
 * <p/>
 * <p>Here is a simple example of using a FragmentTabHost in an Activity:
 * <p/>
 * {@sample development/samples/Support4Demos/src/com/example/android/supportv4/app/FragmentTabs.java
 * complete}
 * <p/>
 * <p>This can also be used inside of a fragment through fragment nesting:
 * <p/>
 * {@sample development/samples/Support4Demos/src/com/example/android/supportv4/app/FragmentTabsFragmentSupport.java
 * complete}
 */
public class HomeFragmentTabHost extends DafyTabHost
        implements TabHost.OnTabChangeListener {

    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private FrameLayout mRealTabContent;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mContainerId;
    private OnTabChangeListener mOnTabChangeListener;
    private TabInfo mLastTab;
    private boolean mAttached;

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    static class SavedState extends BaseSavedState {
        String curTab;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            curTab = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(curTab);
        }

        @Override
        public String toString() {
            return "FragmentTabHost.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " curTab=" + curTab + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public HomeFragmentTabHost(Context context) {

        super(context, null);
        initFragmentTabHost(context, null);
    }

    public HomeFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFragmentTabHost(context, attrs);
    }

    private void initFragmentTabHost(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                new int[]{android.R.attr.inflatedId}, 0, 0);
        mContainerId = a.getResourceId(0, 0);
        a.recycle();

        super.setOnTabChangedListener(this);

        if (findViewById(android.R.id.tabs) == null) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            addView(ll, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TabWidget tw = new TabWidget(context);
            tw.setId(android.R.id.tabs);
            tw.setOrientation(TabWidget.HORIZONTAL);
            tw.setDividerDrawable(null);//去掉了底部tab分割线
            ll.addView(tw, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0));

            FrameLayout fl = new FrameLayout(context);
            fl.setId(android.R.id.tabcontent);
            ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

            mRealTabContent = fl = new FrameLayout(context);
            mRealTabContent.setId(mContainerId);
            ll.addView(fl, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

    /**
     * @deprecated Don't call the original TabHost setup, you must instead
     * call {@link #setup(Context, FragmentManager)} or
     * {@link #setup(Context, FragmentManager, int)}.
     */
    @Override
    @Deprecated
    public void setup() {
        throw new IllegalStateException(
                "Must call setup() that takes a Context and FragmentManager");
    }

    public void setup(Context context, FragmentManager manager) {
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        ensureContent();
    }

    public void setup(Context context, FragmentManager manager, int containerId) {
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
        ensureContent();
        mRealTabContent.setId(containerId);

        if (getId() == View.NO_ID) {
            setId(android.R.id.tabhost);
        }
    }

    private void ensureContent() {
        if (mRealTabContent == null) {
            mRealTabContent = (FrameLayout) findViewById(mContainerId);
            if (mRealTabContent == null) {
                throw new IllegalStateException(
                        "No tab content FrameLayout found for id " + mContainerId);
            }
        }
    }

    @Override
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    public void addTab(TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        if (mAttached) {

            info.fragment = mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }
        }

        mTabs.add(info);
        addTab(tabSpec);
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) mFragmentManager.findFragmentByTag(getCurrentTabTag());
    }

    public BaseFragment getFragment(String tag) {
        return (BaseFragment) mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String currentTab = getCurrentTabTag();

        FragmentTransaction ft = null;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
            if (tab.fragment != null && !tab.fragment.isDetached()) {
                if (tab.tag.equals(currentTab)) {
                    mLastTab = tab;
                } else {
                    if (ft == null) {
                        ft = mFragmentManager.beginTransaction();
                    }
                    ft.detach(tab.fragment);
                }
            }
        }

        mAttached = true;
        ft = doTabChanged(currentTab, ft);
        if (ft != null) {
//            ft.commit();
            ft.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttached = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.curTab = getCurrentTabTag();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentTabByTag(ss.curTab);
    }

    @Override
    public void onTabChanged(String tabId) {
        if (mAttached) {
            FragmentTransaction ft = doTabChanged(tabId, null);
            if (ft != null) {
//                ft.commit();
                ft.commitAllowingStateLoss();
            }
        }
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(tabId);
        }
    }

    public interface onFragmentAttachListener {
        void onfragmentAttch(Fragment fragment);
    }

    private onFragmentAttachListener onFragmentAttachListener;

    public void setFragmentAttachListener(onFragmentAttachListener listener) {
        onFragmentAttachListener = listener;
    }

    private FragmentTransaction doTabChanged(String tabId, FragmentTransaction ft) {

        TabInfo newTab = null;
        int tabIndex = 0;
        for (int i = 0; i < mTabs.size(); i++) {
            TabInfo tab = mTabs.get(i);
            if (tab.tag.equals(tabId)) {
                newTab = tab;
                tabIndex = i;
//                LogUtils.d("TAB", "newTab:" + newTab.tag);
            }
        }

        if (newTab == null) {
            throw new IllegalStateException("No tab known for tag " + tabId);
        }
        if (mLastTab != newTab) {
            if (ft == null) {
                ft = mFragmentManager.beginTransaction();
            }
            if (mLastTab != null && newTab.clss != null) {
                if (mLastTab.fragment != null) {
                    ft.hide(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    if (newTab.clss != null) {
                        newTab.fragment = Fragment.instantiate(mContext,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                        if (onFragmentAttachListener != null) {
                            onFragmentAttachListener.onfragmentAttch(newTab.fragment);
                        }
                        Log.d("BaseFragmentTabHost", "tabIndex:" + tabIndex);
                    }
                } else {
                    ft.show(newTab.fragment);
                }
            }
            if (newTab.fragment != null) {
                mLastTab = newTab;
            }
        }
        return ft;
    }
}
