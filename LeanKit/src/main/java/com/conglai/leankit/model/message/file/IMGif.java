package com.conglai.leankit.model.message.file;

import java.io.Serializable;

/**
 * Created by chenwei on 16/9/11.
 */

public class IMGif implements Serializable {

    private String gifKey;
    private String gifExpression;
    private String gifName;
    private String gifPck;

    public String getGifKey() {
        return gifKey;
    }

    public void setGifKey(String gifKey) {
        this.gifKey = gifKey;
    }

    public String getGifExpression() {
        return gifExpression;
    }

    public void setGifExpression(String gifExpression) {
        this.gifExpression = gifExpression;
    }

    public String getGifName() {
        return gifName;
    }

    public void setGifName(String gifName) {
        this.gifName = gifName;
    }

    public String getGifPck() {
        return gifPck;
    }

    public void setGifPck(String gifPck) {
        this.gifPck = gifPck;
    }
}
