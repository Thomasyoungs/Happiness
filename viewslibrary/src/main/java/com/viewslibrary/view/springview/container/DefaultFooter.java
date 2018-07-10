package com.viewslibrary.view.springview.container;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viewslibrary.R;

/**
 * Created by Administrator on 2016/3/21.
 */
public class DefaultFooter extends BaseFooter {
    private Context context;
    private int rotationSrc;
    private TextView footerTitle;
    private ProgressBar footerProgressbar;

    public DefaultFooter(Context context) {
        this(context, R.drawable.load_frame);
    }

    public DefaultFooter(Context context, int rotationSrc) {
        this.context = context;
        this.rotationSrc = rotationSrc;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.default_footer, viewGroup, true);
        footerTitle = (TextView) view.findViewById(R.id.default_footer_title);
        footerProgressbar = (ProgressBar) view.findViewById(R.id.default_footer_progressbar);
        footerProgressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        footerTitle.setTextColor(Color.parseColor("#777777"));
        if (upORdown) {
            footerTitle.setText("松开载入更多");
        } else {
            footerTitle.setText("查看更多");
        }
    }

    @Override
    public void onStartAnim() {
        footerTitle.setVisibility(View.INVISIBLE);
        footerProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishAnim() {
        footerTitle.setText("查看更多");
        footerTitle.setTextColor(Color.parseColor("#777777"));
        footerTitle.setVisibility(View.VISIBLE);
        footerProgressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadNone() {
        footerProgressbar.setVisibility(View.GONE);
        footerTitle.setVisibility(View.VISIBLE);
        footerTitle.setText("已经到底喽～");
        footerTitle.setTextColor(Color.parseColor("#D1D1D1"));
    }
}