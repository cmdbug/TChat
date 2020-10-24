package com.netease.nim.demo.chatroom.module;

import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.chatroom.adapter.ChatRoomMsgAdapter;
import com.netease.nim.uikit.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.loadmore.MsgListFetchLoadMoreView;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 聊天室消息收发模块
 * Created by huangjun on 2016/1/27.
 */
public class ChatRoomMsgListPanel {
    private static final int MESSAGE_CAPACITY = 500;

    // container
    private Container container;
    private View rootView;
    private Handler uiHandler;

    // message list view
    private RecyclerView messageListView;
    private LinkedList<ChatRoomMessage> items;
    private ChatRoomMsgAdapter adapter;

    public ChatRoomMsgListPanel(Container container, View rootView) {
        this.container = container;
        this.rootView = rootView;

        init();
    }

    public void onResume() {
        setEarPhoneMode(UserPreferences.isEarPhoneModeEnable());
    }

    public void onPause() {
        MessageAudioControl.getInstance(container.activity).stopAudio();
    }

    public void onDestroy() {
        registerObservers(false);
    }

    public boolean onBackPressed() {
        uiHandler.removeCallbacks(null);
        MessageAudioControl.getInstance(container.activity).stopAudio(); // 界面返回，停止语音播放
        return false;
    }

    public void reload(Container container) {
        this.container = container;
        if (adapter != null) {
            adapter.clearData();
        }
    }

    private void init() {
        initListView();
        this.uiHandler = new Handler(DemoCache.getContext().getMainLooper());
        registerObservers(true);
    }

    private void initListView() {
        // RecyclerView
        messageListView = (RecyclerView) rootView.findViewById(com.netease.nim.uikit.R.id.messageListView);
        messageListView.setLayoutManager(new LinearLayoutManager(container.activity));
        messageListView.requestDisallowInterceptTouchEvent(true);
        messageListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    container.proxy.shouldCollapseInputPanel();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            messageListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // adapter
        items = new LinkedList<>();
        adapter = new ChatRoomMsgAdapter(messageListView, items);
        adapter.closeLoadAnimation();
        adapter.setFetchMoreView(new MsgListFetchLoadMoreView());
        adapter.setLoadMoreView(new MsgListFetchLoadMoreView());
        adapter.setEventListener(new MsgItemEventListener());
        adapter.setOnFetchMoreListener(new MessageLoader()); // load from start
        messageListView.setAdapter(adapter);
    }

    public void onIncomingMessage(List<ChatRoomMessage> messages) {
        boolean needScrollToBottom = isLastMessageVisible();
        boolean needRefresh = false;
        List<ChatRoomMessage> addedListItems = new ArrayList<>(messages.size());
        for (ChatRoomMessage message : messages) {
            // 保证显示到界面上的消息，来自同一个聊天室
            if (isMyMessage(message)) {
                saveMessage(message);
                addedListItems.add(message);
                needRefresh = true;
            }
        }
        if (needRefresh) {
            adapter.notifyDataSetChanged();
        }

        // incoming messages tip
        ChatRoomMessage lastMsg = messages.get(messages.size() - 1);
        if (isMyMessage(lastMsg) && needScrollToBottom) {
            doScrollToBottom();
        }
    }

