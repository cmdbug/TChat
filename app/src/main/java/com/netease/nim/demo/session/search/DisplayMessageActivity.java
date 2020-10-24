package com.netease.nim.demo.session.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nim.uikit.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 搜索结果消息列表界面
 * <p>
 * Created by huangjun on 2017/1/11.
 */
public class DisplayMessageActivity extends UI implements ModuleProxy {

    private static String EXTRA_ANCHOR = "anchor";

    public static void start(Context context, IMMessage anchor) {
        Intent intent = new Intent();
        intent.setClass(context, DisplayMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //search extra
        intent.putExtra(EXTRA_ANCHOR, anchor);

        context.startActivity(intent);
    }

    // context
    private SessionTypeEnum sessionType;
    private String account; // 对方帐号
    private IMMessage anchor;

    private MessageListPanelEx messageListPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(this).inflate(R.layout.message_history_activity, null);
        setContentView(rootView);

        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options);

        onParseIntent();

        Container container = new Container(this, account, sessionType, this);
        messageListPanel = new MessageListPanelEx(container, rootView, anchor, true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        messageListPanel.onDestroy();
    }

    protected void onParseIntent() {
        anchor = (IMMessage) getIntent().getSerializableExtra(EXTRA_ANCHOR);
        account = anchor.getSessionId();
        sessionType = anchor.getSessionType();

        setTitle(UserInfoHelper.getUserTitleName(account, sessionType));
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        return false;
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void shouldCollapseInputPanel() {

    }

    @Override
    public boolean isLongClickEnabled() {
        return true;
    }

}
