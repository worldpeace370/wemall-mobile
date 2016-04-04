package com.inuoer.wemall;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity{

	private TranslateAnimation translateanim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.welcome_splash);
		
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
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		};
		timer.schedule(task, 2500);
	}

}
