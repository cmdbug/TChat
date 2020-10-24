package com.netease.nim.demo.session.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.RedPackedAttachment;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by WZTENG on 2017/05/10 0010.
 */

public class RedPackedDialog extends Dialog {
    public static DialogCallback dialogCallback;
    private static AnimationDrawable animationDrawable;

    public RedPackedDialog(Context context) {
        super(context);
    }

    public RedPackedDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private IMMessage message;
        private RedPackedAttachment msgAttachment;
        private ImageView ivClose;
        private HeadImageView ivAvatar;
        private TextView tvName;
        private TextView tvTip;
        private TextView tvSay;
        private TextView tvMoney;
        private ImageView ivOpen;
        private Button btnDetial;

        public Builder(Context context, IMMessage message, DialogCallback dialogCallbackx) {
            this.context = context;
            this.message = message;
            dialogCallback = dialogCallbackx;
        }

        public RedPackedDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final RedPackedDialog dialog = new RedPackedDialog(context, R.style.dialog_redpacked);
            View layout = inflater.inflate(R.layout.wzt_redpacked_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            msgAttachment = (RedPackedAttachment) message.getAttachment();

            ivClose = (ImageView) layout.findViewById(R.id.iv_red_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //这里刷新一下红包的信息
                    dialogCallback.updateRedPackedMessage();
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                    }
                }
            });
            ivAvatar = (HeadImageView) layout.findViewById(R.id.iv_red_avatar);
//            ivAvatar.loadBuddyAvatar(DemoCache.getAccount());
            ivAvatar.loadBuddyAvatar(message.getFromAccount());
            tvName = (TextView) layout.findViewById(R.id.tv_red_name);
//            tvName.setText(DemoCache.getAccount());
            tvName.setText(message.getFromAccount());
            tvTip = (TextView) layout.findViewById(R.id.tv_red_tip);
            tvSay = (TextView) layout.findViewById(R.id.tv_red_say);
            tvSay.setText(msgAttachment.getRedPackedNameLabel());
            tvMoney = (TextView) layout.findViewById(R.id.tv_red_money);
            ivOpen = (ImageView) layout.findViewById(R.id.iv_red_open);
            btnDetial = (Button) layout.findViewById(R.id.btn_red_detial);
            if (msgAttachment.getFlag() == 0) {
                //未领取，显示打开图片
                ivOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgAttachment.setFlag((byte) 1);
                        message.setAttachment(msgAttachment);
                        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
                        //领取，查看金额
                        ivOpen.setImageResource(R.drawable.wzt_redpacked_openbtn_animallist);
                        animationDrawable = (AnimationDrawable) ivOpen.getDrawable();
                        animationDrawable.start();

                        int duration = 0;
                        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                            duration += animationDrawable.getDuration(i);
                        }
                        final Handler rHandler = new RHandler();
                        rHandler.postDelayed(new Runnable() {
                            public void run() {
                                //发送消息出去
                                Message message = new Message();
                                message.what = 0;
                                message.obj = "over";
                                rHandler.sendMessage(message);
                            }
                        }, duration);
                    }
                });
            } else {
                //已领取，直接查看金额
                ivOpen.setVisibility(View.INVISIBLE);
                btnDetial.setVisibility(View.VISIBLE);
                tvMoney.setVisibility(View.VISIBLE);
                tvMoney.setText(msgAttachment.getRedPackedMoney() + " 元");
            }
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        /**
         * 使用 Handler来修改对话框
         */
        private class RHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ivOpen.setVisibility(View.INVISIBLE);
                btnDetial.setVisibility(View.VISIBLE);
                tvMoney.setVisibility(View.VISIBLE);
                tvMoney.setText(msgAttachment.getRedPackedMoney() + " 元");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialogCallback.updateRedPackedMessage();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    /**
     * 回调刷新红包
     */
    public interface DialogCallback {
        public void updateRedPackedMessage();
    }
}
