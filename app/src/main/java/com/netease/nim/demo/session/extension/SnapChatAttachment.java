package com.netease.nim.demo.session.extension;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;

/**
 * Created by zhoujianghua on 2015/7/8.
 */
public class SnapChatAttachment extends FileAttachment {

    private static final String KEY_PATH = "path";
    private static final String KEY_SIZE = "size";
    private static final String KEY_MD5 = "md5";
    private static final String KEY_URL = "url";

    public SnapChatAttachment() {
        super();
    }

    public SnapChatAttachment(JSONObject data) {
        load(data);
    }

    @Override
    public String toJson(boolean send) {
        JSONObject data = new JSONObject();
        try {
            if (!send && !TextUtils.isEmpty(path)) {
                data.put(KEY_PATH, path);
            }

            if (!TextUtils.isEmpty(md5)) {
                data.put(KEY_MD5, md5);
            }

            data.put(KEY_URL, url);
            data.put(KEY_SIZE, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CustomAttachParser.packData(CustomAttachmentType.SnapChat, data);
    }

    private void load(JSONObject data) {
        path = data.getString(KEY_PATH);
        md5 = data.getString(KEY_MD5);
        url = data.getString(KEY_URL);
        size = data.containsKey(KEY_SIZE) ? data.getLong(KEY_SIZE) : 0;
    }
}
