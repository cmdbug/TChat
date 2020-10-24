package com.netease.nim.demo.session.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.session.activity.FileDownloadActivity;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.demo.file.FileIcons;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderFile extends MsgViewHolderBase {
    private ImageView fileIcon;
    private TextView fileNameLabel;
    private TextView fileStatusLabel;
    private ProgressBar progressBar;

    private FileAttachment msgAttachment;

    public MsgViewHolderFile(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_file;
    }

    @Override
    protected void inflateContentView() {
        fileIcon = (ImageView) view.findViewById(R.id.message_item_file_icon_image);
        fileNameLabel = (TextView) view.findViewById(R.id.message_item_file_name_label);
        fileStatusLabel = (TextView) view.findViewById(R.id.message_item_file_status_label);
        progressBar = (ProgressBar) view.findViewById(R.id.message_item_file_transfer_progress_bar);
    }

    @Override
    protected void bindContentView() {
        msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        initDisplay();

        if (!TextUtils.isEmpty(path)) {
            loadImageView();
        } else {
            AttachStatusEnum status = message.getAttachStatus();
            switch (status) {
                case def:
                    updateFileStatusLabel();
                    break;
                case transferring:
                    fileStatusLabel.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    int percent = (int) (getMsgAdapter().getProgress(message) * 100);
                    progressBar.setProgress(percent);
                    break;
                case transferred:
                case fail:
                    updateFileStatusLabel();
                    break;
            }
        }
    }

    private void loadImageView() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        fileStatusLabel.setText(sb.toString());

        progressBar.setVisibility(View.GONE);
    }

    private void initDisplay() {
        int iconResId = FileIcons.smallIcon(msgAttachment.getDisplayName());
        fileIcon.setImageResource(iconResId);
        fileNameLabel.setText(msgAttachment.getDisplayName());
    }

    private void updateFileStatusLabel() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        sb.append("  ");
        // 下载状态
        String path = msgAttachment.getPathForSave();
        if (AttachmentStore.isFileExist(path)) {
            sb.append(context.getString(R.string.file_transfer_state_downloaded));
        } else {
            sb.append(context.getString(R.string.file_transfer_state_undownload));
        }
        fileStatusLabel.setText(sb.toString());
    }

    @Override
    protected void onItemClick() {
        FileDownloadActivity.start(context, message);
    }

    @Override
    protected int leftBackground() {
        return R.drawable.chat_from_file_bg_normal;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.chat_to_file_bg_normal;
    }
}
