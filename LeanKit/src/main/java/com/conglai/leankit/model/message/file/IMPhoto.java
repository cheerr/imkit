package com.conglai.leankit.model.message.file;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by chenwei on 16/7/25.
 */

public class IMPhoto extends IMFile {

    private int width;
    private int height;

    public IMPhoto() {
        super.setFormat(IMFile.TYPE_IMG);
    }

    public void setFormat(String format) {
        super.setFormat(IMFile.TYPE_IMG);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("width", width);
        json.put("height", height);
        return json;
    }
}
