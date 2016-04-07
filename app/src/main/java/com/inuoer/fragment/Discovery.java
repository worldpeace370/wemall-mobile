package com.inuoer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.inuoer.wemall.R;
import com.inuoer.wemall.ShakeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Discovery extends Fragment {

    private List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
    private Context mContext;
    private int arrImageLeft[] = new int[]{R.drawable.ic_mine_activity_myqb,R.drawable.ic_mine_activity_collection
            ,R.drawable.shake_phone, R.drawable.ic_mine_activity_group,R.drawable.ic_mine_activity_baoming,
            R.drawable.ic_mine_activity_app_commend};
    public Discovery() {

    }

    /**
     * 初始化discovery_fragment页面的ListView里面的数据...
     */
    private List<Map<String, Object>> initListViewData(){
        List<Map<String, Object>> list = new ArrayList<>();
        String arrTitle [] = getResources().getStringArray(R.array.arr_mine_fragment_title);
        int count = arrImageLeft.length;
        for (int i = 0; i < count; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("imageIdLeft", arrImageLeft[i]);
            map.put("itemTitle", arrTitle[i]);
            //资源数组里面的最后一张图片
            map.put("imageIdRight", R.drawable.icon_right_arrow);
            list.add(map);
        }
        return list;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalList.addAll(initListViewData());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView_discovery);
        SimpleAdapter adapter = new SimpleAdapter(mContext, totalList, R.layout.item_discovery_listview
                , new String[]{"imageIdLeft","itemTitle","imageIdRight"}, new int[]{R.id.imageView_mine_left,
                R.id.textView_mine_info,R.id.imageView_mine_right});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2){ //启动摇一摇
                    Intent intent = new Intent(mContext, ShakeActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

}
