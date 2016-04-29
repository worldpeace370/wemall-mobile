package com.inuoer.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuoer.util.ChineseToPinyinHelper;
import com.inuoer.wemall.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by wuxiangkun on 2016/2/27.
 * Contacts by wuxiangkun@live.com
 */
public class StickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter{
    private String[] countries;
    private LayoutInflater layoutInflater;
    private ChineseToPinyinHelper pinyinHelper;
    public StickyListAdapter(Context context, String[] countries){
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.countries = countries;
        pinyinHelper = ChineseToPinyinHelper.getInstance();
    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @Override
    public Object getItem(int position) {
        return countries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.sticky_list_item_layout, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(countries[position]);
        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (convertView == null){
            headerViewHolder = new HeaderViewHolder();
            convertView = layoutInflater.inflate(R.layout.sticky_list_header_layout, parent, false);
            headerViewHolder.textView = ((TextView) convertView.findViewById(R.id.textView_header));
            convertView.setTag(headerViewHolder);
        }else {
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        }
        String headerText = "" + pinyinHelper.getPinyin(countries[position].substring(0,1)).toUpperCase().charAt(0);
        headerViewHolder.textView.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return pinyinHelper.getPinyin(countries[position].substring(0,2)).toUpperCase().charAt(0);
    }


    class HeaderViewHolder{
        TextView textView;
    }

    class ViewHolder{
        TextView textView;
    }
}
