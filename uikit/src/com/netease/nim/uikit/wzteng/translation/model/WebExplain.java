package com.netease.nim.uikit.wzteng.translation.model;

import java.util.Arrays;

/**
 * Created by WZTENG on 2017/04/13 0013.
 */

/**
 * 网络释义
 */
public class WebExplain {
    private String key;
    private String[] values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "WebExplain [key=" + key + ", values=" + Arrays.toString(values)
                + "]";
    }
}
