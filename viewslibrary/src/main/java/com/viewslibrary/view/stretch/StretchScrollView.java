package com.viewslibrary.view.stretch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author yangzhikuan
 * @date 2014-04-30
 */
public class StretchScrollView extends ScrollView {
    private static final int MSG_REST_POSITION = 0x01;

    /**
     * The max scroll height.
     */
    private static final int MAX_SCROLL_HEIGHT = 400;
    public static final int SCROLL_BACK_TIME = 300;
    /**
     */
    public static final float SCROLL_RATIO = 0.4f;

    protected View mChildRootView;

    private float mTouchY;
    private boolean mTouchStop = false;

    private int mScrollY = 0;
    private int mScrollDy = 0;

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (MSG_REST_POSITION == msg.what) {
                if (mScrollY != 0 && mTouchStop) {
                    mScrollY -= mScrollDy;

                    if ((mScrollDy < 0 && mScrollY > -10) || (mScrollDy > 0 && mScrollY < 10)) {
                        mScrollY = 0;
                    }
                    mChildRootView.scrollTo(0, mScrollY);
                    if (scrollViewListener != null) {
                        scrollViewListener.onScrollUp(mScrollY);
                    }
                    // continue scroll after 20ms
                    sendEmptyMessageDelayed(MSG_REST_POSITION, 20);
                }
            }
        }
    };*/

    public StretchScrollView(Context context) {
        super(context);

        init();
    }

    public StretchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public StretchScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            mChildRootView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (null != mChildRootView) {
            doTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void doTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                mScrollY = mChildRootView.getScrollY();
                if (mScrollY != 0) {
                    mTouchStop = true;
                    mScrollDy = (int) (mScrollY / 10.0f);
//                    mHandler.sendEmptyMessage(MSG_REST_POSITION);
                    ScrollBack();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                int deltaY = (int) (mTouchY - nowY);
                mTouchY = nowY;
                if (isNeedMove()) {
                    int offset = mChildRootView.getScrollY();
//                    if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
                    mChildRootView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
                    if (scrollViewListener != null) {
                        scrollViewListener.onScrollMove(mChildRootView.getScrollY());
                    }
                    mTouchStop = false;
//                    }
                }
                break;

            default:
                break;
        }
    }

    private void ScrollBack() {
        if (scrollViewListener != null) {
            scrollViewListener.onScrollUp();
        }
    }

    public boolean isNeedMove() {
        int viewHeight = mChildRootView.getMeasuredHeight();
        int scrollHeight = getHeight();
        int offset = viewHeight - scrollHeight;
        int scrollY = getScrollY();

        return scrollY == 0 || scrollY == offset;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        Log.i("StretchScrollView  ", oldy + " " + y + "  " + mChildRootView.getScrollY());
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    private ScrollViewListener scrollViewListener = null;

    public interface ScrollViewListener {
        void onScrollMove(int delatY);

        void onScrollUp();
    }

    public int getChildRootViewY() {
        return mChildRootView.getScrollY();
    }

    /**
     * @param y
     */
    public void chileRootViewScroll(int y) {
        mChildRootView.scrollTo(0, y);
    }

}