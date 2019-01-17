package com.credit.happiness.fragment.personal;

import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.credit.happiness.R;
import com.credit.happiness.app.AppInfo;
import com.credit.happiness.app.HappinessApplication;
import com.credit.happiness.fragment.base.BaseFragment;
import com.credit.happiness.utils.SPUtil;
import com.nineoldandroids.view.ViewHelper;
import com.viewslibrary.view.stretch.ObScrollViewCallbacks;
import com.viewslibrary.view.stretch.ObStretchScrollView;
import com.viewslibrary.view.stretch.ScrollState;
import com.viewslibrary.view.stretch.StretchScrollView;


/**
 *
 */

public class PersonalCenterFragment extends BaseFragment implements ObScrollViewCallbacks {

    private View mFlexibleSpaceView;
    private ObStretchScrollView scrollView;
    private RelativeLayout mToolbarView;
    private RelativeLayout mTitleView;
    private int mFlexibleSpaceHeight;
    private float flexibleSpaceAndToolbarHeight;
    private float criticalY;   //标题栏放大到最大点Y的平移位置
    private float criticalD;   //标题栏放大到最大时和背景下边界的距离
    private ProgressBar progressbar;
    private View emptyStatusBar;

    /******* 刷新逻辑********/
    //    private TextView hitText;
    private boolean isRefreshing = false;

    private int minHeighetY;
    private int maxHeighetY;

    @Override
    protected int onSetContainerViewId() {
        return R.layout.fragment_home_personalcnenter_layout;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    @Override
    protected void onInitView(View containerView) {

        emptyStatusBar = containerView.findViewById(R.id.empty_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) emptyStatusBar.getLayoutParams();
            p.height = getStatusBarHeight();
            emptyStatusBar.setLayoutParams(p);
            containerView.findViewById(R.id.empty_view).setLayoutParams(p);
        }
        initStretchVIew(containerView);

    }

    private void initStretchVIew(View containerView) {
        progressbar = containerView.findViewById(R.id.default_header_progressbar);
//        hitText = containerView.findViewById(R.id.default_header_hint_textview);
        progressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.load_frame));


        mToolbarView = containerView.findViewById(R.id.toolbar);
        mTitleView = containerView.findViewById(R.id.flexible_title_layout);
        mToolbarView.setBackgroundColor(getColorWithAlpha(0, getResources().getColor(R.color.orange)));
        mFlexibleSpaceView = containerView.findViewById(R.id.flexible_space);
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);

        flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + getActionBarSize();
//        containerView.findViewById(R.id.body).setPadding(0, (int) flexibleSpaceAndToolbarHeight, 0, 0);
        containerView.findViewById(R.id.body).setPadding(0, (int) getActionBarSize(), 0, 0);
        mFlexibleSpaceView.getLayoutParams().height = (int) flexibleSpaceAndToolbarHeight;
        scrollView = containerView.findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);
        scrollView.setScrollViewListener(new StretchScrollView.ScrollViewListener() {
            @Override
            public void onScrollMove(int delatY) {
                Log.i("onScrollMove", "delatY = " + delatY);
                moveDragFlexibleSpaceAndText(delatY);

            }

            @Override
            public void onScrollUp() {
                ValueAnimator valueAnimator = null;
                if (isRefreshing) {//正在刷新
                    valueAnimator = ValueAnimator.ofInt(scrollView.getChildRootViewY(), -maxHeighetY + refreshHeight - 5);
                } else {
                    valueAnimator = ValueAnimator.ofInt(scrollView.getChildRootViewY(), 0);
                }
                valueAnimator.setDuration(scrollView.SCROLL_BACK_TIME);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        scrollView.chileRootViewScroll(value);
                        if (value <= 0) {
                            upDragFlexibleSpaceAndText(value);
                        }
                    }
                });
                valueAnimator.start();
                if (isRefreshing) {
                    onRefresh();
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
                        updateFlexibleSpaceText(scrollView.getCurrentScrollY());
                    }
                }.run();
            }
        });

    }

    public void setStatusBarColor(int color) {
        emptyStatusBar.setBackgroundColor(color);
 /*       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getActivity().getWindow().setStatusBarColor(color);
        } else {
            ViewGroup decorView = (ViewGroup) getActivity().getWindow().getDecorView();
            View statusBarView = new View(getActivity());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight());

            statusBarView.setBackgroundColor(color);
            decorView.addView(statusBarView, lp);
        }*/

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

    /**
     * @param alpha
     * @param baseColor
     * @return
     */
    public int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    /**
     * @param delatY
     */
    public void upDragFlexibleSpaceAndText(int delatY) {
        Log.i("upDelatY", delatY + "   " + ((int) criticalY - delatY));
        // ViewHelper.setTranslationY(mFlexibleSpaceView, -delatY);
        if (mTitleView.getY() >= criticalY) {//title平移 与0取最大避免滑出上顶点
            ViewHelper.setTranslationY(mTitleView, criticalY - delatY);
            //刷新重置
            resetRefreshHeighet((int) (criticalY - delatY));
        }
        float scale = (-delatY) / flexibleSpaceAndToolbarHeight;
        ViewHelper.setPivotY(mFlexibleSpaceView, 0);
        ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);

    }

    private void onRefresh() {
        AppInfo.getUIHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resetRefresh();
            }
        }, 2000);
    }

    private void resetRefreshHeighet(int delatY) {
        //刷新重置
        int refreshY = Math.max(minHeighetY, Math.min(maxHeighetY, delatY - refreshHeight));
      /*  ViewHelper.setTranslationY(progressbar, refreshY);
        ViewHelper.setTranslationY(hitText, refreshY);*/
        if (delatY == minHeighetY) {
            progressbar.setVisibility(View.GONE);
//            hitText.setVisibility(View.GONE);
        }

    }

    /**
     */
    private void resetRefresh() {
        if (scrollView.getChildRootViewY() < 0) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(scrollView.getChildRootViewY(), 0);
            valueAnimator.setDuration(scrollView.SCROLL_BACK_TIME);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    scrollView.chileRootViewScroll(value);
                    if (value <= 0) {
                        upDragFlexibleSpaceAndText(value);
                    }
                }
            });
            valueAnimator.start();
            progressbar.setVisibility(View.GONE);
