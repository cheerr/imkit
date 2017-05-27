package com.conglai.leankit.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.conglai.dblib.android.Message;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.model.message.IMAudioMessage;
import com.conglai.leankit.model.message.IMCardMessage;
import com.conglai.leankit.model.message.IMCustomMessage;
import com.conglai.leankit.model.message.IMGifEmojiMessage;
import com.conglai.leankit.model.message.IMImageMessage;
import com.conglai.leankit.model.message.IMLocationMessage;
import com.conglai.leankit.model.message.IMPiTuMessage;
import com.conglai.leankit.model.message.IMReadCallBackMessage;
import com.conglai.leankit.model.message.IMTextMessage;
import com.conglai.leankit.model.message.IMVideoMessage;
import com.conglai.leankit.model.message.LeanArgs;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.model.message.file.IMAudio;
import com.conglai.leankit.model.message.file.IMCardMedia;
import com.conglai.leankit.model.message.file.IMGif;
import com.conglai.leankit.model.message.file.IMHalfPiTu;
import com.conglai.leankit.model.message.file.IMPhoto;
import com.conglai.leankit.model.message.file.IMVideo;
import com.conglai.leankit.model.user.IMUser;

/**
 * Created by chenwei on 16/7/21.
 */

public class MessageUtils {

    /**
     * @param avimMessage
     * @return
     */
    public static Message toMessage(AVIMMessage avimMessage) {
        if (avimMessage == null) return null;
        Message message = new Message();
        if (avimMessage instanceof IMCustomMessage) {
            String nativeId = ((IMCustomMessage) avimMessage).getNativeMessageId();
            if (!TextUtils.isEmpty(nativeId)) {
                message.setNativeMessageId(nativeId);
            }
            message.setMessageFlag(((IMCustomMessage) avimMessage).getMessageFlag());
        }
        message.setStatus(avimMessage.getMessageStatus().getStatusCode());
        message.setContent(avimMessage.getContent());
        message.setConversationId(avimMessage.getConversationId());
        message.setFrom(avimMessage.getFrom());
        message.setIoType(avimMessage.getMessageIOType().getIOType());
        message.setMessageId(avimMessage.getMessageId());
        message.setReceiptTimestamp(avimMessage.getReceiptTimestamp());
        message.setTimestamp(avimMessage.getTimestamp());

        JSONObject jsonObject = JsonUtil.parseJSONObject(avimMessage.getContent());
        message.setMessageType(jsonObject.getIntValue(LeanArgs.TYPE));
        message.setHide(jsonObject.getIntValue(LeanArgs.HIDE));

        return message;
    }

    /**
     * 转成通用类型
     *
     * @param message
     * @return
     */
    public static IMCustomMessage messageToCustomMessage(Message message) {
        switch (message.getMessageType()) {
            case MessageFactory.AVIMMessageType_TEXT:
                return messageToIMTextMessage(message);
            case MessageFactory.AVIMMessageType_AUDIO:
                return messageToIMAudioMessage(message);
            case MessageFactory.AVIMMessageType_IMAGE:
                return messageToIMImageMessage(message);
            case MessageFactory.AVIMMessageType_LOCATION:
                return messageToIMLocationMessage(message);
            case MessageFactory.AVIMMessageType_VIDEO:
                return messageToIMVideoMessage(message);
            case MessageFactory.AVIMMessageType_READ_CALLBACK:
                return messageToIMReadCallBackMessage(message);
            case MessageFactory.AVIMMessageType_GIF_EMOJI:
                return messageToIMGifEmojiMessage(message);
            case MessageFactory.AVIMMessageType_PiTu:
                return messageToIMPiTuMessage(message);
            case MessageFactory.AVIMMessageType_CARD:
                return messageToIMCardMessage(message);
        }
        return null;
    }

