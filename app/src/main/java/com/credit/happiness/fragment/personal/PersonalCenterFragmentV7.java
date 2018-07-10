package com.credit.happiness.fragment.personal;

import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.credit.happiness.R;
import com.credit.happiness.fragment.base.BaseFragment;
import com.nineoldandroids.view.ViewHelper;
import com.viewslibrary.view.ob.ObservableScrollView;
import com.viewslibrary.view.ob.ObservableScrollViewCallbacks;
import com.viewslibrary.view.ob.ScrollState;
import com.viewslibrary.view.ob.StretchScrollView;


/**
 *
 */

public class PersonalCenterFragmentV7 extends BaseFragment implements ObservableScrollViewCallbacks {

    private View mFlexibleSpaceView;
    private RelativeLayout mToolbarView;
    private RelativeLayout mTitleView;
    private int mFlexibleSpaceHeight;
    private int flexibleSpaceAndToolbarHeight;
    private float offsetY;
    private boolean isFlexibleStop = true;  //缩放动画停止
    private boolean isFlexibleStop2 = true;  //缩放动画停止
    private int maxTitleTranslateY;


    @Override
    protected int onSetContainerViewId() {
        return R.layout.fragment_home_personalcnenter_layoutv7;
    }

    @Override
    protected void onInitView(View containerView) {


        mToolbarView = containerView.findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(getColorWithAlpha(0, getResources().getColor(R.color.orange)));
        mTitleView = containerView.findViewById(R.id.flexible_layout);
        getActivity().setTitle(null);
        mFlexibleSpaceView = containerView.findViewById(R.id.flexible_space);
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + getActionBarSize();
        containerView.findViewById(R.id.body).setPadding(0, flexibleSpaceAndToolbarHeight, 0, 0);
        mFlexibleSpaceView.getLayoutParams().height = flexibleSpaceAndToolbarHeight;
        final ObservableScrollView scrollView = containerView.findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);
        scrollView.setScrollViewListener(new StretchScrollView.ScrollViewListener() {
            @Override
            public void onScroll(int y) {
                offsetY += y;
                Log.i("onScroll", "y = " + y + "offsetY  = " + -offsetY);
                if (y < 0 && isFlexibleStop) {//开始拉伸
//                    ViewHelper.setTranslationY(mFlexibleSpaceView, -offsetY);
                    ViewHelper.setTranslationY(mTitleView, maxTitleTranslateY - offsetY);//title平移
                    float scale = (float) ((-offsetY / 0.8) / flexibleSpaceAndToolbarHeight);
                    ViewHelper.setPivotY(mFlexibleSpaceView, 0);
                    ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);
                }

            }

            @Override
            public void onScrollUp(int y) {
                Log.i("onScrollUp", "onScrollUp = " + y);
                if (y < 0 && isFlexibleStop) {
//                    ViewHelper.setTranslationY(mFlexibleSpaceView, -y);
                    ViewHelper.setTranslationY(mTitleView, maxTitleTranslateY - y);
                    float scale = ((float) (-y)) / flexibleSpaceAndToolbarHeight;
                    ViewHelper.setPivotY(mFlexibleSpaceView, 0);
                    ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);
                }
                offsetY = 0;
                if (y == 0) {//缩放动画停止
                    isFlexibleStop2 = true;
                } else {
                    isFlexibleStop2 = false;
                }
            }
        });
        ViewTreeObserver vto = mFlexibleSpaceView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mFlexibleSpaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mFlexibleSpaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                new Runnable() {
                    @Override
                    public void run() {
                        Log.i("scrollView", "scrollView =  " + scrollView.getCurrentScrollY());
                        updateFlexibleSpaceText(scrollView.getCurrentScrollY());
                    }
                }.run();
            }
        });
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void updateFlexibleSpaceText(final int scrollY) {
        if (!isFlexibleStop2) {
            return;
        }
        ViewHelper.setTranslationY(mFlexibleSpaceView, -scrollY);

        int adjustedScrollY = Math.min(mFlexibleSpaceHeight, Math.max(0, scrollY));
        float maxScale = (float) (mFlexibleSpaceHeight - mToolbarView.getHeight()) / mToolbarView.getHeight();
        float scale = maxScale * ((float) mFlexibleSpaceHeight - adjustedScrollY) / mFlexibleSpaceHeight;

        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, 1 + scale);
        ViewHelper.setScaleY(mTitleView, 1 + scale);
        int maxTitleTranslationY = mToolbarView.getHeight() + mFlexibleSpaceHeight - (int) (mTitleView.getHeight() * (1 + scale));
        int titleTranslationY = (int) (maxTitleTranslationY * ((float) mFlexibleSpaceHeight - adjustedScrollY) / mFlexibleSpaceHeight);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        Log.i("titleTranslationY", "titleTranslationY  =  " + titleTranslationY);


        int baseColor = getResources().getColor(R.color.orange);
        float alpha = Math.min(1, (float) scrollY / mFlexibleSpaceHeight);
        mToolbarView.setBackgroundColor(getColorWithAlpha(alpha, baseColor));

        if (scrollY == 0) {//缩放动画停止
            isFlexibleStop = true;
            maxTitleTranslateY = titleTranslationY;
        } else {
            isFlexibleStop = false;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.i("onScroll ", "scrolly = " + scrollY + " first = " + firstScroll + " dragging = " + dragging);
        updateFlexibleSpaceText(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