//            hitText.setVisibility(View.GONE);
            isRefreshing = false;
        }
    }

    /**
     * @param delatY
     */
    public void moveDragFlexibleSpaceAndText(int delatY) {
        if (delatY <= 0 && mTitleView.getY() >= criticalY) {//开始拉伸
            if (mTitleView.getY() > 0) {
                ViewHelper.setTranslationY(mTitleView, criticalY - delatY);//title平移 与0取最大避免滑出上顶点
                setRefreshState((int) (criticalY - delatY));
            }
            float scale = (-delatY) / flexibleSpaceAndToolbarHeight;
            ViewHelper.setPivotY(mFlexibleSpaceView, 0);
            ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);


            if (delatY > 0) {
//            ViewHelper.setTranslationY(mFlexibleSpaceView, -delatY - maxScrollY);
            }
        }
    }

    private int refreshHeight = HappinessApplication.mContext.getResources().getDimensionPixelSize(R.dimen._view_30);

    private void setRefreshState(int delatY) {
        //加载动画
        int refreshY = Math.max(minHeighetY, Math.min(maxHeighetY, delatY - refreshHeight));
        if (!isRefreshing) {//未处于刷新状态
            Log.i("zhikuan", "refresh =  " + refreshY + "  " + minHeighetY + "   " + ((int) criticalY - delatY));
            if (refreshY == minHeighetY) {//隐藏刷新
                progressbar.setVisibility(View.GONE);
//                hitText.setVisibility(View.GONE);
            }
            if (refreshY < maxHeighetY && refreshY > minHeighetY) {//下拉刷新
                progressbar.setVisibility(View.VISIBLE);
//                hitText.setVisibility(View.VISIBLE);
                ViewHelper.setTranslationY(progressbar, refreshY);
//                ViewHelper.setTranslationY(hitText, refreshY);
//                hitText.setText(getResources().getString(R.string.listview_header_hint_normal));
            }
            if (refreshY == maxHeighetY) {//松开刷新
                isRefreshing = true;
                progressbar.setVisibility(View.VISIBLE);
//                hitText.setVisibility(View.VISIBLE);
//                hitText.setText(getResources().getString(R.string.listview_header_hint_ready));
            }
        }
    }

    private int maxScrollY;

    /**
     * 更新标题的缩放
     *
     * @param scrollY
     */

    private void updateFlexibleSpaceText(final int scrollY) {
        if (scrollY > maxScrollY) {
            maxScrollY = scrollY;
        }
        ViewHelper.setTranslationY(mFlexibleSpaceView, -scrollY);
        if (mTitleView.getY() > criticalY && criticalY != 0) {
            float currentY = mTitleView.getY();
            ViewHelper.setTranslationY(mTitleView, currentY - scrollY);
            return;
        }
        int height_scale = mFlexibleSpaceHeight - getResources().getDimensionPixelSize(R.dimen._view_55);
        int adjustedScrollY = Math.min(height_scale, Math.max(0, scrollY));
        float maxScale = (float) (height_scale - mToolbarView.getHeight()) / mToolbarView.getHeight();
        float scale = maxScale * ((float) height_scale - adjustedScrollY) / height_scale;

        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, 1 + scale);
        ViewHelper.setScaleY(mTitleView, 1 + scale);
        int maxTitleTranslationY = mToolbarView.getHeight() + height_scale - (int) (mTitleView.getHeight() * (1 + scale));
        int titleTranslationY = (int) (maxTitleTranslationY * ((float) height_scale - adjustedScrollY) / height_scale);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        Log.i("titleTranslationY", "titleTranslationY  =  " + titleTranslationY);
        int bgColor = getResources().getColor(R.color.orange);
        float alpha = Math.min(0.95f, (float) scrollY / height_scale);
        mToolbarView.setBackgroundColor(getColorWithAlpha(alpha, bgColor));
        setStatusBarColor(getColorWithAlpha(alpha, bgColor));
        if (scrollY == 0) {//缩放动画停止
            criticalY = mTitleView.getY();
            minHeighetY = (int) (criticalY);
            maxHeighetY = minHeighetY + getResources().getDimensionPixelSize(R.dimen._view_40);
            criticalD = mFlexibleSpaceView.getY() - criticalY;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.i("onScrollChanged ", "scrolly = " + scrollY + " first = " + firstScroll + " dragging = " + dragging);
        updateFlexibleSpaceText(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }


}
