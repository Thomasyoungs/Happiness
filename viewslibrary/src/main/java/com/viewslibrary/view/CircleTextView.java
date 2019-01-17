package com.viewslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.TextView;

import com.viewslibrary.R;

/**
 * Created by Three.
 */
public class CircleTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mBgPaint = new Paint();
    private int mColor = Color.WHITE;

    PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttribute(attrs, context);
        mBgPaint.setColor(mColor);
        mBgPaint.setAntiAlias(true);
    }

    public CircleTextView(Context context) {
        super(context);
        mBgPaint.setColor(mColor);
        mBgPaint.setAntiAlias(true);
    }

    private void getAttribute(AttributeSet attrs, Context context) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.CircleTextView);
        try {
            mColor = ta.getColor(R.styleable.CircleTextView_circleColor, mColor);

        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    public void setBackgroundColor(int color) {
        mBgPaint.setColor(color);
        postInvalidate();
    }

    public void setNotifyText(int text) {
        setText(text + "");
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setDrawFilter(pfd);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, mBgPaint);
        super.draw(canvas);
    }
}
