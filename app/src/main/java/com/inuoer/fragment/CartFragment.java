package com.inuoer.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.adpater.CartFragAdapter;
import com.inuoer.manager.ObserverManager;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.wemall.EditAddressActivity;
import com.inuoer.wemall.LoginActivity;
import com.inuoer.wemall.OrderActivity;
import com.inuoer.wemall.R;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Observable;
import java.util.Observer;

/**
 * 所选物品信息添加到ListView中去，还有mHeadView，mFootView作为ListView的头和尾
 */
public class CartFragment extends Fragment implements OnClickListener ,Observer{
	private Intent intent;
	private LayoutInflater inflater;
	//添加到ListView的头部，主要包括个人信息地址，添加地址，付款方式，备注，配送费等，地址不匹配默认隐藏，需要发票默认隐藏
	private LinearLayout mHeadView;
	//添加到ListView的根布局，主要包括总量信息，确认下单等布局
	private LinearLayout mFootView;
	//添加备注时 备注对话框的布局
	private LinearLayout mMarkDialogLayout;
	private Dialog mMarkDialog;
	private ListView mListView;
	private SharedPreferences sharedpreferences;
	private String username , uid;
	private TextView usernametv,phonetv,addresstv;
	private long lastClick = 0;
	private Boolean addressFlag = false;
	private Context mContext;
	private TextView summary;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				
			}else if(msg.what == 0x124){
				Toast.makeText(getActivity(), "订单提交成功", Toast.LENGTH_SHORT).show();
				CartData.removeAllCart();
				mCartFragAdapter.notifyDataSetChanged();
				intent = new Intent(getActivity(), OrderActivity.class);
				startActivity(intent);
			}else {
				mHeadView.findViewById(R.id.confirmorder_address_empty).setVisibility(View.GONE);
				mHeadView.findViewById(R.id.confirmorder_address_full).setVisibility(View.VISIBLE);
				
				addressFlag = true;

				usernametv.setText(msg.getData().get("username").toString());
				phonetv.setText(msg.getData().get("phone").toString());
				addresstv.setText(msg.getData().get("address").toString());

			}
		}
	};
	private String TAG = "CartFragment";
	private CartFragAdapter mCartFragAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		mHeadView = (LinearLayout) inflater.inflate(R.layout.confirmorder_content, null);
		//新增地址线性布局，没有登录的话点击后跳转到选择登录，登陆后隐藏该布局
		mHeadView.findViewById(R.id.confirmorder_address_add).setOnClickListener(this);
		//用户登陆后显示的个人地址栏线性布局，点击后跳转到更改地址页面
		mHeadView.findViewById(R.id.confirmorder_address_full)
				.setOnClickListener(this);
		//点击 备注，弹出备注对话框
		mHeadView.findViewById(R.id.confirmorder_userinfo_remarks).setOnClickListener(
				this);
		usernametv = (TextView) mHeadView.findViewById(R.id.confirmorder_address_username);
		phonetv = (TextView) mHeadView.findViewById(R.id.confirmorder_address_userphone);
		addresstv = (TextView) mHeadView.findViewById(R.id.confirmorder_address_addrdesc);
		
		mFootView = (LinearLayout) inflater.inflate(R.layout.confirmorder_footer, null);
		//点击 确认下单 的监听事件
		mFootView.findViewById(R.id.submit).setOnClickListener(this);
		//商品总价summary,通过观察者模式，在artFragAdapter中进行发送消息来更新
		summary = (TextView) mFootView.findViewById(R.id.summary);

		mListView = new ListView(mContext);
		mListView.addHeaderView(mHeadView);
		mListView.addFooterView(mFootView);

		mCartFragAdapter = new CartFragAdapter(CartData.getCartList(), mContext);
		mListView.setAdapter(mCartFragAdapter);
		
		sharedpreferences = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		username = sharedpreferences.getString("username", "");//如果取不到，则取默认值参数""
		uid = sharedpreferences.getString("uid", "");
		//如果用户已经登录过了，执行下面，向服务器post方式提交用户uid，然后再下载用户地址和手机号等信息，填充到confirmorder_address_full布局中
		if (!username.isEmpty()) {//isEmpty():  Returns true if the length of this string is 0.
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						String result = HttpUtil.getPostJsonContent(Config.API_DO_ADDRESS + "?uid=" + uid + "&do=1");
						Log.i(TAG, "result :" + result + "uid :" + uid + "username :" + username);
						if (!TextUtils.isEmpty(result)) {
							JSONObject jsonObject = JSONObject.parseObject(result);
							
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putString("username", jsonObject.getString("username"));
							data.putString("phone", jsonObject.getString("phone"));
							data.putString("address", jsonObject.getString("address"));
							msg.setData(data);
							if (jsonObject.getString("address").isEmpty()) {
								handler.sendEmptyMessage(0x123);
							}else{
								handler.sendMessage(msg);
							}
						}
						
					} catch (Exception e) {

					}
				}
			}).start();
		}
		
		return mListView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//新增地址点击事件
		case R.id.confirmorder_address_add:
			if (!TextUtils.isEmpty(username)) {//如果用户没有登录，则为true
				intent = new Intent(mContext, EditAddressActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
				createDialog();
			}
			break;
		//用户登陆后显示的个人地址栏线性布局，点击后跳转到更改地址页面
		case R.id.confirmorder_address_full:
			intent = new Intent(mContext, EditAddressActivity.class);
			startActivityForResult(intent, 0);
			break;
		//点击 备注，弹出备注对话框
		case R.id.confirmorder_userinfo_remarks:
			mMarkDialogLayout = (LinearLayout) inflater.inflate(
					R.layout.dialog_remark, null);
			mMarkDialog = new Dialog(mContext);
			mMarkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mMarkDialog.setContentView(mMarkDialogLayout);
			mMarkDialog.show();

			mMarkDialogLayout.findViewById(R.id.dialog_button_right).setOnClickListener(this);
			mMarkDialogLayout.findViewById(R.id.dialog_button_left).setOnClickListener(this);
			break;
		//备注对话框 右键事件
		case R.id.dialog_button_right:
			mMarkDialog.dismiss();
			break;
		//备注对话框 左键事件
		case R.id.dialog_button_left:
			mMarkDialog.dismiss();

			EditText remarks = (EditText) mMarkDialogLayout
					.findViewById(R.id.remarks_inputer);
			TextView textView = (TextView) mHeadView
					.findViewById(R.id.confirmorder_userinfo_remarks);
			textView.setText(remarks.getText());
			break;
		//点击 确认下单 的监听事件
		case R.id.submit:
			if (System.currentTimeMillis() - lastClick >= 1000){
	        	lastClick = System.currentTimeMillis(); 
				if (!TextUtils.isEmpty(username)) {
					if (addressFlag) {
						if (CartData.getCartCount() != 0) {
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									try {
										//提交备注信息和购物车信息到服务器
										TextView notetv = (TextView) mHeadView.findViewById(R.id.confirmorder_userinfo_remarks);
										String result = HttpUtil.getPostJsonContent(Config.API_DO_ORDER + "?uid=" + uid + "&cartdata="
												+ URLEncoder.encode(JSONArray.toJSON(CartData.getCartList()).toString()) +
												"&note=" + URLEncoder.encode(notetv.getText().toString()) + "&do=1");
										if (!TextUtils.isEmpty(result)) {
											handler.sendEmptyMessage(0x124);
										}
									} catch (Exception e) {

									}
								}
							}).start();
						}else{
							Toast.makeText(getActivity(), "请选择商品", Toast.LENGTH_SHORT).show();
						}
					}else {
						Toast.makeText(getActivity(), "请添加地址", Toast.LENGTH_SHORT).show();
					}
					
				}else{
					Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
				}
			}
			
			break;
		default:
			break;
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = getActivity();
		//加入观察者队列，如果有消息被发送，会执行Observer接口的update()方法
		ObserverManager.getObserverManager().addObserver(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		ObserverManager.getObserverManager().deleteObserver(this);
	}

	/**
	 * 如果没有登录，弹出是否进入到登录界面对话框
	 */
	private void createDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("是否去登录？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 当购物车中的所有物品价格总和发生变化时，通知mFootView中的summary TextView发生变化
	 * 总价 共￥
	 * @param observable
	 * @param data
     */
	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof Float){//接受消息，更改总价
			Float sum = (Float) data;
			summary.setText(String.valueOf(summary(sum)));
		}else if (data instanceof String){//更新地址后，接受消息
			String address = (String) data;
			addresstv.setText(address);
		}
	}

	/**
	 * 表明四舍五入，保留两位小数
	 * @param nowSum
	 * @return
	 */
	private Float summary(Float nowSum){
		BigDecimal b = new BigDecimal(nowSum);
		Float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}
}
