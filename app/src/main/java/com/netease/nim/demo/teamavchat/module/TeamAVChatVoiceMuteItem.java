package com.netease.nim.demo.teamavchat.module;

/**
 * Created by hzchenkang on 2017/5/9.
 */

public class TeamAVChatVoiceMuteItem {
    private String account;
    private String displayName;
    private boolean mute;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}
