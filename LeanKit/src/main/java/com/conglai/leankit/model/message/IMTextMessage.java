package com.conglai.leankit.model.message;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;

/**
 * Created by chenwei on 16/7/14.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_TEXT
)
public class IMTextMessage extends IMCustomMessage {

    @AVIMMessageField(
            name = LeanArgs.TEXT
    )
    String text;
    public static final Creator<IMTextMessage> CREATOR = new AVIMMessageCreator<>(IMTextMessage.class);

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMTextMessage() {
        super();
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && !TextUtils.isEmpty(text);
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend();
    }
}
