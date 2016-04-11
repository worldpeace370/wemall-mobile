package com.inuoer.wemall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.inuoer.util.ActivityManager;
import com.inuoer.widget.ParallaxContainer;

public class GuideActivity extends Activity {

    ImageView iv_man;
    ParallaxContainer mParallaxContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setTransparent();

        iv_man = (ImageView) findViewById(R.id.iv_man);
        mParallaxContainer = (ParallaxContainer) findViewById(R.id.parallax_container);

        if (mParallaxContainer != null) {
            mParallaxContainer.setImage(iv_man);
            mParallaxContainer.setLooping(false);

            iv_man.setVisibility(View.VISIBLE);
            mParallaxContainer.setupChildren(getLayoutInflater(),
                    R.layout.view_intro_1, R.layout.view_intro_2,
                    R.layout.view_intro_3, R.layout.view_intro_4,
                    R.layout.view_intro_5, R.layout.view_login);
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

    public void startMain(){
        Intent intent = new Intent(GuideActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startRegLoginActivity(){
        Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void click(View view){
        switch (view.getId()){
            case R.id.rl_weibo:
                startMain();
                break;
            case R.id.phone_reg:
                startRegLoginActivity();
                break;
            case R.id.phone_login:
                startRegLoginActivity();
                break;
        }
    }
}
