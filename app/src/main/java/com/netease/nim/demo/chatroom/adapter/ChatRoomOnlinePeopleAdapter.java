package com.netease.nim.demo.chatroom.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.widget.ChatRoomImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.util.List;

/**
 * Created by huangjun on 2016/12/29.
 */
public class ChatRoomOnlinePeopleAdapter extends BaseQuickAdapter<ChatRoomMember, BaseViewHolder> {

    public ChatRoomOnlinePeopleAdapter(RecyclerView recyclerView, List<ChatRoomMember> members) {
        super(recyclerView, R.layout.online_people_item, members);
    }

    @Override
    protected void convert(BaseViewHolder holder, ChatRoomMember member, int position, boolean isScrolling) {
        // bg selector
        holder.getConvertView().setBackgroundResource(com.netease.nim.uikit.R.drawable.touch_bg);

        // identity image
        ImageView identityImage = holder.getView(R.id.identity_image);
        if (member.getMemberType() == MemberType.CREATOR) {
            identityImage.setVisibility(View.VISIBLE);
            identityImage.setImageDrawable(holder.getContext().getResources().getDrawable(R.drawable.master_icon));
        } else if (member.getMemberType() == MemberType.ADMIN) {
            identityImage.setVisibility(View.VISIBLE);
            identityImage.setImageDrawable(holder.getContext().getResources().getDrawable(R.drawable.admin_icon));
        } else {
            identityImage.setVisibility(View.GONE);
        }

        // head image
        ChatRoomImageView userHeadImage = holder.getView(R.id.user_head);
        userHeadImage.loadAvatarByUrl(member.getAvatar());

        // user name
        holder.setText(R.id.user_name, TextUtils.isEmpty(member.getNick()) ? "" : member.getNick());
    }
}
