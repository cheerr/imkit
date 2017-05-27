package com.conglai.leankit.model.query;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.common.Debug;
import com.conglai.leankit.model.message.IMCardMessage;
import com.conglai.leankit.model.message.LeanArgs;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.model.message.file.IMCardMedia;
import com.conglai.leankit.util.JsonUtil;
import com.conglai.leankit.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwei on 16/9/27.
 */

public class CardQuery {

    private static final String CARD_MSG = "cardMsg";


    /**
     * 云端查找
     *
     * @param chat_id
     */
    public static void find(String chat_id, final CallBack<IMCardMessage> callback) {
        if (callback == null || TextUtil.isEmpty(chat_id)) return;
        AVQuery<AVObject> query = new AVQuery<>(CARD_MSG);
        query.whereEqualTo("chatId", chat_id);
        query.whereEqualTo("messageType", MessageFactory.AVIMMessageType_CARD);
        query.limit(999);
        query.orderByDescending("timestamp");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<IMCardMessage> messageList = new ArrayList<>();

                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        messageList.add(toCardMessage(list.get(i)));
                    }
                }
                callback.done(messageList, e);
            }
        });
    }

    private static IMCardMessage toCardMessage(@NonNull AVObject avObject) {
        JSONObject jsonObject = JsonUtil.parseJSONObject(avObject.getString("content"));

        JSONObject media = jsonObject.getJSONObject(LeanArgs.CARD_MEDIA);

        IMCardMessage imCardMessage = MessageFactory.createCardMessage(
                jsonObject.getIntValue(LeanArgs.IS_COMPLETE),
                jsonObject.getIntValue(LeanArgs.CARD_TYPE),
                jsonObject.getString(LeanArgs.CARD_CONTENT),
                IMCardMedia.parse(media));
        imCardMessage.setTaskMsgId(jsonObject.getString(LeanArgs.TASK_MSG_ID));
        imCardMessage.setConversationId(avObject.getString("chatId"));
        imCardMessage.setFrom(avObject.getString("from"));
        imCardMessage.setMessageId(avObject.getString("messageId"));
        imCardMessage.setMessageType(avObject.getInt("messageType"));
        imCardMessage.setMessageFlag(avObject.getInt("messageFlag"));
        imCardMessage.setMessageStatus(AVIMMessage.AVIMMessageStatus.getMessageStatus(avObject.getInt("messageStatusCode")));
        imCardMessage.setReceiptTimestamp(avObject.getLong("receiptTimestamp"));
        imCardMessage.setTimestamp(avObject.getLong("timestamp"));
        imCardMessage.setContent(avObject.getString("content"));


        return imCardMessage;
    }


    /**
     * 保存到云端
     *
     * @param imCardMessage
     */
    public static void save(final IMCardMessage imCardMessage) {

        if (imCardMessage == null || TextUtil.isEmpty(imCardMessage.getConversationId())
                || TextUtil.isEmpty(imCardMessage.getMessageId())
                || TextUtil.isEmpty(imCardMessage.getFrom())) return;

        AVQuery<AVObject> query = new AVQuery<>(CARD_MSG);
        query.whereEqualTo("messageId", imCardMessage.getMessageId());
        query.limit(1);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list == null || list.size() == 0) {
                    AVObject save = new AVObject(CARD_MSG);// 构建对象

                    save.put("chatId", imCardMessage.getConversationId());
                    save.put("from", imCardMessage.getFrom());
                    save.put("messageId", imCardMessage.getMessageId());
                    save.put("messageType", imCardMessage.getMessageType());
                    save.put("messageFlag", imCardMessage.getMessageFlag());
                    save.put("messageStatusCode", imCardMessage.getMessageStatus().getStatusCode());
                    save.put("receiptTimestamp", imCardMessage.getReceiptTimestamp());
                    save.put("timestamp", imCardMessage.getTimestamp());
                    save.put("content", imCardMessage.getContent());

                    save.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            Debug.i("CardQuery", "AVException " + e);
                        }
                    });
                }
            }
        });
    }
}
