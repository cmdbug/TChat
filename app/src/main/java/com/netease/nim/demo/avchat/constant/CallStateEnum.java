package com.netease.nim.demo.avchat.constant;

/**
 * 呼叫状态,用来控制刷新界面
 * Created by hzxuwen on 2015/4/27.
 */
public enum CallStateEnum {
    INVALID(-1), //无效的状态,该状态下无界面显示

    VIDEO(0), //正在进行视频通话(发起者)
    OUTGOING_VIDEO_CALLING(2), //邀请好友视频通话
    INCOMING_VIDEO_CALLING(4),
    OUTGOING_AUDIO_TO_VIDEO(6), //向好友发起从语音切换到视频的邀请
    VIDEO_CONNECTING(8), //视频通话连接中
    VIDEO_OFF(10), // 对方关闭摄像头

    AUDIO(1), //正在进行语音通话(发起者)
    OUTGOING_AUDIO_CALLING(3), //邀请好友语音通话
    INCOMING_AUDIO_CALLING(5), //来自好友的视频通话、语音通话邀请
    INCOMING_AUDIO_TO_VIDEO(7), //音频切换为视频的邀请
    AUDIO_CONNECTING(9); //语音通话连接中

    private int value;

    CallStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean isVideoMode(CallStateEnum value) {
        return value.getValue() % 2 == 0;
    }

    public static boolean isAudioMode(CallStateEnum value) {
        return value.getValue() % 2 == 1;
    }

    public static CallStateEnum getCallStateEnum(int value) {
        for (CallStateEnum e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }

        return INVALID;
    }
}
