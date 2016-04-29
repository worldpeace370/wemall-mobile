package com.inuoer.wemall;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.inuoer.util.ActivityManager;

/**需要将Activity在清单文件中设置android:theme="@style/Theme.AppCompat.Light.NoActionBar"
 * 设置状态栏透明
 * Created by wuxiangkun on 2016/4/20 13:42.
 * Contacts wuxiangkun@live.com
 */
public abstract class FatherActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        setTransparent();
        initView();
    }

    /**
     * 返回布局资源id
     * @return
     */
    protected abstract int getContentView();
    /**
     * 初始化view
     */
    protected abstract void initView();
    /**
     * 查找view
     */
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
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
//    private long exitTime;
    /**
     * 如果两秒内按了两次返回键则退出程序,否则不会
     * @param keyCode
     * @param event
     * @return
     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
//            if (System.currentTimeMillis() - exitTime > 2000){
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            }else {
//                this.finish();
//            }
//            exitTime = System.currentTimeMillis();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
