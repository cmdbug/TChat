package com.netease.nim.uikit.wzteng.translation.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by WZTENG on 2017/04/13 0013.
 */

public class BasicExplain implements Serializable{
    private String phonetic;//发音
    private String phoneticUK;//英式发音
    private String phoneticUS;//美式发音
    private String[] explains;//释义

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getPhoneticUK() {
        return phoneticUK;
    }

    public void setPhoneticUK(String phoneticUK) {
        this.phoneticUK = phoneticUK;
    }

    public String getPhoneticUS() {
        return phoneticUS;
    }

    public void setPhoneticUS(String phoneticUS) {
        this.phoneticUS = phoneticUS;
    }

    public String[] getExplains() {
        return explains;
    }

    public void setExplains(String[] explains) {
        this.explains = explains;
    }

    @Override
    public String toString() {
        return "BasicExplain [phonetic=" + phonetic + ", phoneticUK="
                + phoneticUK + ", phoneticUS=" + phoneticUS + ", explains="
                + Arrays.toString(explains) + "]";
    }
}
