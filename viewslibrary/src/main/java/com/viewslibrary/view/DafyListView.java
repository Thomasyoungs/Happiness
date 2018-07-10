/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create UpMarqueeTextView 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement GListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.viewslibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.viewslibrary.R;

import java.util.ArrayList;

/*
 *
 */
public class DafyListView extends ListView implements OnScrollListener {

    protected float mLastY = -1; // save event y
    protected float mLastY2 = -1; // save event y
    protected float mLastX = -1; // save event x
    protected float mFirstY = -1; // save event y
    protected float mFirstX = -1; // save event x
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    protected GListViewListener mListViewListener;

    // -- header view
    protected DafyListViewHeader greatListViewHeader;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    protected int greatHeaderViewHeight; // header view's height
    protected boolean mEnablePullRefresh = true;
    protected boolean mPullRefreshing = false; // is refreashing.

    private ArrayList<String> mHeaderViewHashList = new ArrayList<String>();
    // -- footer view
    protected DafyListViewFooter mFooterView;
    protected boolean mEnablePullLoad;
    protected boolean mPullLoading;
    protected boolean mSlideEnable = false;
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    protected int mTotalItemCount;
    protected int firstVisibleItem;
    private int mAheadPullUpCount = 0; // 提前调用pullup的提前量
    private int mLastVisibleIndex = -1;
    private int mLastItemCount = -1;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    protected final static int SCROLL_DURATION = 400; // scroll back duration
    protected final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    protected final static float OFFSET_RADIO = 2.1f; // support iOS like pull
    protected final static float OFFSET = 2.8f; // support iOS like pull
    // feature.
    protected int mFlipDirection = 0;//0-vertical 1:horizon
    protected int mTouchSlop;
    protected int selectedItemPosition = -1;
    protected boolean isSkip = false;


    /* listView的每一个item的布局 */
    private ViewGroup viewGroup;

