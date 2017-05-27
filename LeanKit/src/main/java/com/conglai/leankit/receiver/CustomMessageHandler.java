package com.conglai.leankit.receiver;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.conglai.common.Debug;
import com.conglai.leankit.engine.store.IMStorageController;

public abstract class CustomMessageHandler extends AVIMMessageHandler {

    //接收到消息后的处理逻辑
    @Override
    public final void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        if (message == null) return;
        Debug.i("CustomMessageHandler", "message" + message.getContent());
        message.setMessageStatus(AVIMMessage.AVIMMessageStatus.AVIMMessageStatusReceipt);
        IMStorageController.storeMessage(message);
        onReceivedMessage(message, conversation, client);
    }

    public abstract void onReceivedMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client);

    @Override
    public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {

    }
}