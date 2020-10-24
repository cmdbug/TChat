package com.netease.nim.demo.chatroom.viewholder;

import com.netease.nim.demo.session.extension.GuessAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by hzxuwen on 2016/1/20.
 */
public class ChatRoomMsgViewHolderGuess extends ChatRoomMsgViewHolderText {

    public ChatRoomMsgViewHolderGuess(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected String getDisplayText() {
        GuessAttachment attachment = (GuessAttachment) message.getAttachment();

        return attachment.getValue().getDesc() + "!";
    }
}
