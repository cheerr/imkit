package com.conglai.leankit.model.message;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.conglai.leankit.model.message.file.IMCardMedia;
import com.conglai.leankit.model.message.file.IMFile;
import com.conglai.leankit.util.TextUtil;

/**
 * Created by chenwei on 16/9/27.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_CARD
)
public class IMCardMessage extends IMFileMessage {

    @AVIMMessageField(
            name = LeanArgs.IS_COMPLETE
    )
    int isComplete;  //1表示已完成,0表示未完成
    @AVIMMessageField(
            name = LeanArgs.TASK_MSG_ID
    )
    String taskMsgId;
    @AVIMMessageField(
            name = LeanArgs.CARD_CONTENT
    )
    String card_content;
    @AVIMMessageField(
            name = LeanArgs.CARD_MEDIA
    )
    JSONObject card_media;
    @AVIMMessageField(
            name = LeanArgs.CARD_TYPE
    )
    int card_type; //1表示图片,2表示视频,3表示语音  对应IMCardMedia.type

    public static final Creator<IMCardMessage> CREATOR = new AVIMMessageCreator<>(IMCardMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     *
     * @param format
     */
    IMCardMessage(@NonNull String format) {
        super(format);
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public String getTaskMsgId() {
        return taskMsgId;
    }

    public void setTaskMsgId(String taskMsgId) {
        this.taskMsgId = taskMsgId;
    }

    public String getCard_content() {
        return card_content;
    }

    public void setCard_content(String card_content) {
        this.card_content = card_content;
    }

    public JSONObject getCard_media() {
        return card_media;
    }

    public void setCard_media(JSONObject card_media) {
        this.card_media = card_media;
    }

    public int getCard_type() {
        return card_type;
    }

    public void setCard_type(int card_type) {
        this.card_type = card_type;
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + isComplete + taskMsgId + card_content + card_media + card_type;
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && IMFile.TYPE_CARD.equals(format) && !TextUtil.isEmpty(card_content)
                && IMCardMedia.hasType(card_type);
    }

    @Override
    public boolean checkCanSend() {
        if (!super.checkCanSend()) return false;
        if (isComplete == 1) {
            IMCardMedia cardMedia = IMCardMedia.parse(card_media);
            return cardMedia.isUploaded();
        } else {
            return true;
        }
    }
}
