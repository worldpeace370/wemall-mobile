package com.inuoer.wemall;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inuoer.util.ActivityManager;
import com.inuoer.util.AsyncImageLoader;
import com.inuoer.util.CartData;
import com.inuoer.util.ShareValue;

import java.util.Random;

public class ShakeActivity extends AppCompatActivity {
    //摇一摇上下两张图片
    private ImageView imageView_main_logoup;
    private ImageView imageView_main_logodown;
    //声音池
    private SoundPool soundPool;
    private int soundId;
    //震动类
    private Vibrator vibrator;
    //传感器管理类
    SensorManager mSensorManager;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                showPopFood();
            }
        }
    };
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (x>=15 || y>=15 || z>=15) {

                if (mDialog != null){
                    mDialog.dismiss();
                }
                startAnimationByJava();
                startSound();
                startVibrator();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private Dialog mDialog;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        setTransparent();

        initView();
        initSensorManager();
        initVibrator();
        initSoundPool();
    }

    private void initView() {
        imageView_main_logoup = (ImageView) findViewById(R.id.imageView_main_logoup);
        imageView_main_logodown = (ImageView) findViewById(R.id.imageView_main_logodown);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        if (!ActivityManager.hasKitKat()){//API<14
            ViewGroup.LayoutParams layoutParams =  mToolBar.getLayoutParams();
            layoutParams.height = (int)(50 * getResources().getDisplayMetrics().density);//设置50dp高，height的值是px，所以需要转化
            mToolBar.setLayoutParams(layoutParams);
        }
    }

    public void click(View view){
        if (view.getId() == R.id.button_back){
            ShakeActivity.this.finish();
        }
    }

    /**
     * 加速度传感器的初始化,设置监听器
     */
    private void initSensorManager(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 初始化声音池,不同版本不同方法
     */
    private void initSoundPool(){
        //如果系统版本21执行这一段代码
        if (Build.VERSION.SDK_INT >= 21) {

        }else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        soundId = soundPool.load(this, R.raw.awe, 1);
    }
    //初始化震动加速度传感器
    private void initVibrator(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 开始震动
     */
    private void startVibrator(){
        // 定义震动
        // 只有1个参数的时候，第一个参数用来指定振动的毫秒数。
        // 要传递2个参数的时候，第1个参数用来指定振动时间的样本，第2个参数用来指定是否需要循环，-1为不重复，非-1则从pattern的指定下标开始重复
        // 振动时间的样本是指振动时间和等待时间的交互指定的数组，即节奏数组。
        // ※下面的例子，在程序起动后等待3秒后，振动1秒，再等待2秒后，振动5秒，再等待3秒后，振动1秒
        // long[] pattern = {3000, 1000, 2000, 5000, 3000, 1000};
        // 震动节奏分别为：OFF/ON/OFF/ON…
        vibrator.vibrate(new long[]{500, 200, 500, 200}, -1);
    }

    /**
     * 开始播放声音
     */
    private void startSound(){
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }


    /**
     * 定义摇一摇动画动画,java动画
     */
    public void startAnimationByJava() {
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation mup0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        mup0.setDuration(1000);
        TranslateAnimation  mup1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        mup1.setDuration(1000);

        //延迟执行1秒
        mup1.setStartOffset(1000);
        animup.addAnimation( mup0);
        animup.addAnimation( mup1);
        //上图片的动画效果的添加
        imageView_main_logoup.startAnimation(animup);


        AnimationSet animdn = new AnimationSet(true);
        TranslateAnimation mdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +0.5f);
        mdn0.setDuration(1000);
        TranslateAnimation  mdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -0.5f);
        mdn1.setDuration(1000);

        //延迟执行1秒
        mdn1.setStartOffset(1000);
        animdn.addAnimation(mdn0);
        animdn.addAnimation(mdn1);
        //下图片动画效果的添加
        imageView_main_logodown.startAnimation(animdn);

        //动画的监听，当动画结束后在里面进行操作
        animdn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束后开始发送消息，显示菜单窗体
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void showPopFood(){
        Random random = new Random();
        final int position = random.nextInt(13);

        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_detail, null);
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(layout);
        mDialog.show();
        final ImageView imageView = (ImageView) layout.findViewById(R.id.dialog_detail_big_image);
        AsyncImageLoader imageLoader = new AsyncImageLoader(this);
        //设置缓存到文件系统中去
        imageLoader.setCache2File(true);
        imageLoader.downloadImage(ShareValue.listItem.get(position).get("image").toString(), true,
                new AsyncImageLoader.ImageCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
        TextView textViewPrice = (TextView)layout.findViewById(R.id.dialog_detail_single_price);
        textViewPrice.setText(ShareValue.listItem.get(position).get("price").toString());
        TextView textViewName = ((TextView) layout.findViewById(R.id.dialog_detail_title_name));
        textViewName.setText(ShareValue.listItem.get(position).get("name").toString());
        final TextView textViewNum = (TextView) layout.findViewById(R.id.count);
        layout.findViewById(R.id.dialog_detail_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        layout.findViewById(R.id.dialog_detail_addcart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //点击增加物品数
        layout.findViewById(R.id.plus_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(textViewNum.getText().toString()) + 1;
                textViewNum.setText(String.valueOf(num));
                CartData.editCart(ShareValue.listItem.get(position).get("id").toString(),
                        ShareValue.listItem.get(position).get("name").toString(),
                        ShareValue.listItem.get(position).get("price").toString(),
                        String.valueOf(num),
                        ShareValue.listItem.get(position).get("image").toString());
            }
        });
        //点击减少物品数
        layout.findViewById(R.id.minus_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(textViewNum.getText().toString()) - 1;
                if (num >=0){
                    textViewNum.setText(String.valueOf(num));

                    if (num == 0) {
                        CartData.removeCart(ShareValue.listItem.get(position)
                                .get("id").toString());
                    } else {
                        CartData.editCart(ShareValue.listItem.get(position).get("id").toString(),
                                ShareValue.listItem.get(position).get("name").toString(),
                                ShareValue.listItem.get(position).get("price").toString(),
                                String.valueOf(num),
                                ShareValue.listItem.get(position).get("image").toString());
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
            mSensorManager = null;
        }
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
}
