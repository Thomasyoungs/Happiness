package com.credit.happiness.fragment.personal;

import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.credit.happiness.R;
import com.credit.happiness.fragment.base.BaseFragment;
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


    @Override
    protected int onSetContainerViewId() {
        return R.layout.fragment_home_personalcnenter_layout;
    }

    @Override
    protected void onInitView(View containerView) {

        progressbar = containerView.findViewById(R.id.default_header_progressbar);
        progressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.load_frame));
        mToolbarView = containerView.findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(getColorWithAlpha(0, getResources().getColor(R.color.orange)));
        mTitleView = containerView.findViewById(R.id.flexible_title_layout);
        getActivity().setTitle(null);
        mFlexibleSpaceView = containerView.findViewById(R.id.flexible_space);
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        flexibleSpaceAndToolbarHeight = mFlexibleSpaceHeight + getActionBarSize();
        containerView.findViewById(R.id.body).setPadding(0, (int) flexibleSpaceAndToolbarHeight, 0, 0);
//        containerView.findViewById(R.id.body).setPadding(0, (int) getActionBarSize(), 0, 0);
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
            public void onScrollUp(int delatY) {
                Log.i("onScrollUp", "delatY = " + delatY);
                upDragFlexibleSpaceAndText(delatY);
            }
        });

    }

    @Override
    protected void onInitListener() {
      /*  ViewTreeObserver vto = mFlexibleSpaceView.getViewTreeObserver();
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
        });*/
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
     * @param delatY
     */
    public void upDragFlexibleSpaceAndText(int delatY) {
        if (delatY <= 0) {
//                    ViewHelper.setTranslationY(mFlexibleSpaceView, -delatY);
            if (mTitleView.getY() > 0) {
                ViewHelper.setTranslationY(mTitleView, criticalY - delatY);//title平移 与0取最大避免滑出上顶点
            }
            float scale = (-delatY) / flexibleSpaceAndToolbarHeight;
            ViewHelper.setPivotY(mFlexibleSpaceView, 0);
            ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);
        }
    }

    /**
     * @param delatY
     */
    public void moveDragFlexibleSpaceAndText(int delatY) {
        if (delatY <= 0 && mTitleView.getY() >= criticalY) {//开始拉伸
//                    ViewHelper.setTranslationY(mFlexibleSpaceView, -delatY);
            if (mTitleView.getY() > 0) {
                ViewHelper.setTranslationY(mTitleView, criticalY - delatY);//title平移 与0取最大避免滑出上顶点
            }
            float scale = (-delatY) / flexibleSpaceAndToolbarHeight;
            ViewHelper.setPivotY(mFlexibleSpaceView, 0);
            ViewHelper.setScaleY(mFlexibleSpaceView, scale + 1);
            //加载动画
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新标题的缩放
     *
     * @param scrollY
     */
    private void updateFlexibleSpaceText(final int scrollY) {
        ViewHelper.setTranslationY(mFlexibleSpaceView, -scrollY);
        if (mTitleView.getY() > criticalY && criticalY != 0) {
            float currentY = mTitleView.getY();
            ViewHelper.setTranslationY(mTitleView, currentY - scrollY);
            return;
        }
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
            criticalY = mTitleView.getY();
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

    public int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
