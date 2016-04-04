package com.inuoer.wemall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;

public class OrderDetailActivity extends Activity implements OnClickListener {
	private String order_id;
	private SharedPreferences sharedpreferences;
	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detail);
		
		order_id = getIntent().getStringExtra("order_id");
 
		findViewById(R.id.actionbar_left_back).setOnClickListener(this);
		
		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");
		
		Task task = new Task();
		task.execute( Config.API_DO_ORDER+"?orderid="+order_id+"&uid="+uid+"&do=3" );
		
	}

	class Task extends AsyncTask<String, Integer, String>{
		public TextView order_detail_username, order_detail_tel,
				order_detail_address, order_detail_note,
				order_detail_total_price, order_detail_order_status,
				order_detail_pay_style, order_detail_pay_status;
		public List<Map<String, String>> list;
		public Map<String, String> hashmap;
		public SimpleAdapter adapter;
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String result = HttpUtil.getPostJsonContent(params[0]);
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			JSONObject jsonObject = JSONObject.parseObject(result);
		
			order_detail_username = (TextView) findViewById(R.id.order_detail_username);
			order_detail_tel = (TextView) findViewById(R.id.order_detail_tel);
			order_detail_address = (TextView) findViewById(R.id.order_detail_address);
			order_detail_note = (TextView) findViewById(R.id.order_detail_note);
			order_detail_total_price = (TextView) findViewById(R.id.order_detail_total_price);
			order_detail_order_status = (TextView) findViewById(R.id.order_detail_order_status);
			order_detail_pay_style = (TextView) findViewById(R.id.order_detail_pay_style);
			order_detail_pay_status = (TextView) findViewById(R.id.order_detail_pay_status);
			
			String order_status = jsonObject.getString("order_status").equals("0")?"未发货":"已发货";
			String pay_status = jsonObject.getString("pay_status").equals("0")?"未付款":"已付款";
			
			order_detail_note.setText(jsonObject.getString("note"));
			order_detail_total_price.setText(jsonObject.getString("totalprice"));
			order_detail_order_status.setText(order_status);
			order_detail_pay_style.setText(jsonObject.getString("pay_style"));
			order_detail_pay_status.setText(pay_status);
			order_detail_username.setText(jsonObject.getString("username"));
			order_detail_tel.setText(jsonObject.getString("phone"));
			order_detail_address.setText(jsonObject.getString("address"));
			
			list = new ArrayList<Map<String, String>>();
			JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("cartdata"));
			JSONObject myjObject;
			
			for (int i = 0; i < jsonArray.size(); i++) {
				myjObject = jsonArray.getJSONObject(i);
				hashmap = new HashMap<String, String>();
				hashmap.put("name", myjObject.getString("name"));
				hashmap.put("num", myjObject.getString("num"));
				hashmap.put("price", myjObject.getString("price"));
				list.add(hashmap);
			}
			
			ListView lv = (ListView) findViewById(R.id.order_detail_listview);
			adapter = new SimpleAdapter(OrderDetailActivity.this, list,
					R.layout.activity_order_detail_append, new String[] {
							"name", "num", "price" }, new int[] {
							R.id.order_detail_name, R.id.order_detail_num,
							R.id.order_detail_price });
			lv.setAdapter(adapter);
			fixListViewHeight(lv);
		}

		//ScrollView中listview的高度没法适配,所以必须通过代码计算出高度
		private void fixListViewHeight(ListView listView) {
			// TODO Auto-generated method stub
		      // 如果没有设置数据适配器，则ListView没有子项，返回。  
	        ListAdapter listAdapter = listView.getAdapter();  
	        int totalHeight = 0;   
	        if (listAdapter == null) {   
	            return;   
	        }   
	        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {     
	            View listViewItem = listAdapter.getView(i , null, listView);  
	            // 计算子项View 的宽高   
	            listViewItem.measure(0, 0);    
	            // 计算所有子项的高度和
	            totalHeight += listViewItem.getMeasuredHeight();    
	        }   
	   
	        ViewGroup.LayoutParams params = listView.getLayoutParams();   
	        // listView.getDividerHeight()获取子项间分隔符的高度   
	        // params.height设置ListView完全显示需要的高度    
	        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
	        listView.setLayoutParams(params);   
		}
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.actionbar_left_back:
			finish();
			break;

		default:
			break;
		}
	}
}
