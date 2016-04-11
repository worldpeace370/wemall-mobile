package com.inuoer.wemall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.inuoer.camera.CameraManager;
import com.inuoer.decoding.CaptureActivityHandler;
import com.inuoer.decoding.InactivityTimer;
import com.inuoer.decoding.RGBLuminanceSource;
import com.inuoer.util.ActivityManager;
import com.inuoer.util.PhotoChoiceUtils;
import com.inuoer.view.ViewfinderView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Initial the com.lebron.com.inuoer.camera
 * @author Ryan.Tang
 */
public class MipcaCapture extends AppCompatActivity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private TextView textView_qrcode_title;
    private static final int REQUEST_CODE = 1;
    //调用系统相册后选择相片的路径
    private String photoPath;
    private ProgressDialog progressDialog;
    //发送消息的what,在程序退出的时候最好移除
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    //扫描相册图片得到的bitmap
    private static Bitmap scanBitmap;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mipca_capture);
        setTransparent();
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        textView_qrcode_title = (TextView)toolbar.findViewById(R.id.textView_qrcode_title);
        textView_qrcode_title.setText("扫一扫");
        ImageButton mButtonBack = (ImageButton) toolbar.findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MipcaCapture.this.finish();
            }
        });
        /**
         * 调用系统相册，选择二维码图片扫描
         */
        Button button_photos = (Button) toolbar.findViewById(R.id.button_photos);
        button_photos.setVisibility(View.VISIBLE);
        button_photos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                innerIntent.setType("image/*");
//                innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//                MipcaCapture.this.startActivityForResult(wrapperIntent, REQUEST_CODE);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                MipcaCapture.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    /**
     * 点击相册，弹出相册进行选择后，将选择的结果回调到此处
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){ //选择相片成功后，系统的返回结果码
            switch (requestCode){  //startActivityForResult携带的请求码
                case REQUEST_CODE:
                    //4.4版本以后获取相册图片路径的新方法
                    photoPath = PhotoChoiceUtils.getImageAbsolutePath(this, data.getData());

                    progressDialog = new ProgressDialog(MipcaCapture.this);
                    progressDialog.setMessage("正在扫描...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //开启子线程扫描解析，防止ANR异常Application Not Responding
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photoPath);
                            if (null != result){
                                Message msg1 = mHandler.obtainMessage();
                                msg1.what = PARSE_BARCODE_SUC;
                                msg1.obj = result.getText();
                                mHandler.sendMessage(msg1);
                            }else {
                                Message msg2 = mHandler.obtainMessage();
                                msg2.what = PARSE_BARCODE_FAIL;
                                msg2.obj = "Scan failed!";
                                mHandler.sendMessage(msg2);
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MipcaCapture.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
        mHandler.removeMessages(PARSE_BARCODE_SUC);
        mHandler.removeMessages(PARSE_BARCODE_FAIL);
    }

    /**
     *扫描完成之后将扫描到的结果和二维码的bitmap当初参数传递到
     *handleDecode(Result result, Bitmap barcode)里面
     * @param result
     * @param bitmap
     */
    public void handleDecode(Result result, Bitmap bitmap) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MipcaCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        }else if (resultString.length() <=2){ //说明是菜单的id
            onResultHandlerNew(resultString);
        }else {
            onResultHandler(resultString, bitmap);
        }
        MipcaCapture.this.finish();
    }

    /**
     * 将扫描的结果传到下一个界面
     * @param resultString
     * @param bitmap
     */
    private void onResultHandler(String resultString, Bitmap bitmap){
        if(TextUtils.isEmpty(resultString)){
            Toast.makeText(MipcaCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent resultIntent = new Intent(this, QRCodeResult.class);
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        bundle.putParcelable("bitmap", bitmap);
        resultIntent.putExtras(bundle);
        startActivity(resultIntent);
        MipcaCapture.this.finish();
    }

    /**
     * 将扫描的结果传到启动此活动的界面--MainActivity
     * @param resultString
     */
    private void onResultHandlerNew(String resultString){
        if(TextUtils.isEmpty(resultString)){
            Toast.makeText(MipcaCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        intent.putExtras(bundle);
        setResult(0x55, intent);
        MipcaCapture.this.finish();
    }

    private MyHandler mHandler = new MyHandler(MipcaCapture.this);

    /**
     * 从相册解析二维码用得到
     */
    private static class MyHandler extends Handler{
        private WeakReference<MipcaCapture> weakReference;
        public MyHandler(MipcaCapture activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MipcaCapture activity = weakReference.get();
            if (activity != null){
                activity.progressDialog.dismiss();
                switch (msg.what){
                    case PARSE_BARCODE_SUC: //解析成功后
                        activity.onResultHandler((String)msg.obj, scanBitmap);
                        break;
                    case PARSE_BARCODE_FAIL: //解析失败后
                        activity.progressDialog.dismiss();
                        Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                }
            }else {
                return;
            }
        }
    }

    /**
     * 扫描二维码图片的方法，根基图片路径
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (com.google.zxing.FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


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