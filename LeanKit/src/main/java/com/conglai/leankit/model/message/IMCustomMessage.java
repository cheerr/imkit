package com.conglai.leankit.model.message;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.conglai.dblib.android.Message;
import com.conglai.dblib.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chenwei on 16/7/13.
 */

public abstract class IMCustomMessage extends AVIMTypedMessage {

    @AVIMMessageField(
            name = LeanArgs.ATTRS
    )
    Map<String, Object> attrs = new HashMap<>();

    @AVIMMessageField(
            name = LeanArgs.HIDE
    )
    int hide = 0;
    @AVIMMessageField(
            name = LeanArgs.GROUP
    )
    String groupId;

    @AVIMMessageField(
            name = LeanArgs.PLATFORM
    )
    String platform;

    //本地保存,不上传
    String nativeMessageId;
    int messageFlag;

    public Map<String, Object> getAttrs() {

        //封装基本信息
        return attrs;
    }

    public void setAttrs(Map<String, Object> attr) {
        this.attrs = attr;
    }

    public String getNativeMessageId() {
        return nativeMessageId;
    }

    public void setNativeMessageId(String nativeMessageId) {
        this.nativeMessageId = nativeMessageId;
    }

    public int getMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(int messageFlag) {
        this.messageFlag = messageFlag;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void setMessageType(int messageType) {
        super.setMessageType(messageType);
    }

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMCustomMessage() {
        super();
        nativeMessageId = UUID.randomUUID().toString();
        platform = "android";
        setTimestamp(System.currentTimeMillis());
    }

    /**
     * 从message获取通用信息
     *
     * @param message
     */
    public void gainGeneralMessage(Message message) {
        setFrom(message.getFrom());
        setMessageId(message.getMessageId());
        setContent(message.getContent());
        setMessageType(message.getMessageType());
        setMessageIOType(AVIMMessageIOType.getMessageIOType(message.getIoType() == null ? 0 : message.getIoType()));
        setMessageStatus(AVIMMessageStatus.getMessageStatus(message.getStatus() == null ? 0 : message.getStatus()));
        setConversationId(message.getConversationId());
        setReceiptTimestamp(message.getReceiptTimestamp());
        setNativeMessageId(message.getNativeMessageId());
        setTimestamp(com.conglai.leankit.util.Utils.parseLong(message.getTimestamp()));
        setMessageFlag(message.getMessageFlag() == null ? 0 : message.getMessageFlag());
        setHide(message.getHide());
        setAttrs(null);
    }

    @Override
    public String toString() {
        return Utils.toStringDo(this);
    }

    public String getExtraStr() {
        return Utils.toStringDo(attrs) + nativeMessageId;
    }

    public boolean checkArgs() {
        return !TextUtils.isEmpty(getNativeMessageId());
    }

    public boolean checkCanSend() {
        return checkArgs() && TextUtils.isEmpty(getMessageId());
    }
}
