package com.viewslibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewslibrary.R;

public class NetErrorView extends LinearLayout {
    private Context mContext;
    private LinearLayout mContainer;
    private TextView reload;

    public NetErrorView(Context context) {
        super(context);
        initView(context);
    }

    public NetErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NetErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.net_error_view, null);
        reload = (TextView) mContainer.findViewById(R.id.list_network_error_reload_tv);
        addView(mContainer, lp);
        setGravity(Gravity.CENTER);
    }

    /**
     *
     * @param listener
     */
    public void setRelaodListener(OnClickListener listener) {
        if (listener != null) {
            reload.setOnClickListener(listener);
        }
    }
}
