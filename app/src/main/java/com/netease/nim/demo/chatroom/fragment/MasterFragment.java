package com.netease.nim.demo.chatroom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.helper.ChatRoomMemberCache;
import com.netease.nim.demo.chatroom.widget.ChatRoomImageView;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

/**
 * 聊天室主播fragment
 * Created by hzxuwen on 2015/12/17.
 */
public class MasterFragment extends TFragment {
    private static final String TAG = MasterFragment.class.getSimpleName();
    private ChatRoomImageView imageView;
    private TextView nameText;
    private TextView countText;
    private TextView announceText;
    private LinearLayout announceLayout;
    private LinearLayout noAnnounceLayout;

    private ChatRoomMember master;
    private long lastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
    }

    public void onCurrent() {
        if (!isFastClick()) {
            fetchRoomInfo();
        }
    }

    /**
     * 频率控制，至少间隔一分钟
     * @return
     */
    private boolean isFastClick() {
        long current = System.currentTimeMillis();
        long time = current - lastClickTime;
        if ( 0 < time && time < 60000) {
            return true;
        }
        lastClickTime = current;
        return false;
    }

    private void findViews() {
        imageView = findView(R.id.master_head_image);
        imageView.loadAvatarByUrl(""); // 网络不好的时候，设置一个默认头像
        nameText = findView(R.id.master_name);
        countText = findView(R.id.online_total);
        announceText = findView(R.id.announce_content);
        announceLayout = findView(R.id.announce_layout);
        noAnnounceLayout = findView(R.id.no_announce_layout);
    }

    private void fetchRoomInfo() {
        String roomId = ((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId();
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo param) {
                getChatRoomMaster(param);
            }

            @Override
            public void onFailed(int code) {
                LogUtil.d(TAG, "fetch room info failed:" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.d(TAG, "fetch room info exception:" + exception);
            }
        });
    }

    private void getChatRoomMaster(final ChatRoomInfo roomInfo) {
        master = ChatRoomMemberCache.getInstance().getChatRoomMember(roomInfo.getRoomId(), roomInfo.getCreator());
        if (master != null) {
            updateView(roomInfo);
        } else {
            ChatRoomMemberCache.getInstance().fetchMember(roomInfo.getRoomId(), roomInfo.getCreator(),
                    new SimpleCallback<ChatRoomMember>() {
                        @Override
                        public void onResult(boolean success, ChatRoomMember result) {
                            if (success) {
                                master = result;
                                updateView(roomInfo);
                            }
                        }
                    });
        }
    }

    private void updateView(ChatRoomInfo chatRoomInfo) {
        imageView.loadAvatarByUrl(master.getAvatar());
        nameText.setText(TextUtils.isEmpty(master.getNick()) ? "" : master.getNick());
        countText.setText(String.valueOf(chatRoomInfo.getOnlineUserCount()));

        if (TextUtils.isEmpty(chatRoomInfo.getAnnouncement())) {
            noAnnounceLayout.setVisibility(View.VISIBLE);
            announceLayout.setVisibility(View.GONE);
        } else {
            announceLayout.setVisibility(View.VISIBLE);
            noAnnounceLayout.setVisibility(View.GONE);
            announceText.setText(chatRoomInfo.getAnnouncement());
        }
    }
}
