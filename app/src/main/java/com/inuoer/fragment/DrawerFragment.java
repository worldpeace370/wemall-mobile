package com.inuoer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inuoer.wemall.R;


/** 抽屉布局的Fragment,继承自Fragment
 * Created by wuxiangkun on 2016/3/31.
 * Contacts by wuxiangkun@live.com
 */
public class DrawerFragment extends Fragment{
    private OnDrawerItemSelectedListener mListener;
    private ListView mDrawerListView;
    //当前ListView的点击位置，默认为0，第一项
    private int mCurrentSelectedPosition = 0;
    public void setOnDrawerItemSelectedListener(OnDrawerItemSelectedListener mListener) {
        this.mListener = mListener;
    }

    public DrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**使此fragment能创建选项菜单，通过接受onCreateOptionsMenu和相关方法的调用
         * Report that this fragment would like to participate in populating the options menu by
         * receiving a call to onCreateOptionsMenu and related methods.
         */
        setHasOptionsMenu(true);
    }

    /**
     * 只返回ListView,抽屉布局的控件
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_drawer, container, false);
        //点击ListView的选项后，通过接口回调，在Activity中切换不同的Fragment
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1);
        Object[] array = getActivity().getResources().getStringArray(R.array.drawer_items);
        adapter.addAll(array);
        mDrawerListView.setDividerHeight(2);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    /**
     * 点击ListView选项调用，或者在MainActivity中调用，来添加默认的Fragment
     * @param position
     */
    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null){
            mDrawerListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            /**
             * Sets the checked state of the specified position. The is only valid if the choice mode
             * has been set to CHOICE_MODE_SINGLE or CHOICE_MODE_MULTIPLE.
             */
            mDrawerListView.setItemChecked(position, true);
        }
        if (mListener != null){
            mListener.onDrawerItemSelected(position);
        }
    }

    /**
     * 自定义抽屉布局接口，用于点击菜单选项后在Activity中接口回调来切换Fragment
     */
    public interface OnDrawerItemSelectedListener{
        void onDrawerItemSelected(int position);
    }
}
