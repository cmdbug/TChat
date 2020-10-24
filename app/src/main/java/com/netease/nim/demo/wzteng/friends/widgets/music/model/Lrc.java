package com.netease.nim.demo.wzteng.friends.widgets.music.model;

import com.google.gson.annotations.SerializedName;

public class Lrc {
    @SerializedName("lrcContent")
    private String lrcContent;

    public String getLrcContent() {
        return lrcContent;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }
}
