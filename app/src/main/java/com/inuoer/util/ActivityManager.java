package com.inuoer.util;

import android.app.Activity;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;

/**
 * 作者：$(吴祥坤) on 2015/10/30 19:44
 * 邮箱：337360287@qq.com
 * This class is used for managering all of Activity of this App,
 * when this app is exited,foreach all Activity of this App and finished them,
 * finally successfully to exit.
 * function:addActivity(Activity activity),exit(),getInstance()
 */
public class ActivityManager {
    /**
     * 将App中所有的Activity加入LinkedList<Activity>
     */
    private List<Activity> activityList = new LinkedList<>();
    /**
     * ActivityManager的管理者,单例模式
     */
    private static ActivityManager instance;

    /**
     * 私有的构造方法
     */
    private ActivityManager(){

    }

    /**
     * 静态方法得到ActivityManager的单例
     * @return ActivityManager
     */
    public static ActivityManager getInstance(){
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 往集合里面添加活动
     * @param activity
     */
    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**清除所有观察者(貌似....不需要?)
     * 程序退出时,遍历activityList,销毁所有的活动
     */
    public void exit(){
        for (Activity activity:activityList) {
            if (!activity.isFinishing() && activity!=null) {
                activity.finish();
            }
        }
        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }

    /**
     * 判断版本号是否大于等于19
     * @return
     */
    public static boolean hasKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 判断版本号是否大于等于21
     * @return
     */
    public static boolean hasLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
