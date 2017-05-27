package com.conglai.leankit.model.message;

import com.conglai.leankit.model.message.file.IMAudio;
import com.conglai.leankit.model.message.file.IMCardMedia;
import com.conglai.leankit.model.message.file.IMFile;
import com.conglai.leankit.model.message.file.IMGif;
import com.conglai.leankit.model.message.file.IMHalfPiTu;
import com.conglai.leankit.model.message.file.IMPhoto;
import com.conglai.leankit.model.message.file.IMVideo;

import java.util.HashSet;

/**
 * Created by chenwei on 16/7/13.
 */

/**
 * Message生成工厂,所有的message都最好从这里new出来
 */
public class MessageFactory {

    private static HashSet<Integer> supportTypes = new HashSet<>();
    //一旦上线就不能改!!!!!!!
    public static final int AVIMMessageType_UNSUPPORT = 0, AVIMMessageType_TEXT = 1, AVIMMessageType_IMAGE = 2,
            AVIMMessageType_AUDIO = 3, AVIMMessageType_LOCATION = 5, AVIMMessageType_VIDEO = 6, AVIMMessageType_READ_CALLBACK = 7,
            AVIMMessageType_GIF_EMOJI = 8, AVIMMessageType_PiTu = 9, AVIMMessageType_CARD = 10;


    static {
        supportTypes.add(AVIMMessageType_TEXT);
        supportTypes.add(AVIMMessageType_IMAGE);
        supportTypes.add(AVIMMessageType_AUDIO);
        supportTypes.add(AVIMMessageType_LOCATION);
        supportTypes.add(AVIMMessageType_VIDEO);
        supportTypes.add(AVIMMessageType_GIF_EMOJI);
        supportTypes.add(AVIMMessageType_PiTu);
        supportTypes.add(AVIMMessageType_CARD);
    }

    public static int getSupportTypes() {
        return supportTypes.size() + 2;
    }

    /**
     * 是否包含type
     *
     * @param type
     * @return
     */
    public static int obtainType(int type) {
        if (supportTypes.contains(type)) {
            return type;
        }
        return AVIMMessageType_UNSUPPORT;
    }

    /**
     * 创建文本消息
     *
     * @param text
     * @return
     */
    public static IMTextMessage createTextMessage(String text) {
        IMTextMessage message = new IMTextMessage();
        message.setText(text);
        return message;
    }

    /**
     * 创建文本消息
     *
     * @param messageId
     * @return
     */
    public static IMReadCallBackMessage createReadCallBackMessage(String messageId, int messageType) {
        IMReadCallBackMessage message = new IMReadCallBackMessage();
        message.setRec_id(messageId);
        message.setRec_type(messageType);
        message.setRec_date(System.currentTimeMillis());
        message.setHide(1);
        return message;
    }

    /**
     * 创建图片消息
     *
     * @param photo
     * @return
     */
    public static IMImageMessage createImageMessage(IMPhoto photo) {
        IMImageMessage message = new IMImageMessage(IMFile.TYPE_IMG);
        message.setKey(photo.getKey());
        message.setSource(photo.getSource());
        message.setWidth(photo.getWidth());
        message.setHeight(photo.getHeight());
        return message;
    }

    /**
     * 创建图片消息
     *
     * @param video
     * @return
     */
    public static IMVideoMessage createVideoMessage(IMVideo video) {
        IMVideoMessage message = new IMVideoMessage(IMFile.TYPE_VIDEO);
        message.setKey(video.getKey());
        message.setSource(video.getSource());
        message.setWidth(video.getWidth());
        message.setHeight(video.getHeight());
        message.setDuration(video.getDuration());
        message.setThumb_key(video.getThumb_key());
        message.setThumb_source(video.getThumb_sourceAddr());
        return message;
    }

    /**
     * 创建语音消息
     *
     * @param audio
     * @return
     */
    public static IMAudioMessage createAudioMessage(IMAudio audio) {
        IMAudioMessage message = new IMAudioMessage(IMFile.TYPE_AUDIO);
        message.setKey(audio.getKey());
        message.setSource(audio.getSource());
        message.setDuration(audio.getDuration());
        return message;
    }


    /**
     * 创建位置消息
     *
     * @param latitude
     * @param longitude
     * @param address
     * @return
     */
    public static IMLocationMessage createLocationMessage(double latitude, double longitude, String address) {
        IMLocationMessage message = new IMLocationMessage();
        message.setLatitude(latitude);
        message.setLongitude(longitude);
        message.setAddress(address);
        return message;
    }

    /**
     * 创建GIF表情消息
     *
     * @param imGif
     * @return
     */
    public static IMGifEmojiMessage createGifEmojiMessage(IMGif imGif) {
        IMGifEmojiMessage message = new IMGifEmojiMessage();
        message.setGif_expression(imGif.getGifExpression());
        message.setGif_key(imGif.getGifKey());
        message.setGif_name(imGif.getGifName());
        message.setGif_pck(imGif.getGifPck());
        return message;
    }

    public static IMPiTuMessage createPiTuMessage(int complete, String completeImg, String completeSource, IMHalfPiTu left, IMHalfPiTu right) {
        if (left == null) left = new IMHalfPiTu();
        if (right == null) right = new IMHalfPiTu();
        IMPiTuMessage message = new IMPiTuMessage(IMFile.TYPE_PiTu);
        message.setLeft(left.toJson());
        message.setRight(right.toJson());
        message.setIsComplete(complete);
        message.setCompleteImg(completeImg);
        message.setCompleteSource(completeSource);
        return message;
    }

    public static IMCardMessage createCardMessage(int complete, int card_type, String card_content, IMCardMedia imCardMedia) {
        if (imCardMedia == null) imCardMedia = new IMCardMedia();
        IMCardMessage message = new IMCardMessage(IMFile.TYPE_CARD);
        message.setIsComplete(complete);
        message.setCard_type(card_type);
        message.setCard_content(card_content);
        message.setCard_media(imCardMedia.toJson());
        return message;
    }
}
