package com.conglai.leankit.model.message.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.conglai.leankit.util.TextUtil;

import java.io.Serializable;

/**
 * Created by chenwei on 16/9/27.
 */

public class IMCardMedia implements Serializable {

    public static final int TYPE_EMPTY = 0, TYPE_IMG = 1, TYPE_VIDEO = 2, TYPE_AUDIO = 3;

    private int type = TYPE_EMPTY;

    private IMAudio audio;
    private IMVideo video;
    private IMPhoto image;

    public static boolean hasType(int type) {
        return type == TYPE_IMG || type == TYPE_VIDEO || type == TYPE_AUDIO;
    }

    public boolean isEmpty() {
        return audio != null || video != null || image != null;
    }

    public IMAudio getAudio() {
        return audio;
    }

    public void setAudio(IMAudio audio) {
        this.audio = audio;
        if (audio != null) {
            type = TYPE_AUDIO;
        }
    }

    public IMVideo getVideo() {
        return video;
    }

    public void setVideo(IMVideo video) {
        this.video = video;
        if (video != null) {
            type = TYPE_VIDEO;
        }
    }

    public IMPhoto getImage() {
        return image;
    }

    public void setImage(IMPhoto image) {
        this.image = image;
        if (image != null) {
            type = TYPE_IMG;
        }
    }

    public int getType() {
        return type;
    }

    public static IMCardMedia parse(JSONObject jsonObject) {
        if (jsonObject == null) return new IMCardMedia();
        return JSON.parseObject(jsonObject.toString(), IMCardMedia.class);
    }

    /**
     * 是否已经上传到七牛
     *
     * @return
     */
    public boolean isUploaded() {
        switch (type) {
            case TYPE_AUDIO:
                if (audio != null)
                    return !TextUtil.isEmpty(audio.getKey());
                break;
            case TYPE_IMG:
                if (image != null)
                    return !TextUtil.isEmpty(image.getKey());
                break;
            case TYPE_VIDEO:
                if (video != null)
                    return !TextUtil.isEmpty(video.getKey()) && !TextUtil.isEmpty(video.getThumb_key());
                break;
        }
        return false;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        switch (type) {
            case TYPE_AUDIO:
                if (audio != null)
                    json.put("audio", audio.toJson());
                break;
            case TYPE_IMG:
                if (image != null)
                    json.put("image", image.toJson());
                break;
            case TYPE_VIDEO:
                if (video != null)
                    json.put("video", video.toJson());
                break;
        }
        if (!json.isEmpty()) {
            json.put("type", type);
        }
        return json;
    }
}
