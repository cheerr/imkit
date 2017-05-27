package com.conglai.leankit.model.message;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;

/**
 * Created by chenwei on 16/8/29.
 */

@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_READ_CALLBACK
)
public class IMReadCallBackMessage extends IMCustomMessage {

    @AVIMMessageField(
            name = LeanArgs.RECEIPT_MSG_ID
    )
    String rec_id;
    @AVIMMessageField(
            name = LeanArgs.RECEIPT_MSG_DATE
    )
    long rec_date;
    @AVIMMessageField(
            name = LeanArgs.RECEIPT_MSG_TYPE
    )
    int rec_type;

    public static final Creator<IMReadCallBackMessage> CREATOR = new AVIMMessageCreator<>(IMReadCallBackMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMReadCallBackMessage() {
        super();
    }

    public String getRec_id() {
        return rec_id;
    }

    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }

    public long getRec_date() {
        return rec_date;
    }

    public void setRec_date(long rec_date) {
        this.rec_date = rec_date;
    }

    public int getRec_type() {
        return rec_type;
    }

    public void setRec_type(int rec_type) {
        this.rec_type = rec_type;
    }


    @Override
    public boolean checkArgs() {
        return super.checkArgs() && !TextUtils.isEmpty(rec_id);
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend();
    }
}
