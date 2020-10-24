package com.netease.nim.demo.session.viewholder;

import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RTSAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

public class MsgViewHolderRTS extends MsgViewHolderBase {

    private TextView textView;

    public MsgViewHolderRTS(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_rts;
    }

    @Override
    protected void inflateContentView() {
        textView = (TextView) view.findViewById(R.id.rts_text);
    }

    @Override
    protected void bindContentView() {
        RTSAttachment attachment = (RTSAttachment) message.getAttachment();
        textView.setText(attachment.getContent());
    }

    @Override
    protected int leftBackground() {
        return R.drawable.chat_from_bg_normal;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.chat_to_bg_normal;
    }
}

