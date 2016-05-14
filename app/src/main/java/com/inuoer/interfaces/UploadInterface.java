package com.inuoer.interfaces;

import java.util.Map;

/**上传数据的接口，由什么方法实现根据个人爱好
 * 这里定义这样定义，可以有许多实现类，根据需要用哪种就行了
 * Created by wuxiangkun on 2016/5/14 15:46.
 * Contacts wuxiangkun@live.com
 */
public interface UploadInterface {
    /**
     * 将map数据上传至服务器，
     * @param url 服务器url地址
     * @param map 待上传的数据
     * @param listener 上传成功或者失败后的接口回调
     */
    void uploadData(String url, Map map,  UploadDataListener listener);
}
