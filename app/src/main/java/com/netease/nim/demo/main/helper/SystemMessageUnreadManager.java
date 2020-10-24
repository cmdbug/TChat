package com.netease.nim.demo.main.helper;

/**
 * Created by huangjun on 2015/8/20.
 */
public class SystemMessageUnreadManager {

    private static SystemMessageUnreadManager instance = new SystemMessageUnreadManager();

    public static SystemMessageUnreadManager getInstance() {
        return instance;
    }

    private int sysMsgUnreadCount = 0;

    public int getSysMsgUnreadCount() {
        return sysMsgUnreadCount;
    }

    public synchronized void setSysMsgUnreadCount(int unreadCount) {
        this.sysMsgUnreadCount = unreadCount;
    }
}
