package com.inuoer.util;

import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * 下载图片类
 * @author heqing
 *
 */
public class GetBitmapUtil {

	public Bitmap getBitmapByUrl(String url){
		Bitmap bitmap = null;
		try {
			URL url2=new URL(url);
			HttpURLConnection httpURLConnection=(HttpURLConnection) url2.openConnection();
		    httpURLConnection.setReadTimeout(3000);
		    int code=httpURLConnection.getResponseCode();
		    if(code==200){
		    	bitmap=BitmapFactory.decodeStream(httpURLConnection.getInputStream());
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