    /**
     * 从message里提取IMTextMessage信息
     *
     * @param message
     * @return
     */
    public static IMTextMessage messageToIMTextMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_TEXT)
            return null;
        String text = JsonUtil.parseJSONObject(message.getContent()).getString(LeanArgs.TEXT);
        IMTextMessage imTextMessage = MessageFactory.createTextMessage(text);
        imTextMessage.gainGeneralMessage(message);
        return imTextMessage;
    }


    /**
     * 从message里提取IMImageMessage信息
     *
     * @param message
     * @return
     */
    public static IMImageMessage messageToIMImageMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_IMAGE)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMPhoto photo = new IMPhoto();
        photo.setSource(jsonObject.getString(LeanArgs.SOURCE));
        photo.setKey(jsonObject.getString(LeanArgs.KEY));

        IMImageMessage imImageMessage = MessageFactory.createImageMessage(photo);
        imImageMessage.gainGeneralMessage(message);
        imImageMessage.setWidth(jsonObject.getIntValue(LeanArgs.WIDTH));
        imImageMessage.setHeight(jsonObject.getIntValue(LeanArgs.HEIGHT));
        return imImageMessage;
    }

    /**
     * 从message里提取IMImageMessage信息
     *
     * @param message
     * @return
     */
    public static IMVideoMessage messageToIMVideoMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_VIDEO)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMVideo video = new IMVideo();
        video.setSource(jsonObject.getString(LeanArgs.SOURCE));
        video.setKey(jsonObject.getString(LeanArgs.KEY));

        IMVideoMessage imVideoMessage = MessageFactory.createVideoMessage(video);
        imVideoMessage.gainGeneralMessage(message);
        imVideoMessage.setWidth(jsonObject.getIntValue(LeanArgs.WIDTH));
        imVideoMessage.setHeight(jsonObject.getIntValue(LeanArgs.HEIGHT));
        imVideoMessage.setDuration(jsonObject.getIntValue(LeanArgs.DURATION));
        imVideoMessage.setThumb_key(jsonObject.getString(LeanArgs.THUMBNAIL_KEY));
        imVideoMessage.setThumb_source(jsonObject.getString(LeanArgs.THUMBNAIL_SOURCE));
        return imVideoMessage;
    }

    /**
     * 从message里提取IMAudioMessage信息
     *
     * @param message
     * @return
     */
    public static IMAudioMessage messageToIMAudioMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_AUDIO)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMAudio audio = new IMAudio();
        audio.setSource(jsonObject.getString(LeanArgs.SOURCE));
        audio.setKey(jsonObject.getString(LeanArgs.KEY));

        IMAudioMessage imAudioMessage = MessageFactory.createAudioMessage(audio);
        imAudioMessage.gainGeneralMessage(message);
        imAudioMessage.setDuration(jsonObject.getIntValue(LeanArgs.DURATION));
        imAudioMessage.setRead(jsonObject.getIntValue(LeanArgs.READ));
        return imAudioMessage;
    }

    public static IMLocationMessage messageToIMLocationMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_LOCATION)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMLocationMessage imImageMessage =
                MessageFactory.createLocationMessage(jsonObject.getDoubleValue(LeanArgs.LATITUDE)
                        , jsonObject.getDoubleValue(LeanArgs.LONGITUDE), jsonObject.getString(LeanArgs.ADDRESS));
        imImageMessage.gainGeneralMessage(message);
        return imImageMessage;
    }

    public static IMReadCallBackMessage messageToIMReadCallBackMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_READ_CALLBACK)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMReadCallBackMessage imReadCallBackMessage =
                MessageFactory.createReadCallBackMessage(jsonObject.getString(LeanArgs.RECEIPT_MSG_ID)
                        , jsonObject.getIntValue(LeanArgs.RECEIPT_MSG_TYPE));
        imReadCallBackMessage.setRec_date(jsonObject.getLongValue(LeanArgs.RECEIPT_MSG_DATE));
        imReadCallBackMessage.gainGeneralMessage(message);
        return imReadCallBackMessage;
    }

    public static IMGifEmojiMessage messageToIMGifEmojiMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_GIF_EMOJI)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        IMGif imGif = new IMGif();
        imGif.setGifExpression(jsonObject.getString(LeanArgs.GIF_EXP));
        imGif.setGifKey(jsonObject.getString(LeanArgs.GIF_KEY));
        imGif.setGifName(jsonObject.getString(LeanArgs.GIF_NAME));
        imGif.setGifPck(jsonObject.getString(LeanArgs.GIF_PCK));

        IMGifEmojiMessage imGifEmojiMessage = MessageFactory.createGifEmojiMessage(imGif);
        imGifEmojiMessage.gainGeneralMessage(message);
        return imGifEmojiMessage;
    }

    public static IMPiTuMessage messageToIMPiTuMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_PiTu)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());

        JSONObject left = jsonObject.getJSONObject(LeanArgs.LEFT);
        JSONObject right = jsonObject.getJSONObject(LeanArgs.RIGHT);


        IMPiTuMessage imPiTuMessage = MessageFactory.createPiTuMessage(
                jsonObject.getIntValue(LeanArgs.IS_COMPLETE),
                jsonObject.getString(LeanArgs.COMPLETED_IMG),
                jsonObject.getString(LeanArgs.COMPLETED_SOURCE),
                IMHalfPiTu.parse(left), IMHalfPiTu.parse(right));
        imPiTuMessage.setTaskMsgId(jsonObject.getString(LeanArgs.TASK_MSG_ID));
        imPiTuMessage.gainGeneralMessage(message);
        return imPiTuMessage;
    }

    public static IMCardMessage messageToIMCardMessage(Message message) {
        if (message == null || message.getMessageType() != MessageFactory.AVIMMessageType_CARD)
            return null;
        JSONObject jsonObject = JsonUtil.parseJSONObject(message.getContent());
        JSONObject media = jsonObject.getJSONObject(LeanArgs.CARD_MEDIA);

        IMCardMessage imCardMessage = MessageFactory.createCardMessage(
                jsonObject.getIntValue(LeanArgs.IS_COMPLETE),
                jsonObject.getIntValue(LeanArgs.CARD_TYPE),
                jsonObject.getString(LeanArgs.CARD_CONTENT),
                IMCardMedia.parse(media));
        imCardMessage.setTaskMsgId(jsonObject.getString(LeanArgs.TASK_MSG_ID));
        imCardMessage.gainGeneralMessage(message);
        return imCardMessage;
    }

    /**
     * 从message取User信息
     *
     * @param message
     * @return
     */
    public static IMUser getUserFromMessage(Message message) {
        if (message == null) return null;
        //默认先从本地取,取不到从数据里取
        IMUser user = LeanIM.getInstance().getIMUser(message.getFrom());
        if (user == null) {
            user = new IMUser(message.getFrom());
        }
        return user;
    }
}
