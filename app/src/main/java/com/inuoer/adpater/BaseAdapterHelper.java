package com.inuoer.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**定义抽象父类，继承自BaseAdapter,通过泛型传递参数，并实现其中几个通用的方法，
 * 再让子类继承，避免的代码的重复
 * Created by wuxiangkun on 2016/3/15.
 * Contacts by wuxiangkun@live.com
 */
public abstract class BaseAdapterHelper<T> extends BaseAdapter{
    private List<T> list;
    private LayoutInflater inflater;
    public BaseAdapterHelper(List<T> list, Context context){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent, list, inflater);
    }

    /**
     * 定义抽象方法，等着子类实现
     * 在getView()中调用该方法
     * @param position
     * @param convertView
     * @param parent
     * @param list 由于为私有属性，所以通过参数传递的方式进行使用
     * @param inflater 由于为私有属性，所以通过参数传递的方式进行使用
     * @return
     */
    public abstract View getItemView(int position, View convertView, ViewGroup parent, List<T> list, LayoutInflater inflater);

    /**将参数data加入list，并更新UI
     * isClear表示了两种情况，true清空后再加入
     * @param data 待加入list的数据
     * @param isClear 是否在清空list之后再加入data
     */
    public void reloadData(List<T> data, boolean isClear){
        if (list != null){  //避免空指针异常，最好加上判断
            if (isClear){
                list.clear();
            }
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 表示清空list里面的数据后，更新UI
     */
    public void clearData(){
        if (list != null){  //避免空指针异常，最好加上判断
            list.clear();
            notifyDataSetChanged();
        }
    }
}
