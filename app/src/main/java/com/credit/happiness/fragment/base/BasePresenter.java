package com.credit.happiness.fragment.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author yangzhikuan  2017/12/13
 */
public abstract class BasePresenter<T extends BaseView> {
    protected Reference<T> mView;

    /**
     * 绑定View
     */
    public void onAttch(T view) {
        this.mView = new WeakReference<T>(view);

    }

    /**
     * 做初始化的操作,需要在V的视图初始化完成之后才能调用
     * presenter进行初始化.
     */
    public abstract void onCreate();

    /**
     * 在这里结束异步操作
     */
    public void onDestroy() {

    }

    protected T getView() {
        return mView.get();
    }

    public boolean isViewAttached() {
        return mView != null && mView.get() != null;
    }

    public void detachView() {

    }

    /**
     * 在V销毁的时候调用,解除绑定
     */
    public void onDetach() {
        if (mView != null) {
            mView.clear();
            mView = null;
        }
    }
    /**
     * 容易被回收掉时保存数据
     */
//    public abstract void onSaveInstanceState(Bundle outState);
}

