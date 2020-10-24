package com.netease.nim.demo.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.helper.CustomNotificationCache;
import com.netease.nim.demo.main.viewholder.CustomNotificationViewHolder;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.adapter.TAdapter;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;
import com.netease.nim.uikit.common.ui.listview.AutoRefreshListView;
import com.netease.nim.uikit.common.ui.listview.MessageListView;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义通知
 * <p/>
 * Created by huangjun on 2015/5/28
 */
public class CustomNotificationActivity extends UI implements TAdapterDelegate {

    private static final int CONTACT_SELECT_REQUEST_CODE = 0x01;

    // view
    private MessageListView listView;

    private int sendTarget = -1; // 0 好友 1 群

    // adapter
    private TAdapter adapter;
    private List<CustomNotification> items = new ArrayList<>();

    public static void start(Context context) {
        start(context, null, true);
    }

    public static void start(Context context, Intent extras, boolean clearTop) {
        Intent intent = new Intent();
        intent.setClass(context, CustomNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return CustomNotificationViewHolder.class;
    }

    @Override
    public boolean enabled(int position) {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.system_notification_message_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.custom_notification;
        setToolBar(R.id.toolbar, options);

        initAdapter();
        initListView();

        loadData(); // load old data
        registerCustomNotificationObserver(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        registerCustomNotificationObserver(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_notification_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_custom_notification_to_team:
                selectCustomNotificationTarget(true);
                break;
            case R.id.send_custom_notification_to_buddy:
                selectCustomNotificationTarget(false);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        adapter = new TAdapter(this, items, this);
    }

    private void initListView() {
        listView = (MessageListView) findViewById(R.id.messageListView);
        listView.setMode(AutoRefreshListView.Mode.END);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // adapter
        listView.setAdapter(adapter);
    }


    private void loadData() {
        List<CustomNotification> cache = CustomNotificationCache.getInstance().getCustomNotification();
        if (!cache.isEmpty()) {
            items.addAll(cache);
        }

        refresh();
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void registerCustomNotificationObserver(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, register);
    }

    Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            if (!items.contains(customNotification) && customNotification.getContent() != null) {
                items.add(0, customNotification);
            }
            refresh();
        }
    };

    private void selectCustomNotificationTarget(boolean team) {
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = DemoCache.getContext().getString(R.string.select_custom_notification_target);
        option.multi = false;
        option.showContactSelectArea = !team;
        option.type = team ? ContactSelectActivity.ContactSelectType.TEAM :
                ContactSelectActivity.ContactSelectType.BUDDY;

        sendTarget = team ? 1 : 0;

        NimUIKit.startContactSelect(CustomNotificationActivity.this, option, CONTACT_SELECT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_SELECT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            if (selected != null && !selected.isEmpty()) {
                final EasyEditDialog requestDialog = new EasyEditDialog(this);
                requestDialog.setEditTextMaxLength(200);
                requestDialog.setTitle(getString(R.string.send_custom_notification_tip));
                requestDialog.addNegativeButtonListener(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestDialog.dismiss();
                        finish();
                    }
                });
                requestDialog.addPositiveButtonListener(R.string.send, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestDialog.dismiss();
                        String content = requestDialog.getEditMessage();
                        if (!TextUtils.isEmpty(content)) {
                            sendCustomNotification(selected.get(0), content);
                        }
                        finish();
                    }
                });
                requestDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                requestDialog.show();
                showKeyboard(true);
            }
        } else {
            sendTarget = -1;
        }
    }

    private void sendCustomNotification(String account, String content) {
        JSONObject obj = new JSONObject();
        obj.put("id", "2");
        obj.put("content", content);
        String jsonContent = obj.toJSONString();

        CustomNotification notification = new CustomNotification();
        notification.setFromAccount(DemoCache.getAccount());
        notification.setSessionId(account);
        notification.setSendToOnlineUserOnly(false);
        notification.setSessionType(sendTarget == 1 ? SessionTypeEnum.Team : SessionTypeEnum.P2P);
        notification.setApnsText(jsonContent);
        notification.setContent(jsonContent);

        NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(CustomNotificationActivity.this, R.string.send_custom_notification_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(CustomNotificationActivity.this, R.string.send_custom_notification_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(CustomNotificationActivity.this, R.string.send_custom_notification_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
