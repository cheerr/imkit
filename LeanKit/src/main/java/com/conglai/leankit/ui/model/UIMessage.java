package com.conglai.leankit.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.conglai.leankit.ui.config.ConversationConfig;

/**
 * Created by chenwei on 16/7/21.
 */

public class UIMessage implements Parcelable {

    private boolean showPortrait = ConversationConfig.showPortrait;//是否显示头像
    private boolean hide = ConversationConfig.hide; //是否显示

    public UIMessage() {

    }

    public boolean isShowPortrait() {
        return showPortrait;
    }

    public void setShowPortrait(boolean showPortrait) {
        this.showPortrait = showPortrait;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(showPortrait ? (byte) 1 : (byte) 0);
        dest.writeByte(hide ? (byte) 1 : (byte) 0);
    }

    protected UIMessage(Parcel in) {
        this.showPortrait = in.readByte() != 0;
        this.hide = in.readByte() != 0;
    }

    public static final Creator<UIMessage> CREATOR = new Creator<UIMessage>() {
        @Override
        public UIMessage createFromParcel(Parcel source) {
            return new UIMessage(source);
        }

        @Override
        public UIMessage[] newArray(int size) {
            return new UIMessage[size];
        }
    };
}
