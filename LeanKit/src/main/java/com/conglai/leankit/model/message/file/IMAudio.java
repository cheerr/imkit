package com.conglai.leankit.model.message.file;

import com.alibaba.fastjson.JSONObject;
import com.conglai.dblib.android.Init;

/**
 * Created by chenwei on 16/7/25.
 */

public class IMAudio extends IMFile {

    private int duration;
    private int read = Init.UNREAD;

    public IMAudio() {
        super.setFormat(IMFile.TYPE_AUDIO);
    }

    public void setFormat(String format) {
        super.setFormat(IMFile.TYPE_AUDIO);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("duration", duration);
        json.put("read", read);
        return json;
    }
}
