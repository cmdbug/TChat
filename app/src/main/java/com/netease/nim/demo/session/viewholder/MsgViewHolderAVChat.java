package com.netease.nim.demo.session.viewholder;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderAVChat extends MsgViewHolderBase {

    private ImageView typeImage;
    private TextView statusLabel;

    public MsgViewHolderAVChat(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_avchat;
    }

    @Override
    protected void inflateContentView() {
        typeImage = findViewById(R.id.message_item_avchat_type_img);
        statusLabel = findViewById(R.id.message_item_avchat_state);
    }

    @Override
    protected void bindContentView() {
        if (message.getAttachment() == null) {
            return;
        }

        layoutByDirection();

        refreshContent();
    }

    private void layoutByDirection() {
        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();

        if (isReceivedMessage()) {
            if (attachment.getType() == AVChatType.AUDIO) {
                typeImage.setImageResource(R.drawable.avchat_left_type_audio);
            } else {
                typeImage.setImageResource(R.drawable.avchat_left_type_video);
            }
//            statusLabel.setTextColor(context.getResources().getColor(R.color.color_grey_999999));
            statusLabel.setTextColor(context.getResources().getColor(R.color.black));//teng
        } else {
            if (attachment.getType() == AVChatType.AUDIO) {
                typeImage.setImageResource(R.drawable.avchat_right_type_audio);
            } else {
                typeImage.setImageResource(R.drawable.avchat_right_type_video);
            }
//            statusLabel.setTextColor(Color.WHITE);
            statusLabel.setTextColor(context.getResources().getColor(R.color.black));//teng
        }
    }

    private void refreshContent() {
        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();

        String textString = "";
        switch (attachment.getState()) {
            case Success: //成功接听
                textString = TimeUtil.secToTime(attachment.getDuration());
                break;
            case Missed: //未接听
            case Rejected: //主动拒绝
                textString = context.getString(R.string.avchat_no_pick_up);
                break;
            default:
                break;
        }

        statusLabel.setText(textString);
    }
}
