package com.netease.nim.demo.session.action;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.file.browser.FileBrowserActivity;
import com.netease.nim.demo.session.activity.SendRedPackedActivity;
import com.netease.nim.demo.session.extension.RedPackedAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * Created by WZTENG on 2017/5/9.
 */
public class RedPackedAction extends BaseAction {

    public RedPackedAction() {
        super(R.drawable.message_plus_redpacked_selector, R.string.input_panel_redpacked);
    }

    /**
     * **********************红包************************
     */
    private void sendRedPacked() {
        SendRedPackedActivity.startActivityForResult(getActivity(), makeRequestCode(100));
    }

    @Override
    public void onClick() {
        sendRedPacked();
        //测试
//        RedPackedAttachment redPackedAttachment = new RedPackedAttachment();
//        redPackedAttachment.setFlag((byte) 0);
//        redPackedAttachment.setRedPackedMoney(100);
//        redPackedAttachment.setRedPackedNameLabel("恭喜发财，大吉大利！");
//        redPackedAttachment.setRedPackedFromLabel("藤信红包");
//        IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), "红包", redPackedAttachment);
//        sendMessage(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                String money = data.getStringExtra("money");
                String say = data.getStringExtra("say");
                RedPackedAttachment redPackedAttachment = new RedPackedAttachment();
                redPackedAttachment.setFlag((byte) 0);
                redPackedAttachment.setRedPackedMoney(Float.valueOf(money));
                redPackedAttachment.setRedPackedNameLabel(say);
                redPackedAttachment.setRedPackedFromLabel("藤信红包");
                IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), "红包", redPackedAttachment);
                sendMessage(message);
            }
        }
    }
}
