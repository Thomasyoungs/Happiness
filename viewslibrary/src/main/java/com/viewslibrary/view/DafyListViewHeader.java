/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.viewslibrary.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewslibrary.R;

public class DafyListViewHeader extends LinearLayout {
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    //	private ProgressBar mProgressBar;
    private ImageView animation;

    private TextView mHintTextView;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private AnimationDrawable animationDrawable;//动画关键组件

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    public DafyListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public DafyListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.greatlistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.listview_header_hint_textview);
//		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);
        animation = (ImageView) findViewById(R.id.xlistview_header_progressbar);
        animation.setBackgroundResource(R.drawable.load_frame);
        animationDrawable = (AnimationDrawable) animation.getBackground();


        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

//	public void setXListViewHeaderStyle(int style) {
//		if(style == 1) {
//			mProgressBar.setIndeterminateDrawable(
//					getResources().getDrawable(R.anim.loading_progressbar_anim_white));
//		}
//	}


    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {    // 显示进度
//			mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
//			mProgressBar.setVisibility(View.VISIBLE);
            animation.setVisibility(VISIBLE);
            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }
        } else {    // 显示箭头图片
//			mArrowImageView.setVisibility(View.VISIBLE);
//			mProgressBar.setVisibility(View.INVISIBLE);
//			mHintTextView.setVisibility(View.VISIBLE);
            mArrowImageView.setVisibility(View.INVISIBLE);
//			mProgressBar.setVisibility(View.VISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    animation.setVisibility(GONE);
                    animationDrawable.stop();
                }
                if (mState == STATE_REFRESHING) {
                    animation.setVisibility(GONE);
                    animationDrawable.stop();
                }
//          if (mState == STATE_READY) {
//				mArrowImageView.startAnimation(mRotateDownAnim);
//			}
//			if (mState == STATE_REFRESHING) {
//				mArrowImageView.clearAnimation();
//			}
                mHintTextView.setText(R.string.listview_header_hint_normal);
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    animation.setVisibility(VISIBLE);
//				mArrowImageView.clearAnimation();
//				mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.listview_header_hint_ready);
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText(R.string.listview_header_hint_loading);
                break;
            default:
        }

        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    /**
     * @param res
     */
    public void setmContainerColor(int res) {
        mContainer.setBackgroundColor(res);
    }
}
