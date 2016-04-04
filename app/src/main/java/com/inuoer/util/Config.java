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
	public static final String PHONE = "10086";
	
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
}
