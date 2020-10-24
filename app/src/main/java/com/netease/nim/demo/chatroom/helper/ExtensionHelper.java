package com.netease.nim.demo.chatroom.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 扩展字段Map和String转换工具类
 */
public class ExtensionHelper {

    private static final String TAG = ExtensionHelper.class.getSimpleName();

    public static String getJsonStringFromMap(final Map<String, Object> map) {
        String result = null;
        if (map != null && !map.isEmpty()) {
            try {
                JSONObject json = new JSONObject(map);
                result = json.toString();
            } catch (Exception e) {
                LogUtil.e(TAG, "getJsonStringFromMap exception =" + e.getMessage());
            }
        }

        return result;
    }

    public static Map<String, Object> getMapFromJsonString(final String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        try {
            org.json.JSONObject json = new org.json.JSONObject(jsonStr);
            return recursiveParseJsonObject(json);
        } catch (org.json.JSONException e) {
            LogUtil.e(TAG, "getMapFromJsonString exception =" + e.getMessage());
        }

        return null;
    }

    private static Map<String, Object> recursiveParseJsonObject(org.json.JSONObject json) throws org.json.JSONException {
        if (json == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(json.length());
        String key;
        Object value;
        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            key = i.next();
            value = json.get(key);
            if (value instanceof org.json.JSONArray) {
                map.put(key, recursiveParseJsonArray((org.json.JSONArray) value));
            } else if (value instanceof org.json.JSONObject) {
                map.put(key, recursiveParseJsonObject((org.json.JSONObject) value));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    private static List recursiveParseJsonArray(org.json.JSONArray array) throws org.json.JSONException {
        if (array == null) {
            return null;
        }

        List list = new ArrayList(array.length());
        Object value;
        for (int m = 0; m < array.length(); m++) {
            value = array.get(m);
            if (value instanceof org.json.JSONArray) {
                list.add(recursiveParseJsonArray((org.json.JSONArray) value));
            } else if (value instanceof org.json.JSONObject) {
                list.add(recursiveParseJsonObject((org.json.JSONObject) value));
            } else {
                list.add(value);
            }
        }

        return list;
    }
}
