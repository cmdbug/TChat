package com.netease.nim.demo.session.viewholder;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.activity.WatchSnapChatPictureActivity;
import com.netease.nim.demo.session.extension.SnapChatAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderSnapChat extends MsgViewHolderBase {

    private ImageView thumbnailImageView;

    protected View progressCover;
    private TextView progressLabel;
    private boolean isLongClick = false;

    public MsgViewHolderSnapChat(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_snapchat;
    }

    @Override
    protected void inflateContentView() {
        thumbnailImageView = (ImageView) view.findViewById(R.id.message_item_snap_chat_image);
        progressBar = findViewById(R.id.message_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(R.id.message_item_thumb_progress_cover);
        progressLabel = (TextView) view.findViewById(R.id.message_item_thumb_progress_text);
    }

    @Override
    protected void bindContentView() {
        contentContainer.setOnTouchListener(onTouchListener);

        layoutByDirection();

        refreshStatus();
    }

    private void refreshStatus() {
        thumbnailImageView.setBackgroundResource(isReceivedMessage() ? R.drawable.message_view_holder_left_snapchat : R.drawable.message_view_holder_right_snapchat);

        if (message.getStatus() == MsgStatusEnum.sending || message.getAttachStatus() == AttachStatusEnum.transferring) {
            progressCover.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressCover.setVisibility(View.GONE);
        }

        progressLabel.setText(StringUtil.getPercentString(getMsgAdapter().getProgress(message)));
        readReceiptTextView.setVisibility(View.GONE);
    }

    protected View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                    WatchSnapChatPictureActivity.destroy();

                    // 删除这条消息，当然你也可以将其标记为已读，同时删除附件内容，然后不让再查看
                    if (isLongClick && message.getAttachStatus() == AttachStatusEnum.transferred) {
                        // 物理删除
                        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                        AttachmentStore.delete(((SnapChatAttachment) message.getAttachment()).getPath());
                        AttachmentStore.delete(((SnapChatAttachment) message.getAttachment()).getThumbPath());

                        getMsgAdapter().deleteItem(message, true);
                        isLongClick = false;
                    }
                    break;
            }

            return false;
        }
    };

    @Override
    protected boolean onItemLongClick() {
        if (message.getStatus() == MsgStatusEnum.success) {
            WatchSnapChatPictureActivity.start(context, message);
            isLongClick = true;
            return true;
        }
        return false;
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    private void layoutByDirection() {
        View body = findViewById(R.id.message_item_snap_chat_body);
        View tipsLayout = findViewById(R.id.message_item_tips_layout);
        View tips = findViewById(R.id.message_item_snap_chat_tips_label);
        View readed = findViewById(R.id.message_item_snap_chat_readed);
        ViewGroup container = (ViewGroup) body.getParent();
        container.removeView(tipsLayout);
        if (isReceivedMessage()) {
            container.addView(tipsLayout, 1);
        } else {
            container.addView(tipsLayout, 0);
        }
        if (message.getStatus() == MsgStatusEnum.success) {
            tips.setVisibility(View.VISIBLE);
        } else {
            tips.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(getMsgAdapter().getUuid()) && message.getUuid().equals(getMsgAdapter().getUuid())) {
            readed.setVisibility(View.VISIBLE);
        } else {
            readed.setVisibility(View.GONE);
        }
    }
}