    private boolean isLastMessageVisible() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) messageListView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisiblePosition >= adapter.getBottomDataPosition();
    }


    // 发送消息后，更新本地消息列表
    public void onMsgSend(ChatRoomMessage message) {
        saveMessage(message);

        adapter.notifyDataSetChanged();
        doScrollToBottom();
    }

    public void saveMessage(final ChatRoomMessage message) {
        if (message == null) {
            return;
        }

        if (items.size() >= MESSAGE_CAPACITY) {
            items.poll();
        }

        items.add(message);
    }

    /**
     * *************** MessageLoader ***************
     */
    private class MessageLoader implements BaseFetchLoadAdapter.RequestLoadMoreListener, BaseFetchLoadAdapter.RequestFetchMoreListener {

        private static final int LOAD_MESSAGE_COUNT = 10;

        private IMMessage anchor;

        private boolean firstLoad = true;

        private boolean fetching = false;

        public MessageLoader() {
            anchor = null;
            loadFromLocal();
        }

        private RequestCallback<List<ChatRoomMessage>> callback = new RequestCallbackWrapper<List<ChatRoomMessage>>() {
            @Override
            public void onResult(int code, List<ChatRoomMessage> messages, Throwable exception) {
                onMessageLoaded(messages);

                fetching = false;
            }
        };

        private void loadFromLocal() {
            if (fetching) {
                return;
            }

            fetching = true;
            NIMClient.getService(ChatRoomService.class).pullMessageHistoryEx(container.account, anchor().getTime(), LOAD_MESSAGE_COUNT, QueryDirectionEnum.QUERY_OLD)
                    .setCallback(callback);
        }

        private IMMessage anchor() {
            if (items.size() == 0) {
                return (anchor == null ? ChatRoomMessageBuilder.createEmptyChatRoomMessage(container.account, 0) : anchor);
            } else {
                return items.get(0);
            }
        }

        /**
         * 历史消息加载处理
         */
        private void onMessageLoaded(List<ChatRoomMessage> messages) {
            int count = messages.size();

            // 逆序
            Collections.reverse(messages);
            // 加入到列表中
            if (count < LOAD_MESSAGE_COUNT) {
                adapter.fetchMoreEnd(messages, true);
            } else {
                adapter.fetchMoreComplete(messages);
            }

            // 如果是第一次加载，updateShowTimeItem返回的就是lastShowTimeItem
            if (firstLoad) {
                doScrollToBottom();
            }

            firstLoad = false;
        }

        @Override
        public void onFetchMoreRequested() {
            loadFromLocal();
        }

        @Override
        public void onLoadMoreRequested() {

        }
    }

    /**
     * ************************* 观察者 ********************************
     */

    private void registerObservers(boolean register) {
        ChatRoomServiceObserver service = NIMClient.getService(ChatRoomServiceObserver.class);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeAttachmentProgress(attachmentProgressObserver, register);
    }

    /**
     * 消息状态变化观察者
     */
    Observer<ChatRoomMessage> messageStatusObserver = new Observer<ChatRoomMessage>() {
        @Override
        public void onEvent(ChatRoomMessage message) {
            if (isMyMessage(message)) {
                onMessageStatusChange(message);
            }
        }
    };

    /**
     * 消息附件上传/下载进度观察者
     */
    Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            onAttachmentProgressChange(progress);
        }
    };

    private void onMessageStatusChange(IMMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            item.setStatus(message.getStatus());
            item.setAttachStatus(message.getAttachStatus());
            if (item.getAttachment() instanceof AVChatAttachment
                    || item.getAttachment() instanceof AudioAttachment) {
                item.setAttachment(message.getAttachment());
            }
            refreshViewHolderByIndex(index);
        }
    }

    private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            adapter.putProgress(item, value);
            refreshViewHolderByIndex(index);
        }
    }

    public boolean isMyMessage(ChatRoomMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.account);
    }

    /**
     * 刷新单条消息
     */
    private void refreshViewHolderByIndex(final int index) {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (index < 0) {
                    return;
                }

                adapter.notifyDataItemChanged(index);
            }
        });
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            IMMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    private class MsgItemEventListener implements ChatRoomMsgAdapter.ViewHolderEventListener {

        @Override
        public void onFailedBtnClick(IMMessage message) {
            if (message.getDirect() == MsgDirectionEnum.Out) {
                // 发出的消息，如果是发送失败，直接重发，否则有可能是漫游到的多媒体消息，但文件下载
                if (message.getStatus() == MsgStatusEnum.fail) {
                    resendMessage(message); // 重发
                } else {
                    if (message.getAttachment() instanceof FileAttachment) {
                        FileAttachment attachment = (FileAttachment) message.getAttachment();
                        if (TextUtils.isEmpty(attachment.getPath())
                                && TextUtils.isEmpty(attachment.getThumbPath())) {
                            showReDownloadConfirmDlg(message);
                        }
                    } else {
                        resendMessage(message);
                    }
                }
            } else {
                showReDownloadConfirmDlg(message);
            }
        }

        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item) {
            return true;
        }

        // 重新下载(对话框提示)
        private void showReDownloadConfirmDlg(final IMMessage message) {
            EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                @Override
                public void doCancelAction() {
                }

                @Override
                public void doOkAction() {
                    // 正常情况收到消息后附件会自动下载。如果下载失败，可调用该接口重新下载
                    if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
                        NIMClient.getService(ChatRoomService.class).downloadAttachment((ChatRoomMessage) message, true);
                }
            };

            final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(container.activity, null,
                    container.activity.getString(com.netease.nim.uikit.R.string.repeat_download_message), true, listener);
            dialog.show();
        }

        // 重发消息到服务器
        private void resendMessage(IMMessage message) {
            // 重置状态为unsent
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < items.size()) {
                IMMessage item = items.get(index);
                item.setStatus(MsgStatusEnum.sending);
                refreshViewHolderByIndex(index);
            }

            NIMClient.getService(ChatRoomService.class).sendMessage((ChatRoomMessage) message, true);
        }
    }

    private void setEarPhoneMode(boolean earPhoneMode) {
        UserPreferences.setEarPhoneModeEnable(earPhoneMode);
        MessageAudioControl.getInstance(container.activity).setEarPhoneModeEnable(earPhoneMode);
    }

    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doScrollToBottom();
            }
        }, 200);
    }

    private void doScrollToBottom() {
        messageListView.scrollToPosition(adapter.getBottomDataPosition());
    }
}
