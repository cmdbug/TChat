package com.netease.nim.demo.config;

public class DemoServers {

    //
    // 好友列表信息服务器地址
    //
    private static final String API_SERVER_TEST = "http://223.252.220.238:8080/api/"; // 测试
    private static final String API_SERVER = "https://app.netease.im/api/"; // 线上

    public static final String apiServer() {
        return ServerConfig.testServer() ? API_SERVER_TEST : API_SERVER;
    }

    public static final String chatRoomAPIServer() {
        return apiServer() + "chatroom/";
    }
}
