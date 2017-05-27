package com.conglai.leankit.model.message.file;

import com.alibaba.fastjson.JSONObject;
import com.conglai.dblib.util.Utils;

import java.io.Serializable;

/**
 * Created by chenwei on 16/7/13.
 */

public class IMFile implements Serializable {

    public static final String TYPE_NULL = "",
            TYPE_IMG = "image", TYPE_VIDEO = "video", TYPE_AUDIO = "audio", TYPE_PiTu = "pitu", TYPE_CARD = "card";

    //文件格式
    private String format = TYPE_NULL;
    //本地的path
    private String source;
    //七牛的key
    private String key;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return Utils.toStringDo(this);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("format", format);
        json.put("source", source);
        json.put("key", key);
        return json;
    }
}
