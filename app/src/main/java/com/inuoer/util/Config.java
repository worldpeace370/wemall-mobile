package com.inuoer.util;

/**
 * 
 * APP_URL APP_PHONE 需要配置
 * 开源wemall android仅支持wemall3.0以上
 * @author heqing
 *
 */
public class Config {
	public static final String APP_KEY = "wemall";
	public static final String APP_URL = "http://115.28.64.78/wemall/";
	public static final String PHONE = "0431-8516XXXX";
	
	public static final String API_GET_GOODS = APP_URL
			+ "index.php/App/Index/appgetgood";
	public static final String API_UPLOADS = APP_URL + "Public/Uploads/";
	public static final String API_REGISTER = APP_URL
			+ "index.php/App/Index/appregister";
	public static final String API_LOGIN = APP_URL
			+ "index.php/App/Index/applogin";
	public static final String API_DO_ADDRESS = APP_URL
			+ "index.php/App/Index/appdoaddress";
	public static final String API_DO_ORDER = APP_URL
			+ "index.php/App/Index/appdoorder";

	/***
	 * ------------------------------------应用配置-----------------------------
	 **/
	/**
	 * 配置文件的名称
	 */
	public static String SHARED_PREFERENCES_NAME = "dun.delicious.food";
	/**
	 * 是否是第一次使用，默认值是true
	 */
	public static String THE_FIRST_INSTALL = "THE_FIRST_INSTALL";
	/**
	 * 是否第一次安装，默认是第一次安装
	 */
	public static boolean IS_FIRST = true;
}
