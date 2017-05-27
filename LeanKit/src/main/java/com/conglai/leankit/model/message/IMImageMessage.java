package com.conglai.leankit.model.message;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.conglai.leankit.model.message.file.IMFile;

/**
 * Created by chenwei on 16/7/14.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_IMAGE
)
public class IMImageMessage extends IMFileMessage {

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
    public static final Parcelable.Creator<IMImageMessage> CREATOR = new AVIMMessageCreator<>(IMImageMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     *
     * @param format
     */
    IMImageMessage(@NonNull String format) {
        super(format);
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
    public String getExtraStr() {
        return super.getExtraStr() + width + height + key + source;
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && IMFile.TYPE_IMG.equals(format)
                && (!TextUtils.isEmpty(source) || !TextUtils.isEmpty(key));
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend() && !TextUtils.isEmpty(key);
    }
}
