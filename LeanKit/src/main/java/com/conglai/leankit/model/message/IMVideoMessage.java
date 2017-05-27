package com.conglai.leankit.model.message;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.conglai.leankit.model.message.file.IMFile;
import com.conglai.leankit.util.TextUtil;

/**
 * Created by chenwei on 16/8/12.
 */

@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_VIDEO
)
public class IMVideoMessage extends IMFileMessage {

    @AVIMMessageField(
            name = LeanArgs.SOURCE
    )
    String source;
    @AVIMMessageField(
            name = LeanArgs.KEY
    )
    String key;
    @AVIMMessageField(
            name = LeanArgs.WIDTH
    )
    int width;
    @AVIMMessageField(
            name = LeanArgs.HEIGHT
    )
    int height;
    @AVIMMessageField(
            name = LeanArgs.DURATION
    )
    int duration;
    @AVIMMessageField(
            name = LeanArgs.THUMBNAIL_KEY
    )
    String thumb_key;
    @AVIMMessageField(
            name = LeanArgs.THUMBNAIL_SOURCE
    )
    String thumb_source;
    public static final Parcelable.Creator<IMVideoMessage> CREATOR = new AVIMMessageCreator<>(IMVideoMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     *
     * @param format
     */
    IMVideoMessage(@NonNull String format) {
        super(format);
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public String getThumb_key() {
        return thumb_key;
    }

    public void setThumb_key(String thumb_key) {
        this.thumb_key = thumb_key;
    }

    public String getThumb_source() {
        return thumb_source;
    }

    public void setThumb_source(String thumb_source) {
        this.thumb_source = thumb_source;
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
    public boolean checkArgs() {
        return super.checkArgs() && IMFile.TYPE_VIDEO.equals(format)
                && (!TextUtils.isEmpty(thumb_source) || !TextUtils.isEmpty(thumb_key))
                && (!TextUtils.isEmpty(source) || !TextUtils.isEmpty(key));
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + duration + width + height + thumb_key + thumb_source + key + source;
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend() && !TextUtils.isEmpty(thumb_key) && !TextUtil.isEmpty(key);
    }
}
