package com.inuoer.wemall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;

public class OrderActivity extends Activity implements OnClickListener {
	private SharedPreferences sharedpreferences;
	private String uid;
	private List<Map<String, String>> list;
	private Map<String, String> hashmap;
	private SimpleAdapter adapter;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0x123) {
				adapter.notifyDataSetChanged();

			} else {
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order);

		findViewById(R.id.confirmorder_actionbar_left_back).setOnClickListener(
				this);

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");

		ListView lv = (ListView) findViewById(R.id.order_list);

		list = new ArrayList<Map<String, String>>();

		adapter = new SimpleAdapter(this, list,
				R.layout.itemlist_order, new String[] { "order_id",
						"order_status", "pay_status" }, new int[] {
						R.id.order_id, R.id.order_status, R.id.pay_status });

		lv.setAdapter(adapter);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String result = HttpUtil.getPostJsonContent(Config.API_DO_ORDER
							+ "?uid=" + uid + "&do=2");
					if (!result.isEmpty()) {
						JSONArray jsonArray = JSONArray.parseArray(result);
						JSONObject myjObject;
						String order_status , pay_status;
						
						for (int i = 0; i < jsonArray.size(); i++) {
							myjObject = jsonArray.getJSONObject(i);
							hashmap = new HashMap<String, String>();
							hashmap.put("order_id", myjObject.getString("orderid"));
							
							order_status = myjObject.getString("order_status").equals("0")?"未发货":"已发货";
							pay_status = myjObject.getString("pay_status").equals("0")?"未付款":"已付款";
							
							hashmap.put("order_status", order_status);
							hashmap.put("pay_status", pay_status);
							list.add(hashmap);
						}

						handler.sendEmptyMessage(0x123);
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String order_id = list.get(position).get("order_id").toString();
				Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirmorder_actionbar_left_back:
			finish();
			break;

		default:
			break;
		}
	}

}
