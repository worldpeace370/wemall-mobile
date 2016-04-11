package com.inuoer.wemall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuoer.util.ActivityManager;

/**
 * 此活动刚启动就跳转到二维码扫描界面，这里用的是startActivityForResult跳转
 * 扫描完了之后调到该界面
 */
public class QRCodeResult extends AppCompatActivity {

    private ImageButton button_back;
    /**
     * 显示扫描结果
     */
    private TextView textView_showInfo ;
    /**
     * 显示扫描拍的图片
     */
    private ImageView imageView_photo;
    private TextView textView_qrcode_title;
    //扫描的结果
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qrcode_result);
        setTransparent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        textView_showInfo = (TextView) findViewById(R.id.textView_showInfo);
        imageView_photo = (ImageView) findViewById(R.id.imageView_photo);
        textView_qrcode_title = (TextView)toolbar.findViewById(R.id.textView_qrcode_title);
        button_back = (ImageButton) toolbar.findViewById(R.id.button_back);
        //此活动刚启动就跳转到二维码扫描界面，这里用的是startActivityForResult跳转
        //扫描完了之后调到该界面
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        textView_qrcode_title.setText("扫描结果");
        result = bundle.getString("result");
        textView_showInfo.setText(result);
        imageView_photo.setImageBitmap((Bitmap) bundle.getParcelable("bitmap"));
        //按下返回键时销毁活动
        button_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                QRCodeResult.this.finish();
            }
        });

        /**
         * 存放超链的TextView的点击事件,跳转到WebView
         */
        textView_showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(QRCodeResult.this, QRCodeDetailWebView.class);
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                intent.putExtras(bundle);
                startActivity(intent);
                QRCodeResult.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
