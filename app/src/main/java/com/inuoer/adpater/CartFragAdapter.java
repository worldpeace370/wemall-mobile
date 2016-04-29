package com.inuoer.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuoer.manager.ObserverManager;
import com.inuoer.util.AsyncImageLoader;
import com.inuoer.util.CartData;
import com.inuoer.util.ViewHolder;
import com.inuoer.wemall.R;

import java.util.List;
import java.util.Map;

/**购物车Fragment的 ListView的适配器，当选好物品之后，会在布局中间显示ListView，用到的适配器
 * Created by wuxiangkun on 2016/4/29 10:59.
 * Contacts wuxiangkun@live.com
 */
public class CartFragAdapter extends BaseAdapterHelper<Map<String, Object>>{
    private Context mContext;
    private Float sum = 0f;
    private Boolean flag = true;
    public CartFragAdapter(List<Map<String, Object>> list, Context context) {
        super(list, context);
        this.mContext = context;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewGroup parent, final List<Map<String, Object>> list, LayoutInflater inflater) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.itemlist_shop, null);
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
            convertView.setTag(holder);// 绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
        }
        new AsyncImageLoader(mContext).downloadImage(list.get(position).get("image").toString(),
                new AsyncImageLoader.ImageCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                        holder.image.setImageBitmap(bitmap);
                    }
                });
        holder.name.setText(list.get(position).get("name")
                .toString());
        holder.price.setText(list.get(position).get("price")
                .toString());
        holder.numTextView.setText(list.get(position).get("num")
                .toString());
        if (flag) {
            //在每个item绘制完成之前计算总价，当绘制完成时，不再计算
            sum = sum + Float.parseFloat(list.get(position).get("price").toString())*Float.parseFloat(list.get(position).get("num").toString());
        }
        //所有item绘制结束后，停止对sum的计算，将sum值发送到实现了Observer接口的类中
        if (getCount()-1 == position){
            flag = false;
            //价格计算完成，发送消息，通知总价更新
            ObserverManager.getObserverManager().sendMessage(sum);
        }
        //点击增加数目
        holder.plusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.numTextView.getText()
                        .toString()) + 1;
                holder.numTextView.setText(String.valueOf(num));

                CartData.editCart(list.get(position).get("id").toString(),
                        list.get(position).get("name").toString(),
                        list.get(position).get("price").toString(),
                        String.valueOf(num),
                        list.get(position).get("image").toString());
                sum = sum + Float.parseFloat(list.get(position).get("price").toString());
                //价格计算完成，发送消息，通知总价更新
                ObserverManager.getObserverManager().sendMessage(sum);
            }

        });
        holder.minusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.numTextView.getText()
                        .toString()) - 1;
                if (num >= 0) {
                    holder.numTextView.setText(String.valueOf(num));
                    String price = list.get(position).get("price").toString();
                    if (num == 0) {
                        CartData.removeCart(list.get(position).get("id").toString());
                        notifyDataSetChanged();
                    } else {
                        CartData.editCart(list.get(position).get("id").toString(),
                                list.get(position).get("name").toString(),
                                list.get(position).get("price").toString(),
                                String.valueOf(num),
                                list.get(position).get("image").toString());
                    }
                    sum = sum - Float.parseFloat(price);
                    //价格计算完成，发送消息，通知总价更新
                    ObserverManager.getObserverManager().sendMessage(sum);
                }
            }
        });
        return convertView;
    }
}
