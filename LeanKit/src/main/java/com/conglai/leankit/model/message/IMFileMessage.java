package com.conglai.leankit.model.message;

import android.support.annotation.NonNull;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.conglai.leankit.util.TextUtil;

/**
 * Created by chenwei on 16/7/13.
 */

public class IMFileMessage extends IMCustomMessage {


    @AVIMMessageField(
            name = LeanArgs.FORMAT
    )
    String format;

    public static final Creator<IMFileMessage> CREATOR = new AVIMMessageCreator<>(IMFileMessage.class);


    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMFileMessage(@NonNull String format) {
        super();
        this.format = format;
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + format;
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && !TextUtil.isEmpty(format);
    }
}
