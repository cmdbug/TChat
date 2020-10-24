package com.netease.nim.demo.chatroom.fragment.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.constant.ChatRoomTab;
import com.netease.nim.demo.chatroom.fragment.ChatRoomMessageFragment;

/**
 * 直播互动基类fragment
 * Created by hzxuwen on 2015/12/14.
 */
public class MessageTabFragment extends ChatRoomTabFragment {
    private ChatRoomMessageFragment fragment;

    public MessageTabFragment() {
        this.setContainerId(ChatRoomTab.CHAT_ROOM_MESSAGE.fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }

    @Override
    protected void onInit() {
        findViews();
    }

    @Override
    public void onCurrent() {
        super.onCurrent();
    }

    @Override
    public void onLeave() {
        super.onLeave();
        if (fragment != null) {
            fragment.onLeave();
        }
    }

    private void findViews() {
        fragment = (ChatRoomMessageFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.chat_room_message_fragment);
    }
}
