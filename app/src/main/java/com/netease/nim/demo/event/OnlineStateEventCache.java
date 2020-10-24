package com.netease.nim.demo.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hzchenkang on 2017/4/11.
 */

public class OnlineStateEventCache {

    private static Map<String, OnlineState> onlineStateCache = new HashMap<>();

    // 管理在线状态订阅账号
    private static Set<String> subscribeAccounts = new HashSet<>();

    public static OnlineState getOnlineState(String account) {
        return onlineStateCache.get(account);
    }

    public static void cacheOnlineState(String account, OnlineState state) {
        onlineStateCache.put(account, state);
    }

    public static void removeOnlineState(List<String> accounts) {
        if (accounts != null && !accounts.isEmpty()) {
            for (String account : accounts) {
                onlineStateCache.remove(account);
            }
        }
    }

    public static void addSubsAccounts(List<String> accounts) {
        subscribeAccounts.addAll(accounts);
    }

    public static void addSubsAccount(String accounts) {
        subscribeAccounts.add(accounts);
    }

    public static boolean hasSubscribed(String account) {
        return subscribeAccounts.contains(account);
    }

    public static void removeSubsAccounts(List<String> accounts) {
        subscribeAccounts.removeAll(accounts);
    }

    public static List<String> getSubsAccounts() {
        return new ArrayList<>(subscribeAccounts);
    }

    public static void resetCache() {
        onlineStateCache.clear();
        subscribeAccounts.clear();
    }

    public static void clearSubsAccounts() {
        subscribeAccounts.clear();
    }
}
