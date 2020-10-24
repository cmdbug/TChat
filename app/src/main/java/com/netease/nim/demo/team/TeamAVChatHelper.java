package com.netease.nim.demo.team;

import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.avchat.AVChatProfile;
import com.netease.nim.demo.common.infra.Handlers;
import com.netease.nim.demo.teamavchat.activity.TeamAVChatActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hzchenkang on 2017/5/5.
 */

public class TeamAVChatHelper {

    private static final String KEY_ID = "id";
    private static final String KEY_MEMBER = "members";
    private static final String KEY_TID = "teamId";
    private static final String KEY_RID = "room";
    private static final String KEY_TNAME = "teamName";

    private static final long OFFLINE_EXPIRY = 45 * 1000;

    private static final int ID = 3;

    private boolean isTeamAVChatting = false;
    private boolean isSyncComplete = true; // 未开始也算同步完成，可能存在不启动同步的情况

    public String buildContent(String roomName, String teamID, List<String> accounts, String teamName) {
        JSONObject json = new JSONObject();
        json.put(KEY_ID, ID);
        JSONArray array = new JSONArray();
        array.add(DemoCache.getAccount());
        for (String account : accounts) {
            array.add(account);
        }
        json.put(KEY_MEMBER, array);
        json.put(KEY_TID, teamID);
        json.put(KEY_RID, roomName);
        json.put(KEY_TNAME, teamName);
        return json.toString();
    }

    private JSONObject parseContentJson(CustomNotification notification) {
        if (notification != null) {
            String content = notification.getContent();
            return JSONObject.parseObject(content);
        }
        return null;
    }

    private boolean isTeamAVChatInvite(JSONObject json) {
        if (json != null) {
            int id = json.getInteger(KEY_ID);
            return id == ID;
        }
        return false;
    }

    /**
     * 监听自定义通知消息，id = 3 是群视频邀请
     *
     * @param register
     */

    private Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            try {
                JSONObject jsonObject = parseContentJson(customNotification);
                // 收到群视频邀请
                if (isTeamAVChatInvite(jsonObject)) {
                    final String roomName = jsonObject.getString(KEY_RID);
                    final String teamId = jsonObject.getString(KEY_TID);
                    JSONArray accountArray = jsonObject.getJSONArray(KEY_MEMBER);
                    final ArrayList<String> accounts = new ArrayList<>();
                    final String teamName = jsonObject.getString(KEY_TNAME);
                    if (accountArray != null) {
                        for (Object o : accountArray) {
                            accounts.add((String) o);
                        }
                    }

                    // 接收到群视频邀请，启动来点界面
                    LogUtil.ui("receive team video chat notification " + teamId + " room " + roomName);
                    if (isTeamAVChatting || AVChatProfile.getInstance().isAVChatting()) {
                        LogUtil.ui("cancel launch team av chat isTeamAVChatting = " + isTeamAVChatting);
                        Toast.makeText(DemoCache.getContext(), "正在进行视频通话", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LogUtil.ui("isSyncComplete = " + isSyncComplete);
                    if (isSyncComplete || !checkOfflineOutTime(customNotification)) {
                        isTeamAVChatting = true;
                        launchActivity(teamId, roomName, accounts, teamName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void launchActivity(final String teamId, final String roomName, final ArrayList<String> accounts, final String teamName) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 欢迎界面正在运行，则等MainActivity启动之后再启动，否则直接启动 TeamAVChatActivity
                if (!DemoCache.isMainTaskLaunching()) {
                    TeamAVChatActivity.startActivity(DemoCache.getContext(), true, teamId, roomName, accounts, teamName);
                } else {
                    LogUtil.ui("launch TeamAVChatActivity delay for WelComeActivity is Launching");
                    launchActivity(teamId, roomName, accounts, teamName);
                }
            }
        };

        Handlers.sharedHandler(DemoCache.getContext()).postDelayed(r, 200);
    }

    private Observer<LoginSyncStatus> loginSyncStatusObserver = new Observer<LoginSyncStatus>() {
        @Override
        public void onEvent(LoginSyncStatus loginSyncStatus) {
            isSyncComplete = (loginSyncStatus == LoginSyncStatus.SYNC_COMPLETED ||
                    loginSyncStatus == LoginSyncStatus.NO_BEGIN);
        }
    };

    public boolean checkOfflineOutTime(CustomNotification notification) {
        // 时间差在45s内，考虑本地时间误差，条件适当放宽
        long time = TimeUtil.currentTimeMillis() - notification.getTime();
        LogUtil.ui("rev offline team AVChat request time = " + time);
        return time > OFFLINE_EXPIRY || time < -OFFLINE_EXPIRY;
    }

    public void setTeamAVChatting(boolean teamAVChatting) {
        isTeamAVChatting = teamAVChatting;
    }

    public boolean isTeamAVChatting() {
        return isTeamAVChatting;
    }

    public void registerObserver(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(loginSyncStatusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, register);
    }

    public static TeamAVChatHelper sharedInstance() {
        return InstanceHolder.teamAVChatHelper;
    }

    private static class InstanceHolder {
        private final static TeamAVChatHelper teamAVChatHelper = new TeamAVChatHelper();
    }
}
