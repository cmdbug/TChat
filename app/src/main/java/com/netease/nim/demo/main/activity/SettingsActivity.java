package com.netease.nim.demo.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.activity.AVChatSettingsActivity;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.activity.UserProfileSettingActivity;
import com.netease.nim.demo.jsbridge.JsBridgeActivity;
import com.netease.nim.demo.main.adapter.SettingsAdapter;
import com.netease.nim.demo.main.model.SettingTemplate;
import com.netease.nim.demo.main.model.SettingType;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.avchat.AVChatNetDetectCallback;
import com.netease.nimlib.sdk.avchat.AVChatNetDetector;
import com.netease.nimlib.sdk.lucene.LuceneService;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.settings.SettingsService;
import com.netease.nimlib.sdk.settings.SettingsServiceObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzxuwen on 2015/6/26.
 */
public class SettingsActivity extends UI implements SettingsAdapter.SwitchChangeListener {
    private static final int TAG_HEAD = 1;
    private static final int TAG_NOTICE = 2;
    private static final int TAG_NO_DISTURBE = 3;
    private static final int TAG_CLEAR = 4;
    private static final int TAG_CUSTOM_NOTIFY = 5;
    private static final int TAG_ABOUT = 6;
    private static final int TAG_SPEAKER = 7;

    private static final int TAG_NRTC_SETTINGS = 8;
    private static final int TAG_NRTC_NET_DETECT = 9;

    private static final int TAG_MSG_IGNORE = 10;
    private static final int TAG_RING = 11;
    private static final int TAG_LED = 12;
    private static final int TAG_NOTICE_CONTENT = 13; // 通知栏提醒配置
    private static final int TAG_CLEAR_INDEX = 18; // 清空全文检索缓存
    private static final int TAG_MULTIPORT_PUSH = 19; // 桌面端登录，是否推送
    private static final int TAG_JS_BRIDGE = 20; // js bridge

    private static final int TAG_NOTIFICATION_STYLE = 21; // 通知栏展开、折叠
    ListView listView;
    SettingsAdapter adapter;
    private List<SettingTemplate> items = new ArrayList<SettingTemplate>();
    private String noDisturbTime;
    private SettingTemplate disturbItem;
    private SettingTemplate clearIndexItem;
    private SettingTemplate notificationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);

        initData();
        initUI();

        registerObservers(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // android2.3以下版本 布局混乱的问题
        if (Build.VERSION.SDK_INT <= 10) {
            adapter = null;
            initAdapter();
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(SettingsServiceObserver.class).observeMultiportPushConfigNotify(pushConfigObserver, register);
    }

    Observer<Boolean> pushConfigObserver = new Observer<Boolean>() {
        @Override
        public void onEvent(Boolean aBoolean) {
            Toast.makeText(SettingsActivity.this, "收到multiport push config：" + aBoolean, Toast.LENGTH_SHORT).show();
        }
    };

    private void initData() {
        if (UserPreferences.getStatusConfig() == null || !UserPreferences.getStatusConfig().downTimeToggle) {
            noDisturbTime = getString(R.string.setting_close);
        } else {
            noDisturbTime = String.format("%s到%s", UserPreferences.getStatusConfig().downTimeBegin,
                    UserPreferences.getStatusConfig().downTimeEnd);
        }
    }

    private void initUI() {
        initItems();
        listView = (ListView) findViewById(R.id.settings_listview);
        View footer = LayoutInflater.from(this).inflate(R.layout.settings_logout_footer, null);
        listView.addFooterView(footer);

        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingTemplate item = items.get(position);
                onListItemClick(item);
            }
        });
        View logoutBtn = footer.findViewById(R.id.settings_button_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void initAdapter() {
        adapter = new SettingsAdapter(this, this, items);
        listView.setAdapter(adapter);
    }

    private void initItems() {
        items.clear();

        items.add(new SettingTemplate(TAG_HEAD, SettingType.TYPE_HEAD));
        notificationItem = new SettingTemplate(TAG_NOTICE, getString(R.string.msg_notice), SettingType.TYPE_TOGGLE,
                UserPreferences.getNotificationToggle());
        items.add(notificationItem);
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_RING, getString(R.string.ring), SettingType.TYPE_TOGGLE,
                UserPreferences.getRingToggle()));
        items.add(new SettingTemplate(TAG_LED, getString(R.string.led), SettingType.TYPE_TOGGLE,
                UserPreferences.getLedToggle()));
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_NOTICE_CONTENT, getString(R.string.notice_content), SettingType.TYPE_TOGGLE,
                UserPreferences.getNoticeContentToggle()));
