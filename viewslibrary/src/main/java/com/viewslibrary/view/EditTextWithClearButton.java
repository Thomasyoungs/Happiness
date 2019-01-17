/**
 *
 */
package com.viewslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.viewslibrary.R;


/**
 * 右侧带有清空按钮的文本编辑框
 *
 * 使用方法：
 *
 * 将layout.xml中的 EditText 换成
 *
 *
 * layout中可使用的属性：
 *
 * clearButtonOffset: 清空按钮的左右偏移，正值往右偏移，负值往左偏移。不定义该属性时， 清空按钮在离上、下、右三个边边缘等距离的位置绘制；

 * clearButtonDrawable: 清空按钮的绘图 Drawable 素材，可定制清空按钮行为样式；
 *
 * @author tengfei.li@renren-inc.com
 *
 *         modified by bingbing.qin
 */
public class EditTextWithClearButton extends android.support.v7.widget.AppCompatEditText implements
        OnTouchListener {

    private Drawable mClearButtonDrawable;

    private int mClearButtonOffset;

    private OnTouchListener mOnTouchListener;

    private OnClickListener mOnClearButtonClickListener;

    private TextWatcher mTextWatcher;

    private OnMyFocusChangeListener OnMyFocusChangeListener;

    private boolean ishasFocus = false;

    private boolean isClearBofore = false;

    public void setOnTextWatcher(TextWatcher textWatcher) {
        mTextWatcher = textWatcher;
    }

    ;

    public OnClickListener getOnClearButtonClickListener() {
        return mOnClearButtonClickListener;
    }

    public void setOnClearButtonClickListener(OnClickListener l) {
        this.mOnClearButtonClickListener = l;
    }

    public EditTextWithClearButton(Context context) {
        this(context, null);
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent(context, attrs);
    }

    public EditTextWithClearButton(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        initComponent(context, attrs);
    }

    /**
     * 0
     * 初始化自定义控件
     */
    private final void initComponent(Context context, AttributeSet attrs) {
        TypedArray params = context.obtainStyledAttributes(attrs,
                R.styleable.EditTextWithClearButton);
        try {
            mClearButtonOffset = (int) params.getDimension(
                    R.styleable.EditTextWithClearButton_clearButtonOffset, 0);

            int drawableResId = params.getResourceId(
                    R.styleable.EditTextWithClearButton_clearButtonDrawable,
                    R.drawable.icon_delete);

            setClearButtonDrawable(drawableResId);

            // 注入 OnTouch 处理， 首先截获处理 OnTouch 事件
            super.setOnTouchListener(this);
            this.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    ishasFocus = hasFocus;
                    String string = getText().toString();
                    if (hasFocus && string != null && string.length() > 0) {
                        showClearButton();
                    } else {
                        hideClearBtn();
                    }
                    if (hasFocus && getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                        isClearBofore = true;
                        if (string != null && string.length() > 0) {
//                            setSelection(string.length());
                        }
                    } else {
                        isClearBofore = false;
                    }
                    if (OnMyFocusChangeListener != null) {
                        OnMyFocusChangeListener.onMyFocuschanged(v,hasFocus);
                    }
                }
            });
        } finally {
            params.recycle();
        }
    }

    public void setMyFocusChangeListener(OnMyFocusChangeListener listener) {
        OnMyFocusChangeListener = listener;
    }

    /**
     * 获取清空按钮x轴方向绘制偏移
     *
     * @return
     */
    public int getClearButtonOffset() {
        return mClearButtonOffset;
    }

    /**
     * 设置清空按钮x轴方向绘制偏移
     *
     * @param clearButtonOffset 清空按钮x轴方向绘制偏移
     */
    public void setClearButtonOffset(int clearButtonOffset) {
        this.mClearButtonOffset = clearButtonOffset;
        setClearButtonBounds();
    }

    /**
     * 显示清空按钮
     */
    private final void showClearButton() {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], mClearButtonDrawable,
                drawables[3]);
    }

    /**
     * 隐藏清空按钮
     */
    private final void hideClearButton() {
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
    }

    /**
     * 清空按钮是否显示
     *
     * @return 清空按钮是否显示
     */
    private final boolean isClearButtonVisiable() {
        return null != getCompoundDrawables()[2];
    }

    /**
     * 当前坐标点是否在清空按钮上（目前只用 x 坐标，忽略 y 坐标）
     *
     * @param x 判定点的x坐标
     * @return 是否在清空按钮上
     */
    private final boolean isOnClearButton(float x) {
        Rect bounds = mClearButtonDrawable.getBounds();
        return x > getWidth() - getPaddingRight() - bounds.width()
                + bounds.left;
    }

    /**
     * 设置清除按钮的Drawable
     *
     * @param resId 资源id
     */
    public void setClearButtonDrawable(int resId) {
        mClearButtonDrawable = getResources().getDrawable(resId);
        setClearButtonBounds();
    }

    /**
     * 计算设置清空按钮的绘制区域
     */
    private void setClearButtonBounds() {
        // 默认的绘制偏移是到上下右侧边缘等距离
        int xOffset = (getHeight() - mClearButtonDrawable.getIntrinsicHeight())
                / 2 + mClearButtonOffset;

        mClearButtonDrawable.setBounds(xOffset, 0,
                mClearButtonDrawable.getIntrinsicWidth() + xOffset,
                mClearButtonDrawable.getIntrinsicHeight());
        setCompoundDrawablePadding(mClearButtonDrawable.getIntrinsicWidth() / 2
                - xOffset);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 按键抬起 && 清空按钮显示 && 点击在清空按钮上 时
        if (MotionEvent.ACTION_UP == event.getAction()
                && isClearButtonVisiable() && isOnClearButton(event.getX())) {

            setText("");
            if (null != mOnClearButtonClickListener) {
                mOnClearButtonClickListener.onClick(this);
            }
        }

        if (null != mOnTouchListener) {
            return mOnTouchListener.onTouch(v, event);
        }

        return false;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        // 注入 OnTouch 处理
        mOnTouchListener = l;
    }


    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        // 有输入时显示，无输入内容时隐藏
        if (!TextUtils.isEmpty(text) && ishasFocus) {
            showClearButton();
//            if (isClearBofore) {
//                int len = text.length();
//                if (len > 2) {
//                    String subString = text.subSequence(len - 2, len - 1).toString();
//                    setText(subString);
//                    setSelection(subString.length());
//                }
//                isClearBofore = false;
//            }
        } else {
            hideClearButton();
        }
        if (mTextWatcher != null)
            mTextWatcher.onTextChanged(text, start, lengthBefore, lengthAfter);
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    /**
     * 隐藏清空按钮--
     * --renlei
     */
    public void hideClearBtn() {
        hideClearButton();
    }

    public interface OnMyFocusChangeListener {
        public void onMyFocuschanged(View v, boolean focus);
    }
}
