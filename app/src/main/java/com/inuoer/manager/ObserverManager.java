package com.inuoer.manager;

import java.util.Observable;

/**观察者，用来观察一些操作，如主题颜色的改变，何时弹出更新的窗口等等
 * created by：$(吴祥坤) on 2015/11/3 10:40
 * email：337360287@qq.com
 * Observable is used to notify a group of Observer objects when a change occurs.
 * On creation, the set of observers is empty. After a change occurred,
 * the application can call the notifyObservers() method.
 * This will cause the invocation of the update() method of all registered Observers.
 * The order of invocation is not specified. This implementation will call the Observers
 * in the order they registered. Subclasses are completely free in what order they call the update methods.
 */
public class ObserverManager extends Observable{
    private static ObserverManager observerManager = null;

    /**
     * 单例模式,此App里面有且只有一个唯一的ObserverManager对象
     * @return
     */
    public static ObserverManager getObserverManager(){
        if (observerManager == null) {
            observerManager = new ObserverManager();
        }
        return observerManager;
    }

    /**
     * 私有构造方法
     */
    private ObserverManager(){

    }

    /**
     *  After a change occurred,the application can call the notifyObservers() method,
     *  This will cause the invocation of the update() method of all registered Observers.
     * @param object
     */
    public void sendMessage(Object object){
        observerManager.setChanged();//执行之后,hasChanged() returns true
        /**
         * If hasChanged() returns true, calls the update() method for every Observer
         * in the list of observers using the specified argument.
         * Afterwards calls clearChanged().
         */
        observerManager.notifyObservers(object);
    }
}
