package com.netease.nim.demo.event;

public enum NetStateCode {
    /**
     * 未知
     */
    Unkown(0),
    /**
     * wifi
     */
    Wifi(1),
    /**
     * WWAN
     */
    WWAN(2),
    /**
     * 2G
     */
    _2G(3),
    /**
     * 3G
     */
    _3G(4),
    /**
     * 4G
     */
    _4G(5);

    private int value;

    NetStateCode(int netState) {
        this.value = netState;
    }

    public int getValue() {
        return value;
    }

    public static NetStateCode getNetStateCode(int value) {
        switch (value) {
            case 0:
                return Unkown;
            case 1:
                return Wifi;
            case 2:
                return WWAN;
            case 3:
                return _2G;
            case 4:
                return _3G;
            case 5:
                return _4G;
            default:
                return null;
        }
    }
}
