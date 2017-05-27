package com.conglai.leankit.model.message.file;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by chenwei on 16/9/20.
 */

public class IMHalfPiTu implements Serializable {

    private int status;
    private String image;
    private String sourceAddr;
    private String location;
    private double date;

    public static IMHalfPiTu parse(JSONObject jsonObject) {
        if (jsonObject == null) return new IMHalfPiTu();
        return JSON.parseObject(jsonObject.toString(), IMHalfPiTu.class);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("image", image);
        jsonObject.put("sourceAddr", sourceAddr);
        if (TextUtils.isEmpty(image) || !image.startsWith("bg_template")) {
            jsonObject.put("location", location);
            jsonObject.put("date", smallDate(date));
        }
        return jsonObject;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDate() {
        return bigDate(date);
    }

    public void setDate(double date) {
        this.date = smallDate(date);
    }

    /**
     * 缩小到10位数的double
     *
     * @param time
     * @return
     */
    private double smallDate(double time) {
        if (time == 0) return 0;
        time = Math.abs(time);
        while (time > 1e10) {
            time /= 10.0;
        }
        return time;
    }

    /**
     * 扩展到13位数的long
     *
     * @param time
     * @return
     */
    private long bigDate(double time) {
        if (time == 0) return 0;
        time = Math.abs(time);
        while (time < 1e12) {
            time *= 10.0;
        }
        return (long) time;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }


}
