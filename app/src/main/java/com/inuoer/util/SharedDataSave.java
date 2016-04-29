package com.inuoer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**SharedPreferences存储是否第一次安装应用
 * Created by wuxiangkun on 2016/4/11 9:22.
 * Contacts wuxiangkun@live.com
 */
public class SharedDataSave {
    /**
     * 初始化SharedPreferences
     * @param context
     */
    public static void init(Context context){
        /** 首先得到SharedPreferences对象,如果名字相同,不管在哪里得到的都是唯一的这个对象
         *  进入页面后，判断用户是不是第一次使用该应用，如果是则跳转到应用的导航页面
         *  不是的话,等待几秒后跳到MainActivity.
         *  Only one instance of the SharedPreferences object is returned to
         *  any callers for the same name, meaning they will see each other's edits as soon as they are made.
         */
        SharedPreferences preferences = context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, 0);
        /**
         * 获取是否是第一次使用的参数
         * preferences.getBoolean()的返回值,如果key存在则返回查到的值,不存在的话,
         * 返回Constants.THE_FIRST自己的默认值
         */
        Config.IS_FIRST = preferences.getBoolean(Config.THE_FIRST_INSTALL,Config.IS_FIRST);
    }

    /**
     * 存储数据键值对
     * @param context
     * @param key
     * @param value
     */
    public static void save(Context context, String key, Object value) {
        SharedPreferences preferences = context.getSharedPreferences(
                Config.SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        /**
         * 提交修改
         */
        editor.commit();
    }

    /**
     * SharedPreferences保存数据
     * @param context
     */
    public static void save(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Config.SHARED_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        /**
         * 提交修改
         */
        editor.commit();
    }
}
