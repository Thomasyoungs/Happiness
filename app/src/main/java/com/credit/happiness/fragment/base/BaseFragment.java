package com.credit.happiness.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 */
public abstract class BaseFragment extends Fragment {

    private final String TAG = "BaseFragment";

    public Activity context;
//    private LoadingDialog mProgressDialog = null;

    protected boolean isViewCreated = false;

    /* 预先配置 */
    protected void onPreConfigured() {
    }

    /* 生成通用主文件布局 */
    protected abstract int onSetContainerViewId();

    /* 初始化页面控件 */
    protected abstract void onInitView( View containerView);

    /* 初始化监听*/
    protected void onInitListener() {
    }

    /* 注册广播接收者*/
    protected void onRegisterReceiver() {
    }

    protected void unRegisterReceiver() {
    }

    /* 加载网络数据 */
    protected void onLoadNetworkData() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        setMenuVisibility(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        onPreConfigured();
        View containerView = inflater.inflate(onSetContainerViewId(), container, false);

        onInitView(containerView);
        onInitListener();
        onRegisterReceiver();

        onLoadNetworkData();

        return containerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // onAttach(getActivity());
        afterActivityCreated();
        super.onActivityCreated(savedInstanceState);
        onFragmentShow();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onFragmentShow() {
    }

    public void onFragmentHide() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            onFragmentShow();
        } else {
            onFragmentHide();
        }
    }
    /**
     * 此处不能采用context
     * 需要动态回调函数getActivity() 获取当前
     */
/*    protected void showProgressBar() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog == null) {
                        mProgressDialog = new LoadingDialog(getActivity());
                        mProgressDialog.setCancelable(true);
                    }
                    if (mProgressDialog != null && !mProgressDialog.isShowing() && !((Activity) context).isFinishing()) {
                        mProgressDialog.show();
                    }
                }
            });
        }
    }

    protected void dismissProgressBar() {
        if (getActivity() != null) {
            AppInfo.getUIHandler().post(new Runnable() {

                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });
        }
    }*/
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // onFragmentResume();
    }

    protected void afterActivityCreated() {
        isViewCreated = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // onFragmentPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void finish() {
        getActivity().finish();
    }





    /***************************************定制工具-封装区************************************************/

    /**
     * 显示相关的控件
     * 1-GONE 隐藏相关控件
     * 2-无形 控件的VISIBLE 和 GONE 不同之处不需要加载
     *
     * @param views
     */
    public void shows(View... views) {
        for (View view : views) {
            setVisibility(view, View.VISIBLE);
        }
    }

    public void hides(View... views) {
        for (View view : views) {
            setVisibility(view, View.GONE);
        }
    }

    public void invsibles(View... views) {
        for (View view : views) {
            setVisibility(view, View.INVISIBLE);
        }
    }

    /**
     * 设置控件的可见性. 判断控件当前的可见性和要设置的可见性是否相同.
     * 仅当可见性不同时才设置
     *
     * @param view
     * @param visibility
     */
    public void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

}
