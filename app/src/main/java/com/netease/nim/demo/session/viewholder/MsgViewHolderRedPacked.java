package com.netease.nim.demo.session.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.activity.RedPackedDialog;
import com.netease.nim.demo.session.extension.RedPackedAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

/**
 * Created by WZTENG on 2017/5/9.
 */
public class MsgViewHolderRedPacked extends MsgViewHolderBase {
    private ImageView redPackedIcon;
    private TextView redPackedNameLabel;
    private TextView redPackedStatusLabel;
    private TextView redPackedFromLabel;

    private RedPackedAttachment msgAttachment;

    public MsgViewHolderRedPacked(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_redpacked;
    }

    @Override
    protected void inflateContentView() {
        redPackedIcon = (ImageView) view.findViewById(R.id.message_item_redpacked_icon_image);
        redPackedNameLabel = (TextView) view.findViewById(R.id.message_item_redpacked_name_label);
        redPackedStatusLabel = (TextView) view.findViewById(R.id.message_item_redpacked_status_label);
        redPackedFromLabel = (TextView) view.findViewById(R.id.message_item_redpacked_from_label);
    }

    @Override
    protected void bindContentView() {
        layoutDirection();
        msgAttachment = (RedPackedAttachment) message.getAttachment();
        String nameLabel = msgAttachment.getRedPackedNameLabel();
        byte statusLabel = msgAttachment.getFlag();
        float money = msgAttachment.getRedPackedMoney();
        initDisplay();

        AttachStatusEnum status = message.getAttachStatus();
        switch (status) {
            case def:
                updateRedPackedStatusLabel();
                break;
            case transferring:
                break;
            case transferred:
            case fail:
                updateRedPackedStatusLabel();
                break;
        }
    }

    private void layoutDirection() {
        RelativeLayout bodyTextView = findViewById(R.id.message_item_redpacked_detail_layout);
        ViewGroup.LayoutParams params = bodyTextView.getLayoutParams();
        params.width = ScreenUtil.dip2px(210);
        params.height = ScreenUtil.dip2px(85);
        bodyTextView.setLayoutParams(params);
    }

    private void initDisplay() {
        int iconResId = R.drawable.album_push_lucky_money_icon;
        redPackedIcon.setImageResource(iconResId);
        redPackedNameLabel.setText("恭喜发财，大吉大利！");
        redPackedStatusLabel.setText("领取红包");
        redPackedFromLabel.setText("藤信红包");
    }

    public void updateRedPackedStatusLabel() {
        int iconResId = R.drawable.album_push_lucky_money_icon;
        redPackedIcon.setImageResource(iconResId);
        redPackedNameLabel.setText(msgAttachment.getRedPackedNameLabel() + "");
        redPackedStatusLabel.setText((msgAttachment.getFlag() == 0 ? "领取红包" : "查看红包"));
        redPackedFromLabel.setText(msgAttachment.getRedPackedFromLabel() + "");
    }

    @Override
    protected void onItemClick() {
//        if (msgAttachment.getFlag() == 0) {
            //弹出红包对话框
            RedPackedDialog.Builder builder = new RedPackedDialog.Builder(
                    context,
                    message,
                    new RedPackedDialog.DialogCallback() {
                        @Override
                        public void updateRedPackedMessage() {
                            updateRedPackedStatusLabel();
                        }
                    });
            builder.create().show();
/*
            msgAttachment.setFlag((byte) 1);
            message.setAttachment(msgAttachment);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
            updateRedPackedStatusLabel();
*/
//        } else {
//            //直接进入红包Activity
//        }
    }

    @Override
    protected boolean onItemLongClick() {
        return true;
    }

    @Override
    protected int leftBackground() {
        return R.drawable.wzt_message_item_redpacked_left_selector;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.wzt_message_item_redpacked_right_selector;
    }
}
