package com.conglai.leankit.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;
import com.conglai.leankit.util.TextUtil;

/**
 * Created by chenwei on 16/7/13.
 */

public class IMUser implements Parcelable {

    private String userId;
    private String photo;
    private String nickName;
    private String remark;

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public IMUser(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShowName() {
        if (!TextUtil.isEmpty(remark))
            return remark;
        if (!TextUtil.isEmpty(nickName))
            return nickName;
        return "TA";
    }

    public IMUser() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.photo);
        dest.writeString(this.nickName);
        dest.writeString(this.remark);
    }

    protected IMUser(Parcel in) {
        this.userId = in.readString();
        this.photo = in.readString();
        this.nickName = in.readString();
        this.remark = in.readString();
    }

    public static final Creator<IMUser> CREATOR = new Creator<IMUser>() {
        @Override
        public IMUser createFromParcel(Parcel source) {
            return new IMUser(source);
        }

        @Override
        public IMUser[] newArray(int size) {
            return new IMUser[size];
        }
    };
}
