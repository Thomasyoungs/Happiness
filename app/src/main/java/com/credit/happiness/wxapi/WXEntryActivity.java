package com.credit.happiness.wxapi;

import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * @author yagnzhikuan
 * @date 2018/3/30.
 */

public class WXEntryActivity  extends WXCallbackActivity {
//    OnWechatShareListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
//        if (listener != null) {
//            listener.onWechatShareCallback(resp);
//
//        }
    }


    public interface OnWechatShareListener {
        void onWechatShareCallback(BaseResp resp);
    }
}
