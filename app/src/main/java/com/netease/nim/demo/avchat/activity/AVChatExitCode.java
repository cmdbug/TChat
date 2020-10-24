package com.netease.nim.demo.avchat.activity;

/**
 * Created by hzxuwen on 2015/4/24.
 */
public class AVChatExitCode {
    public static final int PEER_HANGUP = 0;

    public static final int PEER_REJECT = 1;

    public static final int HANGUP = 2;

    public static final int NET_CHANGE = 4;

    public static final int REJECT = 5;

    public static final int PEER_BUSY = 6;

    public static final int NET_ERROR = 8;

    public static final int KICKED_OUT = 9;

    public static final int CONFIG_ERROR = 10;

    public static final int PROTOCOL_INCOMPATIBLE_SELF_LOWER = 12;

    public static final int PROTOCOL_INCOMPATIBLE_PEER_LOWER = 13;

    public static final int INVALIDE_CHANNELID = 14;

    public static final int OPEN_DEVICE_ERROR = 15;

    public static final int SYNC_REJECT = 16;

    public static final int SYNC_ACCEPT = 17;

    public static final int SYNC_HANGUP = 18;

    public static final int PEER_NO_RESPONSE = 19; //超时，无人接听

    public static final int CANCEL = 20; //取消

    public static final int LOCAL_CALL_BUSY = 21; // 正在进行本地通话

    public static String getExitString(int code){
        switch (code){
            case PEER_HANGUP:
                return "PEER_HANGUP";
            case PEER_REJECT:
                return "PEER_REJECT";
            case HANGUP:
                return "HANGUP";
            case NET_CHANGE:
                return "NET_CHANGE";
            case REJECT:
                return "REJECT";
            case PEER_BUSY:
                return "PEER_BUSY";
            case NET_ERROR:
                return "NET_ERROR";
            case KICKED_OUT:
                return "KICKED_OUT";
            case CONFIG_ERROR:
                return "CONFIG_ERROR";
            case PROTOCOL_INCOMPATIBLE_SELF_LOWER:
                return "PROTOCOL_INCOMPATIBLE_SELF_LOWER";
            case PROTOCOL_INCOMPATIBLE_PEER_LOWER:
                return "PROTOCOL_INCOMPATIBLE_PEER_LOWER";
            case INVALIDE_CHANNELID:
                return "INVALIDE_CHANNELID";
            case OPEN_DEVICE_ERROR:
                return "OPEN_DEVICE_ERROR";
            case SYNC_REJECT:
                return "SYNC_REJECT";
            case SYNC_ACCEPT:
                return "SYNC_ACCEPT";
            case SYNC_HANGUP:
                return "SYNC_HANGUP";
            case CANCEL:
                return "CANCEL";
            case PEER_NO_RESPONSE:
                return "PEER_NO_RESPONSE";
            case LOCAL_CALL_BUSY:
                return "LOCAL_CALL_BUSY";
            default:
                return "UNKNOWN";
        }
    }

}
