package com.netease.nim.demo.teamavchat.holder;

import com.netease.nim.demo.teamavchat.module.TeamAVChatItem;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2017/5/9.
 */

public class TeamAVChatEmptyViewHolder extends TeamAVChatItemViewHolderBase {

    public TeamAVChatEmptyViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected void inflate(BaseViewHolder holder) {

    }

    @Override
    protected void refresh(TeamAVChatItem data) {

    }


}
