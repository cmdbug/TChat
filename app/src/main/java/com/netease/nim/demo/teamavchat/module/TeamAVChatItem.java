package com.netease.nim.demo.teamavchat.module;

/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatItem {
    public interface TYPE {
        int TYPE_ADD = 0; // 添加
        int TYPE_DATA = 1; // 用户
        int TYPE_HOLDER = 2; // 占位
    }

    public interface STATE {
        int STATE_WAITING = 0; // 等待接听
        int STATE_PLAYING = 1; // 接听中
        int STATE_END = 2; // 未接听
        int STATE_HANGUP = 3; // 已挂断
    }

    public int type; // 类型：0 加号；1 正常surface
    public int state; // 当前状态：0 等待 1 正在播放 2 未接通 3 已挂断
    public boolean videoLive; // 是否正在视频
    public int volume; // 音频音量
    public String teamId;
    public String account;

    public TeamAVChatItem(int type, String teamId, String account) {
        this.type = type;
        this.teamId = teamId;
        this.account = account;
        this.state = STATE.STATE_WAITING;
        this.videoLive = false;
        this.volume = 0;
    }

    public TeamAVChatItem(String teamId) {
        this.teamId = teamId;
        this.type = TYPE.TYPE_HOLDER;
    }
}
