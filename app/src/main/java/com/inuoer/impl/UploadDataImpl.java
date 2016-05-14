package com.inuoer.impl;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.inuoer.interfaces.UploadDataListener;
import com.inuoer.interfaces.UploadInterface;
import com.inuoer.wemall.MyApplication;

import java.util.Map;

/**上传数据的实现类，这里用Volley框架来实现。
 * Created by wuxiangkun on 2016/5/14 15:51.
 * Contacts wuxiangkun@live.com
 */
public class UploadDataImpl implements UploadInterface{
    private Context context;

    public UploadDataImpl(Context context) {
        this.context = context;
    }

    @Override
    public void uploadData(String url, final Map map, final UploadDataListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }){
            //post方式提交Map数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        //设置取消取消http请求标签 Activity的生命周期中的onStop()中调用
        request.setTag("volleyPost");
        //将当前请求加入请求队列,不要忘了INTERNET权限
        MyApplication.getRequestQueue().add(request);
    }
}
