package com.conglai.leankit.model.message.file;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by chenwei on 16/8/12.
 */

public class IMVideo extends IMFile {

    private int width;
    private int height;
    private int duration;
    private String thumb_key;
    private String thumb_sourceAddr;

    public IMVideo() {
        super.setFormat(IMFile.TYPE_VIDEO);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumb_key() {
        return thumb_key;
    }

    public void setThumb_key(String thumb_key) {
        this.thumb_key = thumb_key;
    }

    public String getThumb_sourceAddr() {
        return thumb_sourceAddr;
    }

    public void setThumb_sourceAddr(String thumb_sourceAddr) {
        this.thumb_sourceAddr = thumb_sourceAddr;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("width", width);
        json.put("height", height);
        json.put("duration", duration);
        json.put("thumb_key", thumb_key);
        json.put("thumb_sourceAddr", thumb_sourceAddr);
        return json;
    }
}
