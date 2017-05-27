package com.conglai.leankit.engine.imageloader;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.conglai.dblib.util.Utils;

/**
 * Created by chenwei on 16/7/25.
 */

public class ImgOpt implements Parcelable {

    private String key;
    private String url;
    private String path;

    private int defaultImg;

    private int sizeType = 0;//大小等级,0最小,1为一个等级

    public ImgOpt ofKey(String key) {
        this.key = key;
        return this;
    }

    public ImgOpt ofPath(String path) {
        this.path = path;
        return this;
    }

    public ImgOpt ofUrl(String url) {
        this.url = url;
        return this;
    }

    public ImgOpt ofDefaultImage(@DrawableRes int defaultImg) {
        this.defaultImg = defaultImg;
        return this;
    }

    public int getDefaultImg() {
        return defaultImg;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public int getSizeType() {
        return sizeType;
    }

    public void setSizeType(int sizeType) {
        this.sizeType = sizeType;
    }

    public ImgOpt() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.url);
        dest.writeString(this.path);
        dest.writeInt(this.defaultImg);
        dest.writeInt(this.sizeType);
    }

    protected ImgOpt(Parcel in) {
        this.key = in.readString();
        this.url = in.readString();
        this.path = in.readString();
        this.defaultImg = in.readInt();
        this.sizeType = in.readInt();
    }

    public static final Creator<ImgOpt> CREATOR = new Creator<ImgOpt>() {
        @Override
        public ImgOpt createFromParcel(Parcel source) {
            return new ImgOpt(source);
        }

        @Override
        public ImgOpt[] newArray(int size) {
            return new ImgOpt[size];
        }
    };

    @Override
    public String toString() {
        return Utils.toStringDo(this);
    }
}
