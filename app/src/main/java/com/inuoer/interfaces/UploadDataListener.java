package com.inuoer.interfaces;

/**
 * Created by wuxiangkun on 2016/5/14 15:47.
 * Contacts wuxiangkun@live.com
 */
public interface UploadDataListener {
    /**
     * 成功时回调
     */
    void onSuccess(String result);

    /**
     * 失败时回调
     */
    void onError();
}
