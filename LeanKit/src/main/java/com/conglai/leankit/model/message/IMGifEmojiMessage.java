package com.conglai.leankit.model.message;

import android.os.Parcelable;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.conglai.leankit.model.message.file.IMGif;

/**
 * Created by chenwei on 16/9/11.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_GIF_EMOJI
)
public class IMGifEmojiMessage extends IMCustomMessage {

    @AVIMMessageField(
            name = LeanArgs.GIF_KEY
    )
    String gif_key;
    @AVIMMessageField(
            name = LeanArgs.GIF_EXP
    )
    String gif_expression;
    @AVIMMessageField(
            name = LeanArgs.GIF_NAME
    )
    String gif_name;
    @AVIMMessageField(
            name = LeanArgs.GIF_PCK
    )
    String gif_pck;
    public static final Parcelable.Creator<IMGifEmojiMessage> CREATOR = new AVIMMessageCreator<>(IMGifEmojiMessage.class);


    public IMGif getGif() {
        IMGif imGif = new IMGif();
        imGif.setGifPck(gif_pck);
        imGif.setGifExpression(gif_expression);
        imGif.setGifName(gif_name);
        imGif.setGifKey(gif_key);
        return imGif;
    }

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMGifEmojiMessage() {
        super();
    }

    public String getGif_key() {
        return gif_key;
    }

    public void setGif_key(String gif_key) {
        this.gif_key = gif_key;
    }

    public String getGif_expression() {
        return gif_expression;
    }

    public void setGif_expression(String gif_expression) {
        this.gif_expression = gif_expression;
    }

    public String getGif_name() {
        return gif_name;
    }

    public void setGif_name(String gif_name) {
        this.gif_name = gif_name;
    }

    public String getGif_pck() {
        return gif_pck;
    }

    public void setGif_pck(String gif_pck) {
        this.gif_pck = gif_pck;
    }


    @Override
    public boolean checkArgs() {
        return super.checkArgs() && !TextUtils.isEmpty(gif_key);
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend();
    }


    @Override
    public String getExtraStr() {
        return super.getExtraStr() + getGif();
    }
}
