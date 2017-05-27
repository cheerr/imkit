package com.conglai.leankit.engine.store;

import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.dblib.android.GroupChat;
import com.conglai.dblib.android.Message;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.db.GroupChatDbHelper;
import com.conglai.leankit.db.MessageDbHelper;
import com.conglai.leankit.util.MessageUtils;

/**
 * Created by chenwei on 16/7/14.
 */
//IM的数据存储
public class IMStorageController {

    public static void storeMessage(AVIMMessage avimMessage) {
        Message message = MessageUtils.toMessage(avimMessage);
        MessageDbHelper.getInstance(LeanIM.appContext).updateOrSave(message);
        Log.i("IMStorageController", "storeMessage: " + message);
    }

    public static void storeGroupChat(String chatId, String signId) {
        GroupChat groupChat = new GroupChat();
        groupChat.setChatId(chatId);
        groupChat.setGroupId(signId);
        GroupChatDbHelper.getInstance(LeanIM.appContext).updateOrSave(groupChat);
        Log.i("IMStorageController", "storeGroupChat: " + groupChat);
    }
}
