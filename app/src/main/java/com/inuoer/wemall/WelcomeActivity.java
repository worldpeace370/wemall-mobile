package com.inuoer.wemall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.inuoer.util.ActivityManager;
import com.inuoer.util.Config;
import com.inuoer.util.SharedDataSave;

public class WelcomeActivity extends Activity{

	private TranslateAnimation translateanim;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.welcome_splash);
		setTransparent();
		initStartAnim();
	}

	/**
	 * 初始化首页动画,监听动画的结束，进而选择操作
	 */
	private void initStartAnim() {
		LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ImageView imageView = new ImageView(this);
		imageView.setPadding(0, 100, 0, 0);
		ll.addView(imageView);
		imageView.setImageResource(R.drawable.wemall);

		translateanim = new TranslateAnimation(0, 0, imageView.getY(), imageView.getY()+100);
		translateanim.setDuration(2000);
		translateanim.setFillAfter(true);
		imageView.startAnimation(translateanim);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(ll, params);
		translateanim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isFirstStartApp();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	/**
	 * 设置状态栏透明
	 */
	private void setTransparent() {
		if (ActivityManager.hasKitKat() && !ActivityManager.hasLollipop()){
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}else if (ActivityManager.hasLollipop()){
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}
	/**
	 * 如果是第一次安装,先显示一张图片,然后跳到引导页面(GuideActivity)
	 * 如果安装过了再次启动此App,则只显示一张图片(2秒),然后跳到MainActivity
	 * Handler Looper技术
	 */
	private void isFirstStartApp(){
		//初始化SharedPreferences,得到Config.IS_FIRST
		SharedDataSave.init(mContext);
		boolean isFirst = Config.IS_FIRST;
		if (isFirst){
			//启动GuideActivity
			goGuideActivity();
		}else {
			//进入到MainActivity
			goMainActivity();
		}
	}

	private void goMainActivity(){
		Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void goGuideActivity(){
		Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
		startActivity(intent);
		finish();
	}
}