//        items.add(new SettingTemplate(TAG_NOTIFICATION_STYLE, getString(R.string.notification_folded), SettingType.TYPE_TOGGLE,
//                UserPreferences.getNotificationFoldedToggle()));
        items.add(SettingTemplate.addLine());
        disturbItem = new SettingTemplate(TAG_NO_DISTURBE, getString(R.string.no_disturb), noDisturbTime);
        items.add(disturbItem);
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_MULTIPORT_PUSH, getString(R.string.multiport_push), SettingType.TYPE_TOGGLE,
                !NIMClient.getService(SettingsService.class).isMultiportPushOpen()));

        items.add(SettingTemplate.makeSeperator());

        items.add(new SettingTemplate(TAG_SPEAKER, getString(R.string.msg_speaker), SettingType.TYPE_TOGGLE,
                com.netease.nim.uikit.UserPreferences.isEarPhoneModeEnable()));

        items.add(SettingTemplate.makeSeperator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            items.add(new SettingTemplate(TAG_NRTC_SETTINGS, getString(R.string.nrtc_settings)));
            items.add(SettingTemplate.addLine());
            items.add(new SettingTemplate(TAG_NRTC_NET_DETECT, "音视频通话网络探测"));
            items.add(SettingTemplate.makeSeperator());
        }

        items.add(new SettingTemplate(TAG_MSG_IGNORE, "过滤通知",
                SettingType.TYPE_TOGGLE, UserPreferences.getMsgIgnore()));

        items.add(SettingTemplate.makeSeperator());

        items.add(new SettingTemplate(TAG_CLEAR, getString(R.string.about_clear_msg_history)));
        items.add(SettingTemplate.addLine());
        clearIndexItem = new SettingTemplate(TAG_CLEAR_INDEX, getString(R.string.clear_index), getIndexCacheSize() + " M");
        items.add(clearIndexItem);

        items.add(SettingTemplate.makeSeperator());

        items.add(new SettingTemplate(TAG_CUSTOM_NOTIFY, getString(R.string.custom_notification)));
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_JS_BRIDGE, getString(R.string.js_bridge_demonstration)));
        items.add(SettingTemplate.makeSeperator());

        items.add(new SettingTemplate(TAG_ABOUT, getString(R.string.setting_about)));

    }

    private void onListItemClick(SettingTemplate item) {
        if (item == null) return;

        switch (item.getId()) {
            case TAG_HEAD:
                UserProfileSettingActivity.start(this, DemoCache.getAccount());
                break;
            case TAG_NO_DISTURBE:
                startNoDisturb();
                break;
            case TAG_CUSTOM_NOTIFY:
                CustomNotificationActivity.start(SettingsActivity.this);
                break;
            case TAG_ABOUT:
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                break;
            case TAG_CLEAR:
                NIMClient.getService(MsgService.class).clearMsgDatabase(true);
                Toast.makeText(SettingsActivity.this, R.string.clear_msg_history_success, Toast.LENGTH_SHORT).show();
                break;
            case TAG_CLEAR_INDEX:
                clearIndex();
                break;
            case TAG_NRTC_SETTINGS:
                startActivity(new Intent(SettingsActivity.this, AVChatSettingsActivity.class));
                break;
            case TAG_NRTC_NET_DETECT:
                netDetectForNrtc();
                break;
            case TAG_JS_BRIDGE:
                startActivity(new Intent(SettingsActivity.this, JsBridgeActivity.class));
                break;
            default:
                break;
        }
    }

    private void netDetectForNrtc() {
        AVChatNetDetector.startNetDetect(new AVChatNetDetectCallback() {
            @Override
            public void onDetectResult(String id,
                                       int code,
                                       int loss,
                                       int rttMax,
                                       int rttMin,
                                       int rttAvg,
                                       int mdev,
                                       String info) {
                String msg = code == 200 ?
                        ("loss:" + loss + ", rtt min/avg/max/mdev = " + rttMin + "/" + rttAvg + "/" + rttMax + "/" + mdev + " ms")
                        : ("error:" + code);
                Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 注销
     */
    private void logout() {
        removeLoginState();
        MainActivity.logout(SettingsActivity.this, false);

        finish();
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 清除登陆状态
     */
    private void removeLoginState() {
        Preferences.saveUserToken("");
    }

    @Override
    public void onSwitchChange(SettingTemplate item, boolean checkState) {
        switch (item.getId()) {
            case TAG_NOTICE:
                setMessageNotify(checkState);
                break;
            case TAG_SPEAKER:
                com.netease.nim.uikit.UserPreferences.setEarPhoneModeEnable(checkState);
                MessageAudioControl.getInstance(SettingsActivity.this).setEarPhoneModeEnable(checkState);
                break;
            case TAG_MSG_IGNORE:
                UserPreferences.setMsgIgnore(checkState);
                break;
            case TAG_RING:
                UserPreferences.setRingToggle(checkState);
                StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
                config.ring = checkState;
                UserPreferences.setStatusConfig(config);
                NIMClient.updateStatusBarNotificationConfig(config);
                break;
            case TAG_LED:
                UserPreferences.setLedToggle(checkState);
                StatusBarNotificationConfig config1 = UserPreferences.getStatusConfig();
                if (checkState) {
                    config1.ledARGB = Color.GREEN;
                    config1.ledOnMs = 1000;
                    config1.ledOffMs = 1500;
                } else {
                    config1.ledARGB = -1;
                    config1.ledOnMs = -1;
                    config1.ledOffMs = -1;
                }
                UserPreferences.setStatusConfig(config1);
                NIMClient.updateStatusBarNotificationConfig(config1);
                break;
            case TAG_NOTICE_CONTENT:
                UserPreferences.setNoticeContentToggle(checkState);
                StatusBarNotificationConfig config2 = UserPreferences.getStatusConfig();
                config2.titleOnlyShowAppName = checkState;
                UserPreferences.setStatusConfig(config2);
                NIMClient.updateStatusBarNotificationConfig(config2);
                break;
            case TAG_MULTIPORT_PUSH:
                updateMultiportPushConfig(!checkState);
                break;
            case TAG_NOTIFICATION_STYLE:
                UserPreferences.setNotificationFoldedToggle(checkState);
                config = UserPreferences.getStatusConfig();
                config.notificationFolded = checkState;
                UserPreferences.setStatusConfig(config);
                NIMClient.updateStatusBarNotificationConfig(config);
            default:
                break;
        }
        item.setChecked(checkState);
    }

    private void setMessageNotify(final boolean checkState) {
        // 如果接入第三方推送（小米），则同样应该设置开、关推送提醒
        // 如果关闭消息提醒，则第三方推送消息提醒也应该关闭。
        // 如果打开消息提醒，则同时打开第三方推送消息提醒。
        NIMClient.getService(MixPushService.class).enable(checkState).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(SettingsActivity.this, R.string.user_info_update_success, Toast.LENGTH_SHORT).show();
                notificationItem.setChecked(checkState);
                setToggleNotification(checkState);
            }

            @Override
            public void onFailed(int code) {
                notificationItem.setChecked(!checkState);
                // 这种情况是客户端不支持第三方推送
                if (code == ResponseCode.RES_UNSUPPORT) {
                    notificationItem.setChecked(checkState);
                    setToggleNotification(checkState);
                } else if (code == ResponseCode.RES_EFREQUENTLY) {
                    Toast.makeText(SettingsActivity.this, R.string.operation_too_frequent, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void setToggleNotification(boolean checkState) {
        try {
            setNotificationToggle(checkState);
            NIMClient.toggleNotification(checkState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotificationToggle(boolean on) {
        UserPreferences.setNotificationToggle(on);
    }

    private void startNoDisturb() {
        NoDisturbActivity.startActivityForResult(this, UserPreferences.getStatusConfig(), noDisturbTime, NoDisturbActivity.NO_DISTURB_REQ);
    }

    private String getIndexCacheSize() {
        long size = NIMClient.getService(LuceneService.class).getCacheSize();
        return String.format("%.2f", size / (1024.0f * 1024.0f));
    }

    private void clearIndex() {
        NIMClient.getService(LuceneService.class).clearCache();
        clearIndexItem.setDetail("0.00 M");
        adapter.notifyDataSetChanged();
    }

    private void updateMultiportPushConfig(final boolean checkState) {
        NIMClient.getService(SettingsService.class).updateMultiportPushConfig(checkState).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(SettingsActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(SettingsActivity.this, "设置失败,code:" + code, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NoDisturbActivity.NO_DISTURB_REQ:
                    setNoDisturbTime(data);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置免打扰时间
     *
     * @param data
     */
    private void setNoDisturbTime(Intent data) {
        boolean isChecked = data.getBooleanExtra(NoDisturbActivity.EXTRA_ISCHECKED, false);
        noDisturbTime = getString(R.string.setting_close);
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (isChecked) {
            config.downTimeBegin = data.getStringExtra(NoDisturbActivity.EXTRA_START_TIME);
            config.downTimeEnd = data.getStringExtra(NoDisturbActivity.EXTRA_END_TIME);
            noDisturbTime = String.format("%s到%s", config.downTimeBegin, config.downTimeEnd);
        } else {
            config.downTimeBegin = null;
            config.downTimeEnd = null;
        }
        disturbItem.setDetail(noDisturbTime);
        adapter.notifyDataSetChanged();
        UserPreferences.setDownTimeToggle(isChecked);
        config.downTimeToggle = isChecked;
        UserPreferences.setStatusConfig(config);
        NIMClient.updateStatusBarNotificationConfig(config);
    }
}
