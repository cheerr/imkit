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
 * Created by chenwei on 16/7/22.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_AUDIO
)
public class IMAudioMessage extends IMFileMessage {

    @AVIMMessageField(
            name = LeanArgs.SOURCE
    )
    String source;
    @AVIMMessageField(
            name = LeanArgs.KEY
    )
    String key;
    @AVIMMessageField(
            name = LeanArgs.DURATION
    )
    int duration;

    @AVIMMessageField(
            name = LeanArgs.READ
    )
    int read;

    public static final Parcelable.Creator<IMAudioMessage> CREATOR = new AVIMMessageCreator<>(IMAudioMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     *
     * @param format
     */
    IMAudioMessage(@NonNull String format) {
        super(format);
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
        return super.checkArgs() && IMFile.TYPE_AUDIO.equals(format)
                && (!TextUtils.isEmpty(source) || !TextUtils.isEmpty(key));
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + duration + read + key + source;
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend() && !TextUtil.isEmpty(key);
    }
}
