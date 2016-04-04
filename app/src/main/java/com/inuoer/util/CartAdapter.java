package com.inuoer.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inuoer.util.AsyncImageLoader.ImageCallback;
import com.inuoer.wemall.R;

public class CartAdapter extends BaseAdapter {

	private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
	private ArrayList<Map<String, Object>> getDate;
	private Context mContext;
	private LinearLayout ll_footer;
	private Float sum = 0f;
	private Boolean flag = true;
	
	public CartAdapter(Context context , ArrayList<Map<String, Object>> getDate , LinearLayout ll_footer) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.getDate = getDate;
		this.ll_footer = ll_footer;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getDate.size();// 返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return getDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.itemlist_shop, null);
			holder.image = (ImageView) convertView
					.findViewById(R.id.itemlist_image);
			holder.name = (TextView) convertView
					.findViewById(R.id.itemlist_shopname);
			holder.price = (TextView) convertView
					.findViewById(R.id.waimai_shopmenu_adapter_item_price);
			holder.plusButton = (ImageButton) convertView
					.findViewById(R.id.plus_btn);
			holder.minusButton = (ImageButton) convertView
					.findViewById(R.id.minus_btn);
			holder.numTextView = (TextView) convertView
					.findViewById(R.id.count);
			holder.summary = (TextView) ll_footer.findViewById(R.id.summary);
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		new AsyncImageLoader(mContext).downloadImage(getDate.get(position).get("image").toString(),
				new ImageCallback() {
					@Override
					public void onImageLoaded(Bitmap bitmap, String imageUrl) {
						// TODO Auto-generated method stub
						holder.image.setImageBitmap(bitmap);
					}
				});
		holder.name.setText(getDate.get(position).get("name")
				.toString());
		holder.price.setText(getDate.get(position).get("price")
				.toString());
		holder.numTextView.setText(getDate.get(position).get("num")
				.toString());
		if (flag) {
			//通知数据改变之后，不让他再次计算sum的值
			sum = sum + Float.parseFloat(getDate.get(position).get("price").toString())*Float.parseFloat(getDate.get(position).get("num").toString());
		}
		
		if (position == (getCount()-1)) {
				holder.summary.setText( String.valueOf(summary(sum)) );
				flag = false;
		}

		holder.plusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int num = Integer.parseInt(holder.numTextView.getText()
						.toString()) + 1;
				holder.numTextView.setText(String.valueOf(num));
				
//				getDate.get(position).put("num", num);
				CartData.editCart(getDate.get(position).get("id").toString(),
						getDate.get(position).get("name").toString(),
						getDate.get(position).get("price").toString(),
						String.valueOf(num),
						getDate.get(position).get("image").toString());
				sum = sum + Float.parseFloat(getDate.get(position).get("price").toString());
				holder.summary.setText(String.valueOf(summary(sum)));
//				notifyDataSetChanged();
			}
			
		});
		holder.minusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int num = Integer.parseInt(holder.numTextView.getText()
						.toString()) - 1;
				if (num >= 0) {
					holder.numTextView.setText(String.valueOf(num));
					String price = "";
					if (num == 0) {
						//notifyDataSetChanged();当adapter（适配器）中的数据有改变时，通知adapter用getView()来更新界面中的每个item。
//						getDate.remove(position);
						price = getDate.get(position).get("price").toString();
						CartData.removeCart(getDate.get(position).get("id").toString());
						notifyDataSetChanged();
					} else {
//						getDate.get(position).put("num", num);//notifyDataSetChanged();
						CartData.editCart(getDate.get(position).get("id").toString(),
								getDate.get(position).get("name").toString(),
								getDate.get(position).get("price").toString(),
								String.valueOf(num),
								getDate.get(position).get("image").toString());
						price = getDate.get(position).get("price").toString();
					}
					
					sum = sum - Float.parseFloat(price);
					holder.summary.setText(String.valueOf(summary(sum)));
				}
			}
		});
		return convertView;
	}
	public Float summary(Float nowsum){
		BigDecimal b = new BigDecimal(nowsum); 
		//表明四舍五入，保留两位小数 
		Float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue(); 
		return f1;
	}

}
