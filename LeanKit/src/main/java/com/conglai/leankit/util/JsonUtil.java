package com.conglai.leankit.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by chenwei on 16/7/22.
 */

public class JsonUtil {

    public static JSONObject parseJSONObject(String content, String... key) {
        JSONObject jsonObject = JSON.parseObject(content);
        for (int i = 0; key != null && i < key.length; i++) {
            if (!TextUtils.isEmpty(key[i])) {
                jsonObject = jsonObject == null ? null : jsonObject.getJSONObject(key[i]);
            }
        }
        return jsonObject == null ? new JSONObject() : jsonObject;
    }
}
