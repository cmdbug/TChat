package com.netease.nim.demo.event;

/**
 * 在线状态
 */

public class OnlineState {

    /**
     * 客户端类型，参照 {@link com.netease.nimlib.sdk.auth.ClientType}
     */
    private int onlineClient;

    /**
     * 网络状态，WIFI,4G,3G,2G
     */
    private NetStateCode netState;

    /**
     * 在线状态，0 在线  1 忙碌  2 离开
     */
    private OnlineStateCode onlineState;

    public OnlineState(int onlineClient, int netState, int onlineState) {
        this.onlineClient = onlineClient;
        this.netState = NetStateCode.getNetStateCode(netState);
        this.onlineState = OnlineStateCode.getOnlineStateCode(onlineState);
    }

    public OnlineState(int onlineClient, NetStateCode netState, OnlineStateCode onlineState) {
        this.onlineClient = onlineClient;
        this.netState = netState;
        this.onlineState = onlineState;
    }

    /**
     * 获取在线状态
     *
     * @return onlineState
     */
    public OnlineStateCode getOnlineState() {
        return onlineState;
    }

    /**
     * 获取在线客户端类型
     *
     * @return onlineClient
     */
    public int getOnlineClient() {
        return onlineClient;
    }

    /**
     * 获取网络状态
     *
     * @return netState
     */
    public NetStateCode getNetState() {
        return netState;
    }
}
