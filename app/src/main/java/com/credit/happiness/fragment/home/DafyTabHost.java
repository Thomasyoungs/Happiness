package com.credit.happiness.fragment.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

/**
 * 继承TabHost 实现添加当前项点击监听
 *
 * @author yangzhikuan
 */
public class DafyTabHost extends TabHost {
    public DafyTabHost(Context context) {
        super(context);
    }

    public DafyTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DafyTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DafyTabHost(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
    }

    @Override
    public void setCurrentTab(int index) {
        if (index == getCurrentTab()) {
            if (onCurrenttabclick != null) {
                onCurrenttabclick.ontabClick(index);
            }
            return;
        }
        super.setCurrentTab(index);
    }

    public interface OnCurrenttabclick {
        void ontabClick(int index);
    }

    private OnCurrenttabclick onCurrenttabclick;

    public void setOnCurrenttabclickListener(OnCurrenttabclick o) {
        onCurrenttabclick = o;
    }
}
