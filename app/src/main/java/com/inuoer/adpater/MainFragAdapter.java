package com.inuoer.adpater;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inuoer.util.AsyncImageLoader;
import com.inuoer.util.CartData;
import com.inuoer.util.ViewHolder;
import com.inuoer.wemall.R;

import java.util.List;
import java.util.Map;

/** MainFragment的ListView适配器，继承自抽象类，减少重复代码
 *
 * Created by wuxiangkun on 2016/4/29 10:23.
 * Contacts wuxiangkun@live.com
 */
public class MainFragAdapter extends BaseAdapterHelper<Map<String, Object>>{
    private Context mContext;
    public MainFragAdapter(List<Map<String, Object>> list, Context context) {
        super(list, context);
        this.mContext = context;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewGroup parent, final List<Map<String, Object>> list, final LayoutInflater inflater) {
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
        //整个ListView的item的监听事件convertView.setOnClickListener(),没有用ListView的setOnItemClickListener();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框，可以进行购物
                final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_detail, null);
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(layout);
                dialog.show();

                final ImageView imageView = (ImageView) layout.findViewById(R.id.dialog_detail_big_image);
                new AsyncImageLoader(mContext).downloadImage(list.get(position).get("image").toString(), true,
                        new AsyncImageLoader.ImageCallback() {
                            @Override
                            public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });

                TextView textViewPrice = (TextView)layout.findViewById(R.id.dialog_detail_single_price);
                textViewPrice.setText(holder.price.getText().toString());

                TextView textViewName = ((TextView) layout.findViewById(R.id.dialog_detail_title_name));
                textViewName.setText(holder.name.getText().toString());

                TextView textViewNum = (TextView) layout.findViewById(R.id.count);
                textViewNum.setText(holder.numTextView.getText().toString());

                layout.findViewById(R.id.dialog_detail_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                layout.findViewById(R.id.dialog_detail_addcart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final TextView textViewin = (TextView) layout.findViewById(R.id.count);
                layout.findViewById(R.id.plus_btn).setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                    @Override
                    public void onClick(View v) {
                        holder.plusButton.callOnClick();//回调holder.plusButton.setOnClickListener()方法
                        textViewin.setText(holder.numTextView.getText().toString());
                    }
                });
                layout.findViewById(R.id.minus_btn).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        holder.minusButton.callOnClick();
                        textViewin.setText(holder.numTextView.getText().toString());
                    }
                });

            }
        });
        //异步任务下载每个item上面的图片
        new AsyncImageLoader(mContext).downloadImage(list.get(position).get("image").toString(),true,
                new AsyncImageLoader.ImageCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                        holder.image.setImageBitmap(bitmap);
                    }
                });
        //填充每个item上面的name,price等等
        holder.name.setText(list.get(position).get("name")
                .toString());
        holder.price.setText(list.get(position).get("price")
                .toString());
        holder.numTextView.setText(String.valueOf(CartData.findCart(position)));
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

            }
        });
        holder.minusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(holder.numTextView.getText()
                        .toString()) - 1;
                if (num >= 0) {
                    holder.numTextView.setText(String.valueOf(num));
                    if (num == 0) {
                        CartData.removeCart(list.get(position)
                                .get("id").toString());
                    } else {
                        CartData.editCart(list.get(position).get("id").toString(),
                                list.get(position).get("name").toString(),
                                list.get(position).get("price").toString(),
                                String.valueOf(num),
                                list.get(position).get("image").toString());
                    }

                }
            }
        });
        return convertView;
    }
}
