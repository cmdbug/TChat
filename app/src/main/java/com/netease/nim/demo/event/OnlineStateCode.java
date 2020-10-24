package com.netease.nim.demo.event;

public enum OnlineStateCode {
    /**
     * 在线
     */
    Online(0),
    /**
     * 忙碌
     */
    Busy(1),
    /**
     * 离线
     */
    Offline(2);


    private int value;

    OnlineStateCode(int netState) {
        this.value = netState;
    }

    public int getValue() {
        return value;
    }

    public static OnlineStateCode getOnlineStateCode(int value) {
        switch (value) {
            case 0:
                return Online;
            case 1:
                return Busy;
            case 2:
                return Offline;
            default:
                return null;
        }
    }
}
