package com.conglai.leankit.model.message;

import com.avos.avoscloud.im.v2.AVIMMessageCreator;
import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;

/**
 * Created by chenwei on 16/7/22.
 */
@AVIMMessageType(
        type = MessageFactory.AVIMMessageType_LOCATION
)
public class IMLocationMessage extends IMCustomMessage {
    @AVIMMessageField(
            name = LeanArgs.ADDRESS
    )
    private String address;
    @AVIMMessageField(
            name = LeanArgs.LATITUDE
    )
    private double latitude;
    @AVIMMessageField(
            name = LeanArgs.LONGITUDE
    )
    private double longitude;

    public static final Creator<IMLocationMessage> CREATOR = new AVIMMessageCreator<>(IMLocationMessage.class);

    /**
     * 构造请采用MessageFactory构造工厂
     */
    IMLocationMessage() {
        super();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getExtraStr() {
        return super.getExtraStr() + latitude + longitude + address;
    }

    @Override
    public boolean checkArgs() {
        return super.checkArgs() && Math.abs(latitude) > 0.1 && Math.abs(latitude) < 90
                && Math.abs(longitude) > 0.1 && Math.abs(longitude) < 180;
    }

    @Override
    public boolean checkCanSend() {
        return super.checkCanSend();
    }
}
