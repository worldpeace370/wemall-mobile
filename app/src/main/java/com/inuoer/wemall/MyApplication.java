package com.inuoer.wemall;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**在清单文件中 application中添加name属性，指定当前MyApplication
 * 在应用中得到唯一的一个Volley请求队列
 * Created by wuxiangkun on 2016/5/14 15:53.
 * Contacts wuxiangkun@live.com
 */
public class MyApplication extends Application{
    private static RequestQueue requestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
