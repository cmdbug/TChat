package com.netease.nim.demo.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by WZTENG on 2017/5/9.
 */
public class RedPackedAttachment extends CustomAttachment {

    private String redPackedNameLabel;//文本
    private String redPackedFromLabel;//文本
    private float redPackedMoney;//金领
    private byte flag;//是否领取

    public RedPackedAttachment() {
        super(CustomAttachmentType.RED);
    }

    @Override
    protected void parseData(JSONObject data) {
        this.flag = data.getByte("flag");
        this.redPackedMoney = data.getFloatValue("money");
        this.redPackedNameLabel = data.getString("label");
        this.redPackedFromLabel = data.getString("from");
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("flag", flag);
        data.put("money", redPackedMoney);
        data.put("label", redPackedNameLabel);
        data.put("from", redPackedFromLabel);
        return data;
    }

    public String getRedPackedNameLabel() {
        return redPackedNameLabel;
    }

    public void setRedPackedNameLabel(String redPackedNameLabel) {
        this.redPackedNameLabel = redPackedNameLabel;
    }

    public float getRedPackedMoney() {
        return redPackedMoney;
    }

    public void setRedPackedMoney(float redPackedMoney) {
        this.redPackedMoney = redPackedMoney;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getRedPackedFromLabel() {
        return redPackedFromLabel;
    }

    public void setRedPackedFromLabel(String redPackedFromLabel) {
        this.redPackedFromLabel = redPackedFromLabel;
    }
}
