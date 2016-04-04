package com.inuoer.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.util.MainAdapter;

public class MainFragment extends Fragment{
	private String jsonString;
	private ListView lv;
	private MainAdapter MyAdapter;
	private ArrayList<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				MyAdapter.notifyDataSetChanged();
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "请查看网络连接", Toast.LENGTH_LONG)
						.show();
			}
		}
	};
	
	
	public MainFragment(ArrayList<Map<String, Object>> listItem , MainAdapter MyAdapter) {
//		super();
		// TODO Auto-generated constructor stub
		this.listItem = listItem;
		this.MyAdapter = MyAdapter;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		lv = new ListView(getActivity());
//		listItem = new ArrayList<Map<String, Object>>();
		lv.setAdapter(MyAdapter);// 为ListView绑定Adapter

//		lv.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView arg0, int arg1) {
//				// TODO Auto-generated method stub
//				switch (arg1) {
//				case OnScrollListener.SCROLL_STATE_IDLE:
//					 // 判断滚动到底部
//				    if (lv.getLastVisiblePosition() == (lv.getCount() - 1)) {
//						progressDialog = new ProgressDialog(getActivity());
//						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//						progressDialog.setMessage("正在加载中...");
//						progressDialog.setCancelable(true);
//						progressDialog.show();
//						initData();
//						
//						listItem.clear();
//				    }
//					break;
//
//				default:
//					break;
//				}
//			}
//			
//			@Override
//			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		return lv;
	}
	
	public void initData() {
		new Thread(new Runnable() {
			public void run() {
				try {
					jsonString = HttpUtil
							.getGetJsonContent(Config.API_GET_GOODS);

					JSONArray jsonArr = JSON.parseArray(jsonString);
					for (int i = 0; i < jsonArr.size(); i++) {
						// 获取每一个JsonObject对象
						JSONObject myjObject = jsonArr.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", i);
						//new GetBitmapUtil().getBitmapByUrl(Config.API_UPLOADS+myjObject.getString("image"));
						map.put("image", Config.API_UPLOADS+myjObject.getString("image"));
						map.put("name", myjObject.getString("name"));
						map.put("price", myjObject.getString("price"));
						map.put("num", CartData.findCart(i));
						listItem.add(map);
					}
//
					handler.sendEmptyMessage(0x123);
//					progressDialog.dismiss();
				} catch (Exception e) {
					// TODO: handle exception
//					System.out.println(e);
					handler.sendEmptyMessage(0x124);
					// System.out.println("请检查网络连接");
				}

			}
		}).start();

	}

}