    /**
     * @param context
     */
    public DafyListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public DafyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public DafyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    @SuppressWarnings("deprecation")
    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        greatListViewHeader = new DafyListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) greatListViewHeader
                .findViewById(R.id.listview_header_content);
        mHeaderTimeView = (TextView) greatListViewHeader
                .findViewById(R.id.listview_header_time);
        addHeaderView(greatListViewHeader, null, true);

        // init footer view
        mFooterView = new DafyListViewFooter(context);

        // init header height
        greatListViewHeader.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        greatHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
        ViewConfiguration config = ViewConfiguration.get(context);

    }


    /**
     * @param res
     */
    public void setHeaderContentColor(int res) {
        greatListViewHeader.setmContainerColor(res);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    @Override
    public void addHeaderView(View v) {
        int viewHash = v.hashCode();
        boolean isAdded = false;
        if (mHeaderViewHashList != null && mHeaderViewHashList.size() > 0) {
            for (String hash : mHeaderViewHashList) {
                if (hash != null && hash.equals(String.valueOf(viewHash))) {
                    isAdded = true;
                    return;
                }
            }
        }
        mHeaderViewHashList.add(String.valueOf(viewHash));
        super.addHeaderView(v);
    }

    @Override
    public boolean removeHeaderView(View v) {
        int viewHash = v.hashCode();
        boolean isAdded = false;
        if (mHeaderViewHashList != null && mHeaderViewHashList.size() > 0) {
            for (String hash : mHeaderViewHashList) {
                if (hash != null && hash.equals(String.valueOf(viewHash))) {
                    isAdded = true;
                    break;
                }
            }
            if (isAdded) {
                mHeaderViewHashList.remove(String.valueOf(viewHash));
            }
        }
        return super.removeHeaderView(v);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setFooterText(getResources().getString(R.string.listview_footer_hint_normal));
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(DafyListViewFooter.STATE_NORMAL);
        }
    }

    public void setSlideEnable(boolean isEnable) {
        mSlideEnable = isEnable;
        if (mSlideEnable) {
            mFlipDirection = -1;
        } else {
            mFlipDirection = 0;
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    protected void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    protected void updateHeaderHeight(float delta) {
        if (!mEnablePullRefresh)
            return;

        greatListViewHeader.setVisiableHeight((int) delta
                + greatListViewHeader.getVisiableHeight());
        if (!mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (greatListViewHeader.getVisiableHeight() > greatHeaderViewHeight) {
                greatListViewHeader.setState(DafyListViewHeader.STATE_READY);
            } else {
                greatListViewHeader.setState(DafyListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    protected void resetHeaderHeight() {
        int height = greatListViewHeader.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= greatHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > greatHeaderViewHeight) {
            finalHeight = greatHeaderViewHeight;
        }

        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    protected void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(DafyListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(DafyListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

    }

    protected void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    protected void startLoadMore() {
        if (mPullLoading == true) {
            return;
        }
        mPullLoading = true;
        mFooterView.setState(DafyListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    public interface OnInnerListScrolListner {
        void onScrollEnd(float deltaY, boolean isFirstShow, int scrollStat);

        boolean onScroll(float deltaY, boolean isFirstShow, int scrollStat);

    }

    private OnInnerListScrolListner innerListScrolListner;

    public void setOnScrollstateListener(OnInnerListScrolListner l) {
        innerListScrolListner = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY2 == -1) {
            mLastY2 = ev.getY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY2 = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                final float deltaY = ev.getY() - mLastY2;
                mLastY2 = ev.getY();
                if (innerListScrolListner != null && mFlipDirection != 1 && !innerListScrolListner.onScroll(deltaY / OFFSET,
                        getFirstVisiblePosition() == 0, currentScrollState)) { //获取list滚动信息的listener

                    return true;
                }


                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                mLastY2 = -1; // reset
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (mLastY == -1) {
            mLastY = ev.getY();
        }
        if (mFirstY == -1) {
            mFirstY = ev.getY();
        }
        if (mLastX == -1) {
            mLastX = ev.getX();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstX = ev.getX();
                mFirstY = ev.getY();
                mLastX = ev.getX();
                mLastY = ev.getY();
                isSkip = false;
                if (mSlideEnable) {
                    mFlipDirection = -1;

                } else {
                    mFlipDirection = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSlideEnable && isSkip) {
                    return true;
                }
                if (mSlideEnable && mFlipDirection == -1) {
                    if (Math.abs(ev.getY() - mFirstY) > mTouchSlop) {
                        mFlipDirection = 0;
                    } else if (Math.abs(ev.getX() - mFirstX) > mTouchSlop) {
                        mFlipDirection = 1;
                    }
//
                }
                if (Math.abs(ev.getY() - mFirstY) > 0 || Math.abs(ev.getX() - mFirstX) > 0) {
                    if (Math.abs(ev.getY() - mFirstY) >= 2 * Math.abs(ev.getX() - mFirstX)) {
                        mFlipDirection = 0;
                    } else {
                        mFlipDirection = 1;
                    }
                }
                if (mFlipDirection == 0) {
                    final float deltaX = ev.getX() - mLastX;
                    mLastX = ev.getX();
                    final float deltaY = ev.getY() - mLastY;
                    mLastY = ev.getY();
//                    Log.i("getScrollHeight", getScrollHeight() + "getScrollHeight" + MatchDetailFragment.headerHeight);
//                    if (innerListScrolListner != null && !innerListScrolListner.onScroll(deltaY / OFFSET_RADIO,
//                            getFirstVisiblePosition() == 0, currentScrollState)) { //获取list滚动信息的listener
////                        return super.dispatchTouchEvent(ev);
//                        return true;
//                    }

                    if (getFirstVisiblePosition() == 0
                            && (greatListViewHeader.getVisiableHeight() > 0 || deltaY > 0)) {
                        // the first item is showing, header has shown or pull down.
                        updateHeaderHeight(deltaY / OFFSET_RADIO);
                        invokeOnScrolling();
                    } else if (mEnablePullLoad && getLastVisiblePosition() == mTotalItemCount - 1
                            && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                        // last item, already pulled up or want to pull up.
                        updateFooterHeight(-deltaY / OFFSET_RADIO);
                    }
                } else if (mFlipDirection == 1) {
                    if (selectedItemPosition < this.getHeaderViewsCount()
                            || selectedItemPosition >= this.getCount() - this.getFooterViewsCount()) {
                        return super.dispatchTouchEvent(ev);
                    }

                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                float deltaY = ev.getY() - mFirstY;
                if (innerListScrolListner != null && mFlipDirection != 1) {
                    innerListScrolListner.onScrollEnd(deltaY / OFFSET, getFirstVisiblePosition() == 0, currentScrollState);
                }
                mLastX = -1; // reset
                mLastY = -1; // reset
                mFirstX = -1;
                mFirstY = -1;
                if (mSlideEnable && isSkip) {
                    return true;
                }
                if (!mSlideEnable || mFlipDirection != 1) {
                    if (getFirstVisiblePosition() == 0) {
                        // invoke refresh
                        if (mEnablePullRefresh
                                && greatListViewHeader.getVisiableHeight() > greatHeaderViewHeight) {
                            mPullRefreshing = true;
                            greatListViewHeader.setState(DafyListViewHeader.STATE_REFRESHING);
                            if (mListViewListener != null) {
                                mListViewListener.onRefresh();
                            }
                        }
                        resetHeaderHeight();
                    } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                        // invoke load more.
                        if (mEnablePullLoad
                                && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
                                && !mPullLoading) {
                            startLoadMore();
                        }
                        resetFooterHeight();
                    }
                }
                if (mSlideEnable && mFlipDirection == 1) {
                    Log.i("GreatlistView", "横向滑动结束");
                    mFlipDirection = -1;
                    return true;
                }
                mFlipDirection = -1;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     *
     */
    public void callRefreshAnim(int height) {
        mPullRefreshing = true;
        updateHeaderHeight(greatHeaderViewHeight + height);
        greatListViewHeader.setState(DafyListViewHeader.STATE_REFRESHING);

    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                greatListViewHeader.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    public int currentScrollState;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollState == SCROLL_STATE_IDLE && innerListScrolListner != null) {
//            innerListScrolListner.onScrollEnd(0, false, 0);
        }
        currentScrollState = scrollState;

    }


    public int getScrollHeight() {

        View c = getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
        if (mEnablePullLoad) {
            this.firstVisibleItem = firstVisibleItem;
            int lastVisibleItem = firstVisibleItem + visibleItemCount;
            // 距离底部mAheadPullUpCount调用加载更多，或滑动到底部也掉加载更多(为了解决加载出来的总数小于mAheadPullUpCount，导致不自动加载)
            if ((lastVisibleItem == totalItemCount && lastVisibleItem != mLastVisibleIndex)
                    || (lastVisibleItem + mAheadPullUpCount >= totalItemCount && mLastVisibleIndex + mAheadPullUpCount < mLastItemCount)) {
                // 在最后一个item内移动时，不要触发loadmore
                if (!mEnablePullLoad || mPullLoading)
                    return;
                // 数量充满屏幕才触发
                if (isFillScreenItem()) {
                    startLoadMore();
                }
            }
            mLastVisibleIndex = lastVisibleItem;
            mLastItemCount = totalItemCount;
        }
    }

    /**
     * @param count
     */
    public void setmAheadPullUpCount(int count) {
        mAheadPullUpCount = count;
    }

    /**
     * 条目是否填满整个屏幕
     */
    private boolean isFillScreenItem() {
        final int firstVisiblePosition = getFirstVisiblePosition();
        final int lastVisiblePostion = getLastVisiblePosition() - getFooterViewsCount();
        final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
        final int totalItemCount = getCount() - getFooterViewsCount();
        if (visibleItemCount < totalItemCount)
            return true;
        return false;
    }


    public void setDafyListViewListener(GListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface GListViewListener {
        public void onRefresh();

        public void onLoadMore();

        public void onDeleteItem(final int index);
    }

    public void setFooterViewVisible(boolean flag) {
        if (!mEnablePullLoad && !flag) {
            mFooterView.hide();
        } else if (mEnablePullLoad && flag) {
            mFooterView.show();
        }
    }

    public boolean isPullRefreshing() {
        return mPullRefreshing;
    }

    /**
     * 没有更多
     *
     * @param content
     */
    public void setFooterText(String content) {
        if (mFooterView != null) {
            mFooterView.show();
            this.setFooterViewVisible(true);
            mFooterView.setFooterText(content, getResources().getColor(R.color.black_233a59));
            mFooterView.setOnClickListener(null);
            mEnablePullLoad = false;
        }
    }

    public void setFooterText(String content, int color) {
        if (mFooterView != null) {
            mFooterView.show();
            this.setFooterViewVisible(true);
            mFooterView.setFooterText(content, color);
            mFooterView.setOnClickListener(null);
            mEnablePullLoad = false;
        }
    }

    public View getFooterView() {
        return mFooterView;
    }


}
