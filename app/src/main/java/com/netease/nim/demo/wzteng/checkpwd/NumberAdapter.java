package com.netease.nim.demo.wzteng.checkpwd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;

public class NumberAdapter extends BaseAdapter {

    private String mNumbers = "123456789C0#";
    private Context mContext;

    public NumberAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mNumbers.length();
    }

    @Override
    public String getItem(int position) {
        return String.valueOf(mNumbers.charAt(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            itemHolder = new ItemHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.checkpwd_item_view_input_group_code, null);
            itemHolder.mRootView = (RelativeLayout) convertView.findViewById(R.id.number_root_view);
            itemHolder.mNumberTextView = (TextView) convertView.findViewById(R.id.number_textView);
            itemHolder.mDeleteImageView = (ImageView) convertView.findViewById(R.id.number_delete_imageView);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        String curNumber = getItem(position);
        if ("C".equals(curNumber)) {
            itemHolder.mDeleteImageView.setVisibility(View.GONE);
            itemHolder.mNumberTextView.setVisibility(View.VISIBLE);
            itemHolder.mNumberTextView.setText(curNumber);
            itemHolder.mRootView.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
        } else if ("#".equals(curNumber)) {
            itemHolder.mRootView.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            itemHolder.mNumberTextView.setVisibility(View.GONE);
            itemHolder.mDeleteImageView.setVisibility(View.VISIBLE);
        } else {
            itemHolder.mRootView.setBackgroundResource(R.drawable.checkpwd_list_selector);
            itemHolder.mDeleteImageView.setVisibility(View.GONE);
            itemHolder.mNumberTextView.setVisibility(View.VISIBLE);
            itemHolder.mNumberTextView.setText(curNumber);
        }
        return convertView;
    }


    private static class ItemHolder {
        RelativeLayout mRootView;
        TextView mNumberTextView;
        ImageView mDeleteImageView;
    }
}
