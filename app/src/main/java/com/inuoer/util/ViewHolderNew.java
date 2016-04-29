package com.inuoer.util;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuoer.wemall.R;

/**这样定义的ViewHolder可以在getView()方法中不用写findViewById()
 * Created by wuxiangkun on 2016/4/26 22:17.
 * Contacts wuxiangkun@live.com
 */
public class ViewHolderNew {
    private ImageView image;
    private TextView name;
    private TextView price;
    private ImageButton plusButton;
    private ImageButton minusButton;
    private TextView numTextView;
    private TextView summary;

    private View mConvertView;
    public ViewHolderNew(View convertView){
        this.mConvertView = convertView;
    }

    public ImageView getImage(){
        if (image == null){
            image = (ImageView) mConvertView.findViewById(R.id.itemlist_image);
        }
        return image;
    }

    public TextView getName(){
        if (name == null){
            name = (TextView) mConvertView.findViewById(R.id.itemlist_shopname);
        }
        return name;
    }

    public TextView getPrice(){
        if (price == null){
            price = (TextView) mConvertView.findViewById(R.id.waimai_shopmenu_adapter_item_price);
        }
        return price;
    }

    public ImageButton getPlusButton() {
        if (plusButton == null){
            plusButton = (ImageButton) mConvertView.findViewById(R.id.plus_btn);
        }
        return plusButton;
    }

    public ImageButton getMinusButton() {
        if (minusButton == null){
            minusButton = (ImageButton) mConvertView.findViewById(R.id.minus_btn);
        }
        return minusButton;
    }

    public TextView getNumTextView() {
        if (numTextView == null){
            numTextView = (TextView) mConvertView.findViewById(R.id.count);
        }
        return numTextView;
    }

    public TextView getSummary() {
        if (summary == null){
            summary = (TextView) mConvertView.findViewById(R.id.summary);
        }
        return summary;
    }
}
