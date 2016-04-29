package com.inuoer.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * 通过Http请求获取json字符串
 * @author heqing
 *
 */

public class HttpUtil {

	public HttpUtil() {

	}

	/**
	 * 根据url获取Get请求的json字符串
	 * @param urlStr
	 * @return
     */
	public static String getGetJsonContent(String urlStr) {
		
		try {// 获取HttpURLConnection连接对象
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			// 设置连接属性
			httpConn.setConnectTimeout(3000);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			// 获取相应码
			int respCode = httpConn.getResponseCode();
			if (respCode == 200) {
				return ConvertStream2Json(httpConn.getInputStream());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据url获取Post请求的json字符串
	 * @param urlStr
	 * @return
     */
	public static String getPostJsonContent(String urlStr){
		try {
			String[] urlArr = urlStr.split("\\?");
			urlStr = urlArr[0];
			
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	
			// 因为这个是post请求，设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");
			// Post请求不能使用缓存
			urlConn.setUseCaches(false);
			urlConn.setInstanceFollowRedirects(true);
			// 配置本次连接的Content-type,配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Contet-Type",
					"application/x-www-form-urlencoded");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			urlConn.connect();
			
			// DataOutputStream流
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
			// 要上传的参数
			String content = urlArr[1];
			// 将要上传的内容写入流中
			out.writeBytes(content);
			// 刷新、关闭
			out.flush();
			out.close();
	
			// 获取相应码
			int respCode = urlConn.getResponseCode();
			if (respCode == 200) {
				return ConvertStream2Json(urlConn.getInputStream());
			}
		} catch (Exception e) {
		}
		return null;
	}

	private static String ConvertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		// ByteArrayOutputStream相当于内存输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
}
