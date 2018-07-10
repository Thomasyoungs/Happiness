package com.credit.happiness.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.credit.happiness.R;


/**
 * 耗时操作时显示的对话框     有遮罩

 */
public class LoadingDialog extends Dialog {
	
	private View mView;
	private AnimationDrawable anim;
	private AlphaAnimation alphaAnimation;
	TextView mLoadingMessage=null;
	/**
	 * 默认提示语
	 */
	private String message = "拼命加载中...";
	/**
	 * 默认透明度
	 */
	private float alpha = 0.8f;

	public LoadingDialog(Context context){
		super(context, R.style.loadingDialog);
		initView(context);
	}


	public LoadingDialog(Context context, String msg) {
		super(context, R.style.loadingDialog);
		this.message = msg;
		initView(context);
	}
	public LoadingDialog(Context context, String msg, int style) {
		super(context, style);
		this.message = msg;
		initView(context);
	}
	/**
	 * 初始化
	 * @param context
	 */
	private void initView(Context context) {
		LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		mView = mLayoutInflater.inflate(R.layout.dialog_loading, null);
		mLoadingMessage = (TextView) mView.findViewById(R.id.df_loading_message);
		ImageView mLoadingImage = (ImageView) mView.findViewById(R.id.df_loading_image);
		mLoadingMessage.setText(message);
		mLoadingImage.setBackgroundResource(R.drawable.load_frame);
		anim = (AnimationDrawable) mLoadingImage.getBackground();
		alphaAnimation = new AlphaAnimation(0f, 1f);
		alphaAnimation.setDuration(500);
		setContentView(mView);
		setCancelable(false);
	}

	/**
	 * 开启动画
	 */
	private void startAnim() {
		// 如果正在转动，先停止，再转动
		if(!anim.isRunning()){
			anim.start();
		}
	}

	/**
	 * 停止动画
	 */
	private void stopAnim() {
		if (anim.isRunning()) {
			anim.stop(); // 如果正在播放 停止
			mView.clearAnimation();
		}
	}


	/**
	 * 设置窗口透明度
	 */
	private void setWindowAlpha() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = alpha;
		window.setAttributes(lp);
	}
	public  void setBackground(int id){
		mView.setBackgroundResource(id);
	}
	/**
	 * 设置透明度
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}


	public float getAlpha() {
		return alpha;
	}


	public String getMessage(){
		return message; 
	}

	public void setMessage(String message){
		this.message = message;
	}

	public void setTextViewInVisible(){
		this.mLoadingMessage.setVisibility(View.GONE);
	}
	public void setTextViewVisible(){
		this.mLoadingMessage.setVisibility(View.VISIBLE);
	}
	@Override
	public void show() {
		super.show();
		mView.startAnimation(alphaAnimation);
		setWindowAlpha();
		startAnim();
	}

	@Override
	public void hide() {
		stopAnim();
		super.hide();
	}

	@Override
	public void dismiss() {
		stopAnim();
		super.dismiss();
	}

	@Override
	public void cancel() {
		stopAnim();
		super.cancel();
	}
}
