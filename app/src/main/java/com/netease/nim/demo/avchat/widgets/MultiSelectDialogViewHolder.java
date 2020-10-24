package com.netease.nim.demo.avchat.widgets;

import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.adapter.TViewHolder;

public class MultiSelectDialogViewHolder extends TViewHolder {

	private TextView textView;
	private ImageView imageView;


	@Override
	protected int getResId() {
		return R.layout.multi_select_dialog_list_item;
	}

	@Override
	protected void inflate() {
		textView = (TextView) view.findViewById(R.id.select_dialog_text_view);
		imageView = (ImageView) view.findViewById(R.id.select_dialog_image_view);
	}

	@Override
	protected void refresh(Object item) {
        if(item instanceof Pair<?,?>){
            Pair<String,Boolean> pair = (Pair<String, Boolean>) item;
            textView.setText(pair.first);
			imageView.setPressed(pair.second);
        }
	}

}
